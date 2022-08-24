package net.frozenorb.potpvp.game.postmatchinv.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.game.match.event.MatchTerminateEvent;
import net.frozenorb.potpvp.game.postmatchinv.PostMatchInvHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PostMatchInvGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();
        postMatchInvHandler.recordMatch(event.getMatch());
    }

    // remove 'old' post match data when their match starts
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                postMatchInvHandler.removePostMatchData(member);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();
        UUID playerUuid = event.getPlayer().getUniqueId();

        postMatchInvHandler.removePostMatchData(playerUuid);
    }

}