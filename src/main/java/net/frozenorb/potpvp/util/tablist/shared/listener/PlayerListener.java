package net.frozenorb.potpvp.util.tablist.shared.listener;

import lombok.RequiredArgsConstructor;
import net.frozenorb.potpvp.util.tablist.shared.TabHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private TabHandler handler;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.handler.sendUpdate(event.getPlayer());
    }
}