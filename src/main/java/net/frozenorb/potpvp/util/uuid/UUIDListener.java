package net.frozenorb.potpvp.util.uuid;

import net.frozenorb.potpvp.PotPvPSI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

final class UUIDListener
        implements Listener {
    UUIDListener() {
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PotPvPSI.getInstance().uuidCache.update(event.getUniqueId(), event.getName());
    }
}

