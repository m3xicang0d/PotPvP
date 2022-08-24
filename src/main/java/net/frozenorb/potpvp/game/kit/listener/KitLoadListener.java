package net.frozenorb.potpvp.game.kit.listener;

import net.frozenorb.potpvp.PotPvPSI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class KitLoadListener implements Listener {

    @EventHandler(priority=EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPSI.getInstance().getKitHandler().loadKits(event.getUniqueId());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPSI.getInstance().getKitHandler().unloadKits(event.getPlayer());
    }
}