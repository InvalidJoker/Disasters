package de.joker.disasters.disasters

import de.joker.disasters.StateManager
import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.extensions.broadcast
import org.bukkit.Bukkit

class AcidRainDisaster : Disaster(
    name = "Acid Rain",
    explanation = "All players are affected by acid rain! You will take damage over time!"
) {
    override fun start() {
        val world = Bukkit.getWorlds().firstOrNull() ?: return

        world.setStorm(true)
        world.weatherDuration = 12000 // 10 minutes in ticks

        broadcast("Acid rain starts falling from the sky! All players will take damage over time!")
    }

    override fun stop() {
        val world = Bukkit.getWorlds().firstOrNull() ?: return
        world.setStorm(false)
        world.weatherDuration = 0 // Stop the rain immediately

        broadcast("The acid rain has stopped. You are no longer taking damage.")
    }

    override fun second() {
        val players = StateManager.players.mapNotNull { Bukkit.getPlayer(it) }

        players.forEach { player ->
            if (player.isInRain) player.damage(1.0) // Deal 1 damage every second while in acid rain
        }

    }
}