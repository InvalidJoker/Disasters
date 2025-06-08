package de.joker.disasters.listener

import de.joker.disasters.StateManager
import de.joker.disasters.common.setSpectatorMode
import de.joker.kutils.paper.event.listen
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent

object PlayerListener {
    var pvpEnabled = false

    val onBlockBreak = listen<BlockBreakEvent> {
        it.isCancelled = true
    }

    val onPlace = listen<BlockPlaceEvent> {
        it.isCancelled = true
    }

    val onDamage = listen<EntityDamageByEntityEvent> {
        if (pvpEnabled) return@listen
        if (it.damager !is org.bukkit.entity.Player) return@listen
        if (it.entity !is org.bukkit.entity.Player) return@listen

        it.isCancelled = true
    }

    val onDie = listen<PlayerDeathEvent> {
        it.isCancelled = true
        it.player.spigot().respawn()

        it.player.health = it.entity.maxHealth

        it.player.foodLevel = 20

        StateManager.players.remove(it.entity.uniqueId)
        it.player.setSpectatorMode()

        if (StateManager.players.isEmpty()) {
            StateManager.end()
        } else {
            it.player.sendMessage("You have died and are now spectating the game. You can rejoin later.")
        }
    }
}