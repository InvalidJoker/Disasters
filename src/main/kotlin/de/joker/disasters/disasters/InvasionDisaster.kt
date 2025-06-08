package de.joker.disasters.disasters

import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.coroutines.taskRunLater
import de.joker.kutils.paper.extensions.broadcast
import org.bukkit.Bukkit
import kotlin.random.Random

class InvasionDisaster : Disaster(
    name = "Invasion",
    explanation = "Monsters are invading the world! Beware of the hordes that will spawn and attack players!",
) {
    override fun start() {
        broadcast("The skies darken... Anvils begin to fall!")
    }

    override fun stop() {
        broadcast("The anvil rain has stopped. The ground is safe again.")
    }

    override fun second() {
        val world = Bukkit.getWorlds().firstOrNull() ?: return
        val centerX = world.spawnLocation.blockX
        val centerZ = world.spawnLocation.blockZ

        repeat(3) {

            val x = centerX + Random.nextInt(-25, 26)
            val z = centerZ + Random.nextInt(-25, 26)
            val y = world.maxHeight - 1

            val skeleton = Random.nextInt(3)

            val m = when (skeleton) {
                0 ->  world.spawnEntity(world.getBlockAt(x, y, z).location, org.bukkit.entity.EntityType.SKELETON)
                1 -> world.spawnEntity(world.getBlockAt(x, y, z).location, org.bukkit.entity.EntityType.ZOMBIE)
                2 -> world.spawnEntity(world.getBlockAt(x, y, z).location, org.bukkit.entity.EntityType.CREEPER)
                else -> world.spawnEntity(world.getBlockAt(x, y, z).location, org.bukkit.entity.EntityType.ZOMBIE)
            }

            m.isInvulnerable = true // Make the monster invulnerable for a short time

            taskRunLater(100) { // Delay to let the monster spawn
                m.isInvulnerable = false // Make the monster vulnerable after 5 seconds
            }
        }



    }
}