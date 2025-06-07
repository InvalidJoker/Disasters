package de.joker.disasters

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
    }

    override fun shutdown() {
        CommandAPI.onDisable()
    }
}