package de.joker.disasters.disasters

import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.coroutines.taskRun
import de.joker.kutils.paper.coroutines.taskRunLater
import de.joker.kutils.paper.event.listen
import de.joker.kutils.paper.event.register
import de.joker.kutils.paper.event.unregister
import de.joker.kutils.paper.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.FallingBlock
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.util.Vector
import kotlin.random.Random

class AnvilRainDisaster : Disaster(
    name = "Anvil Rain",
    explanation = "Anvils are falling from the sky! Take cover or risk getting crushed. Blocks might also break from the impact."
) {
    override fun start() {
        fallBlockEvent.register()
        broadcast("The skies darken... Anvils begin to fall!")
    }

    override fun stop() {
        fallBlockEvent.unregister()
        broadcast("The anvil rain has stopped. The ground is safe again.")
    }

    override fun second() {
        dropAnvils()
    }

    private fun dropAnvils() {
        val world = Bukkit.getWorlds().firstOrNull() ?: return
        val centerX = world.spawnLocation.blockX
        val centerZ = world.spawnLocation.blockZ

        repeat(3) {
            val x = centerX + Random.nextInt(-25, 26)
            val z = centerZ + Random.nextInt(-25, 26)
            val y = world.maxHeight - 1

            val block = world.getBlockAt(x, y, z)
            if (block.type.isAir) {
                taskRun {
                    val fallingAnvil = world.spawn(block.location, FallingBlock::class.java)
                    fallingAnvil.blockData = Material.ANVIL.createBlockData()
                    fallingAnvil.velocity = Vector(0.0, -1.0, 0.0)
                    fallingAnvil.dropItem = false
                    fallingAnvil.setHurtEntities(true)
                }
            }
        }
    }

    val fallBlockEvent = listen<EntityChangeBlockEvent>(register = false) { event ->
        if (event.entity is FallingBlock) {
            val fallingBlock = event.entity as FallingBlock
            if (fallingBlock.blockData.material == Material.ANVIL) {
                val location = event.block.location

                taskRunLater(1) {
                    // Check if the block below is solid and not bedrock
                    val belowBlock = location.clone().subtract(0.0, 1.0, 0.0).block
                    if (belowBlock.type.isSolid && belowBlock.type != Material.BEDROCK) {
                        belowBlock.type = Material.AIR // Break the block below
                    }
                }
            }
        }
    }
}