package net.frozenorb.potpvp.kt.nametag

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.event.player.PlayerJoinEvent

internal class NametagListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.setMetadata("starkNametag-LoggedIn", FixedMetadataValue(PotPvPSI.instance, true) as MetadataValue)
        PotPvPSI.instance.nametagEngine.initiatePlayer(event.player)
        PotPvPSI.instance.nametagEngine.reloadPlayer(event.player)
        PotPvPSI.instance.nametagEngine.reloadOthersFor(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.removeMetadata("starkNametag-LoggedIn", PotPvPSI.instance)
        PotPvPSI.instance.nametagEngine.teamMap.remove(event.player.name)
    }
}