package de.joker.disasters.commands

import de.joker.disasters.StateManager
import de.joker.disasters.common.GameState
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor

object StartCommand {
    val command = commandTree("start") {
        playerExecutor { sender, _ ->
            StateManager.startGame()
        }
    }
}