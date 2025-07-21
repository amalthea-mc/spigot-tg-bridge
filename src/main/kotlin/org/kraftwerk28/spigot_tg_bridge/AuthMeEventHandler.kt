package org.kraftwerk28.spigot_tg_bridge

import fr.xephi.authme.events.LoginEvent
import fr.xephi.authme.events.LogoutEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class AuthMeEventHandler(
    private val plugin: Plugin,
    private val config: Configuration,
    private val tgBot: TgBot,
) : Listener {
    private val playersLoggedIn = HashMap<UUID, Boolean>()

    @EventHandler
    fun onPlayerLogin(event: LoginEvent) {
        if (!config.logLoginLogout) return
        val username = event.player.displayName.fullEscape()
        val text = config.loginString.replace("%username%", username)
        playersLoggedIn[event.player.uniqueId] = true
        sendMessage(text)
    }

    @EventHandler
    fun onPlayerLogout(event: LogoutEvent) {
        if (!config.logLoginLogout) return
        if (!playersLoggedIn.contains(event.player.uniqueId)) return
        val username = event.player.displayName.fullEscape()
        val text = config.leaveString.replace("%username%", username)
        playersLoggedIn.remove(event.player.uniqueId)
        sendMessage(text)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        if (!config.logLoginLogout) return
        if (!playersLoggedIn.contains(event.player.uniqueId)) return
        val username = event.player.displayName.fullEscape()
        val text = config.leaveString.replace("%username%", username)
        playersLoggedIn.remove(event.player.uniqueId)
        sendMessage(text)
    }

    private fun sendMessage(text: String, username: String? = null) {
        plugin.launch {
            tgBot.sendMessageToTelegram(text, username)
        }
    }
}