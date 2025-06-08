package de.joker.disasters.common

import org.bukkit.GameMode
import org.bukkit.entity.Player


fun Player.setSpectatorMode() {
    gameMode = GameMode.SPECTATOR
    allowFlight = true
    isFlying = true
    noDamageTicks = 0
    health = 20.0
    foodLevel = 20
    saturation = 20f
}

fun Player.setSurvivalMode() {
    gameMode = GameMode.SURVIVAL
    allowFlight = false
    isFlying = false
    noDamageTicks = 0
    health = 20.0
    foodLevel = 20
    saturation = 20f
}