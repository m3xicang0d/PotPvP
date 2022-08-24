package net.frozenorb.potpvp.player.rematch.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.event.MatchTerminateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RematchGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PotPvPSI.getInstance().getRematchHandler().registerRematches(event.getMatch());
    }

}