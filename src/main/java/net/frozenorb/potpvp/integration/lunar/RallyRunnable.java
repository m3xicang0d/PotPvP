package net.frozenorb.potpvp.integration.lunar;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class RallyRunnable extends BukkitRunnable {

    public Match match;

    @Override
    public void run() {
        for(MatchTeam team : match.getTeams()) {
            team.getRallys().keySet().forEach(uuid -> {
                RallyPoint point = team.getRallys().get(uuid);
                if(point.expired()) {
                    team.removePoint(point);
                    Player owner = point.getPlayer();
                    owner.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("POINT.EXPIRED")));
                }
            });
        }
    }
}
