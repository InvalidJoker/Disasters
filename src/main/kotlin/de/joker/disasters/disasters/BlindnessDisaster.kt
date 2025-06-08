package de.joker.disasters.disasters

import de.joker.disasters.StateManager
import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class BlindnessDisaster : Disaster(
    name = "Blindness",
    explanation = "All players are blinded! You can't see anything, be careful!",
    disableOnNextDisaster = true
) {
    override fun start() {
        broadcast("A thick fog envelops the land, blinding all players! Be careful, you can't see anything!")
        StateManager.players.forEach { player ->
            val onlinePlayer = Bukkit.getPlayer(player) ?: return@forEach
            onlinePlayer.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 2000 * 60, 1, true, false, false))
        }
    }

    override fun stop() {
        broadcast("The fog has lifted, you can see again!")
        StateManager.players.forEach { player ->
            val onlinePlayer = Bukkit.getPlayer(player) ?: return@forEach
            onlinePlayer.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }
}