package net.frozenorb.potpvp.player.elo.listener;

import com.google.common.base.Joiner;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.match.event.MatchEndEvent;
import net.frozenorb.potpvp.game.match.event.MatchTerminateEvent;
import net.frozenorb.potpvp.player.elo.EloCalculator;
import net.frozenorb.potpvp.player.elo.EloHandler;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

import static net.frozenorb.potpvp.game.match.MatchEndReason.DURATION_LIMIT_EXCEEDED;
import static net.frozenorb.potpvp.game.match.MatchEndReason.FORCEFULLY_TERMINATED;

public final class EloUpdateListener implements Listener {

    public static final String ELO_CHANGE_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&eElo Changes: &a%s +%d (%d) &c%s -%d (%d)");

    public final EloHandler eloHandler;
    public final EloCalculator eloCalculator;

    public EloUpdateListener(EloHandler eloHandler, EloCalculator eloCalculator) {
        this.eloHandler = eloHandler;
        this.eloCalculator = eloCalculator;
    }

    // we actually save elo when the match first ends but only
    // send messages when it terminates (when players go back to
    // the lobby)
    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        KitType kitType = match.getKitType();
        List<MatchTeam> teams = match.getTeams();

        if (!match.isRanked() || teams.size() != 2 || match.getWinner() == null
            || event.getMatch().getEndReason() == FORCEFULLY_TERMINATED
            || event.getMatch().getEndReason() == DURATION_LIMIT_EXCEEDED) {
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        EloCalculator.Result result = eloCalculator.calculate(
            eloHandler.getElo(winnerTeam.getAllMembers(), kitType),
            eloHandler.getElo(loserTeam.getAllMembers(), kitType)
        );

        eloHandler.setElo(winnerTeam.getAllMembers(), kitType, result.getWinnerNew());
        eloHandler.setElo(loserTeam.getAllMembers(), kitType, result.getLoserNew());

        match.setEloChange(result);
    }

    // see comment on onMatchEnd method
    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();
        EloCalculator.Result result = match.getEloChange();

        if (result == null) {
            return;
        }

        List<MatchTeam> teams = match.getTeams();
        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        String winnerStr;
        String loserStr;

        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {
            winnerStr = PotPvPSI.getInstance().getUuidCache().name(winnerTeam.getFirstMember());
            loserStr = PotPvPSI.getInstance().getUuidCache().name(loserTeam.getFirstMember());
        } else {
            winnerStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(winnerTeam.getAllMembers()));
            loserStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(loserTeam.getAllMembers()));
        }

        // we negate loser gain to convert negative gain to positive (which we prefix with - in the string)
        match.messageAll(String.format(ELO_CHANGE_MESSAGE, winnerStr, result.getWinnerGain(), result.getWinnerNew(), loserStr, -result.getLoserGain(), result.getLoserNew()));
    }

}