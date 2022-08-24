package net.frozenorb.potpvp.util.potpvp;

import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import org.bukkit.Bukkit;

@Getter
public class PotPvPCache implements Runnable {

    public int onlineCount = 0;
    public int fightsCount = 0;
    public int queuesCount = 0;

    @Override
    public void run() {
        onlineCount = Bukkit.getOnlinePlayers().size();
        fightsCount = PotPvPSI.getInstance().getMatchHandler().countPlayersPlayingInProgressMatches();
        queuesCount = PotPvPSI.getInstance().getQueueHandler().getQueuedCount();
    }
}
