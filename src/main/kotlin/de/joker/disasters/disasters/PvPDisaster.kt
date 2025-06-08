package de.joker.disasters.disasters

import de.joker.disasters.StateManager
import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.extensions.actionBar
import de.joker.kutils.paper.extensions.broadcast
import de.joker.kutils.paper.extensions.onlinePlayers
import dev.fruxz.ascend.extension.isNull

class PvPDisaster : Disaster(
    name = "PvP",
    explanation = "All players are affected by PvP! You can attack and be attacked by other players!",
) {
    var half: Int? = null

    override fun start() {
        broadcast("The skies darken... Anvils begin to fall!")
        half = (StateManager.players.size / 2).coerceAtLeast(1)
    }

    override fun stop() {
        broadcast("The anvil rain has stopped. The ground is safe again.")
    }

    override fun second() {
        if (half.isNull) return

        val players = StateManager.players.mapNotNull { org.bukkit.Bukkit.getPlayer(it) }

        // if players.size is less than half, return
        if (players.size < half!!) {
            StateManager.currentDisasters.remove(this)
            stop()
            broadcast("PvP disaster has ended, half of the players are gone!")
        } else {
            onlinePlayers.forEach { player ->
                player.actionBar(
                    "${players.size - half!!} players are left to eliminate!"
                )
            }
        }

    }
}