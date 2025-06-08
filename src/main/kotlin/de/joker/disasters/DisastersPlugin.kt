package de.joker.disasters

import de.joker.disasters.commands.StartCommand
import de.joker.disasters.listener.PlayerListener
import de.joker.kutils.paper.KPlugin
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig

class DisastersPlugin: KPlugin() {
    companion object {
        lateinit var instance: DisastersPlugin
    }

    override fun load() {
        instance = this
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true))
    }

    override fun startup() {
        CommandAPI.onEnable()

        StateManager.start()

        StartCommand
        PlayerListener

        logger.info("Disasters started successfully!")
    }

    override fun shutdown() {
        CommandAPI.onDisable()

        StateManager.stop()
    }
}