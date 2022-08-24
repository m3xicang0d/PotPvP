package net.frozenorb.potpvp.game.match.spectator;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.spectator.listener.SpectatorListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectatorHandler {

    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(new SpectatorListener(), PotPvPSI.getInstance());
    }

    public boolean isSpectating(Player player) {
        return PotPvPSI.getInstance().getMatchHandler().isSpectatingMatch(player);
    }
}
