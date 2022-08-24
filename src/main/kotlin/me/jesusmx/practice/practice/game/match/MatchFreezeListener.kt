package me.jesusmx.practice.practice.game.match

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

class MatchFreezeListener : Listener {

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player) ?: return
        if(!match.isFreezed) return
        event.isCancelled = true
    }

   /* @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player) ?: return
        if (!match.isFreezed) return
        event.isCancelled = true
    }*/

    @EventHandler
    fun onPlayerInteract(event : PlayerInteractEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player) ?: return
        if (!match.isFreezed) return
        event.isCancelled = true
    }

    @EventHandler //Only in BuildUHC
    fun onPlayerPlaceBlock(event : BlockPlaceEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player) ?: return
        if (!match.isFreezed) return
        event.isCancelled = true
    }

    @EventHandler //Only in BuildUHC
    fun onPlayerBreakBlock(event : BlockBreakEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player) ?: return
        if (!match.isFreezed) return
        event.isCancelled = true
    }
}