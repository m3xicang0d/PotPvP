package net.frozenorb.potpvp.game.match.spectator.listener;

import net.frozenorb.potpvp.PotPvPSI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SpectatorListener implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!PotPvPSI.getInstance().getSpectatorHandler().isSpectating((Player) event.getEntity())) return;
        event.setCancelled(true);
    }
}
