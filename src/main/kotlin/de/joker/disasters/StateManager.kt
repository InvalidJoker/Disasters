package de.joker.disasters

import de.joker.disasters.common.Disaster
import de.joker.disasters.common.GameState
import de.joker.disasters.common.setSpectatorMode
import de.joker.disasters.disasters.*
import de.joker.kutils.core.extensions.getLogger
import de.joker.kutils.paper.coroutines.taskRunTimer
import de.joker.kutils.paper.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

object StateManager {
    val availableDisasters = mutableListOf(
        FloodDisaster(),
        AnvilRainDisaster(),
        BlindnessDisaster(),
        AcidRainDisaster(),
        SwapperDisaster(),
        InvasionDisaster(),
        PvPDisaster(),
        SwapperDisaster()
    )

    val currentDisasters = mutableListOf<Disaster>()

    var countDown = 301 // 5 minutes
    var disastersTime = 45 // 45 seconds between disasters
    val players = mutableListOf<UUID>()
    var state = GameState.LOBBY

    private lateinit var scheduler: BukkitTask

    fun end() {
        scheduler.cancel()

        getLogger().info("Game ended")

        broadcast("Game ended! Thanks for playing!")
    }

    fun startGame() {
        state = GameState.INGAME

        countDown = 301 // Reset countdown to 5 minutes
        players.clear()
        players.addAll(Bukkit.getOnlinePlayers().map { it.uniqueId })

        // first disaster
        val firstDisaster = availableDisasters.filter { it.canBeFirstDisaster }.randomOrNull()
        if (firstDisaster == null) {
            getLogger().error("No available disasters to start the game")
            return
        }

        firstDisaster.start()
        currentDisasters.add(firstDisaster)
        getLogger().info("Game started with first disaster: ${firstDisaster.name}")

    }

    fun start() {
        if (::scheduler.isInitialized) {
            getLogger().info("Scheduler already running")
            return
        }

        scheduler = taskRunTimer(sync = false, period = 20) { // Run every second
            if (state != GameState.INGAME) return@taskRunTimer

            countDown--
            if (countDown <= 0) {
                currentDisasters.forEach { disaster ->
                    disaster.stop()
                    getLogger().info("Disaster ${disaster.name} stopped")
                }
                currentDisasters.clear()

                broadcast("Game over! All disasters have ended.")
                broadcast("${players.size} players survived the disasters (${players.joinToString(", ") { Bukkit.getPlayer(it)?.name ?: "Unknown" }})")

                players.forEach {
                    val player = Bukkit.getPlayer(it)
                    player?.setSpectatorMode()
                }

                scheduler.cancel()
            } else {
                currentDisasters.forEach { disaster ->
                    disaster.second()
                }

                if ((countDown % disastersTime).toLong() == 0L) {
                    val shouldDisable = currentDisasters.filter { it.disableOnNextDisaster }

                    shouldDisable.forEach { disaster ->
                        disaster.stop()
                        currentDisasters.remove(disaster)
                        getLogger().info("Disaster ${disaster.name} disabled for next disaster")
                    }

                    val nextDisaster = availableDisasters.filter { currentDisasters.none { c -> c.name == it.name } &&
                            (it.canBeFirstDisaster || currentDisasters.isNotEmpty()) &&
                            currentDisasters.none { c -> it.incompatibleWith.contains(c.name) } }
                        .randomOrNull()

                    if (nextDisaster == null) {
                            getLogger().info("No more disasters available")
                            broadcast("No more disasters available. Game over!")
                            countDown = 0
                            return@taskRunTimer
                    }

                    nextDisaster.start()
                    currentDisasters.add(nextDisaster)

                    getLogger().info("New disaster started: ${nextDisaster.name}")
                    broadcast("A new disaster has started: ${nextDisaster.name} - ${nextDisaster.explanation}")
                }
            }
        }
    }

    fun stop() {
        if (!::scheduler.isInitialized) {
            getLogger().info("Scheduler not running")
            return
        }
        scheduler.cancel()
        getLogger().info("Scheduler stopped")
    }

}
