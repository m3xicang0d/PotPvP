package net.frozenorb.potpvp.listener.kits;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SkywarsListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Match match = PotPvPSI.getInstance().matchHandler.getMatchPlaying(player);
        if(match == null) return;
        if (!match.kitType.id.equalsIgnoreCase("skywars")) return;
        event.setCancelled(false);
    }

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Match match = PotPvPSI.getInstance().matchHandler.getMatchPlaying(player);
        if(match == null) return;
        if (!match.kitType.id.equalsIgnoreCase("skywars")) return;
        event.setCancelled(false);
    }
}