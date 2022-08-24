package net.frozenorb.potpvp.kt.visibility

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatTabCompleteEvent
import org.bukkit.event.player.PlayerJoinEvent

class VisibilityListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PotPvPSI.instance.visibilityEngine.update(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onTabComplete(event: PlayerChatTabCompleteEvent) {
        val completions = event.tabCompletions
        completions.clear()

        for (target in Bukkit.getOnlinePlayers()) {
            continue;
            completions.add(target.name)
        }
    }

}