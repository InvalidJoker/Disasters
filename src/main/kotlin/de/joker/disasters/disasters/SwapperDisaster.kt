package de.joker.disasters.disasters

import de.joker.disasters.StateManager
import de.joker.disasters.common.Disaster
import de.joker.kutils.paper.extensions.broadcast

class SwapperDisaster : Disaster(
    name = "Swapper",
    explanation = "Swaps the positions of all players every 5 seconds! Be ready for unexpected changes!",
    disableOnNextDisaster = true,
) {
    var currentGameSecond = 0.0

    var currentRandomIndex = 0
    var randoms = mutableListOf<Int>()

    override fun start() {
        currentGameSecond = 0.0

        randoms.clear()
        val r = 10..30

        (0 until 100).forEach { _ ->
            randoms.add(r.random())
        }
    }

    override fun stop() {
        currentGameSecond = 0.0
        currentRandomIndex = 0
        randoms.clear()
    }

    override fun second() {
        currentGameSecond += 1.0

        if (currentGameSecond >= randoms[currentRandomIndex]) {
            currentGameSecond = 0.0
            currentRandomIndex = (currentRandomIndex + 1) % randoms.size
            swapPlayers()
        }
    }

    private fun swapPlayers() {
        val players = StateManager.players.mapNotNull { org.bukkit.Bukkit.getPlayer(it) }
        if (players.size < 2) {
            broadcast("Not enough players to swap positions!")
            return
        }

        // group into pairs
        val pairs = players.chunked(2)

        pairs.forEach { pair ->
            if (pair.size == 2) {
                val player1 = pair[0]
                val player2 = pair[1]

                // Swap positions
                val tempLocation = player1.location.clone()
                player1.teleport(player2.location)
                player2.teleport(tempLocation)

                broadcast("${player1.name} and ${player2.name} have swapped positions!")
            } else {
                // If there's an odd number of players, the last one stays in place
                broadcast("${pair[0].name} has no one to swap with!")
            }
        }
    }
}