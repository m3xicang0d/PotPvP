package net.frozenorb.potpvp.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class DurabilityFixListener implements Listener {

    @EventHandler
    public void onDurLoss(PlayerItemDamageEvent event) {
        int damage = event.getDamage();
        event.setDamage((int) Math.floor(damage / 3));
    }
}
