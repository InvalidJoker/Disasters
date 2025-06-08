package de.joker.disasters.disasters

import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.coroutines.taskRun
import de.joker.kutils.paper.coroutines.taskRunLater
import de.joker.kutils.paper.extensions.broadcast

class FloodDisaster : Disaster(
    name = "Flood",
    explanation = "A flood is a natural disaster that occurs when water overflows onto land that is normally dry. It can cause significant damage to property, infrastructure, and the environment.",
    canBeFirstDisaster = false
) {
    var currentGameSecond = 0.0
    var floodSpeed = 20.0
    var floodHeight = 50 // Height of the flood in blocks

    override fun start() {
        currentGameSecond = 0.0

        taskRunLater(
            delay = 4 * 20, // 40 seconds in ticks
        ) {
            // 0 until floodHeight
            for (y in 0 until floodHeight) {
                floodHeight(y)
            }

            // Notify players about the flood
            broadcast("A flood has started! Stay safe and find higher ground!")
        }
    }

    override fun stop() {
        currentGameSecond = 0.0
        floodSpeed = 20.0
    }

    override fun second() {
        currentGameSecond += 1.0

        if (currentGameSecond >= floodSpeed) {
            currentGameSecond = 0.0
            floodSpeed = (floodSpeed * 0.90).coerceAtLeast(5.0)

            // Increase the flood height by 1 block every second
            if (floodHeight < 100) { // Limit the flood height to 100 blocks
                floodHeight++
                broadcast("The flood is rising! Current height: $floodHeight blocks")
            } else {
                broadcast("The flood has reached its maximum height of 100 blocks!")
            }
        }
    }

    fun floodHeight(y: Int) {
        val world = org.bukkit.Bukkit.getWorlds().firstOrNull() ?: return

        val centerX = world.spawnLocation.blockX
        val centerZ = world.spawnLocation.blockZ
        taskRun {
            for (x in (centerX - 25)..(centerX + 25)) {
                for (z in (centerZ - 25)..(centerZ + 25)) {
                    val block = world.getBlockAt(x, y, z)
                    if (block.type.isAir) {
                        block.setType(org.bukkit.Material.WATER, false)
                    }
                }
            }
        }
    }
}