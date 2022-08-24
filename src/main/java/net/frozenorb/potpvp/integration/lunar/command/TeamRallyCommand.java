package net.frozenorb.potpvp.integration.lunar.command;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.integration.lunar.RallyPoint;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.entity.Player;

public class TeamRallyCommand {

    @Command(names={ "team rally", "t rally", "f rally", "faction rally", "fac rally", "team setrally", "t setrally", "f setrally", "faction setrally", "fac setrally" }, permission="")
    public static void rally(Player sender) {
        MatchHandler handler = PotPvPSI.getInstance().getMatchHandler();
        if(!handler.isPlayingMatch(sender)) {
            return;
        }
        if(PotPvPSI.getInstance().getPartyHandler().getParty(sender.getUniqueId()) == null) {
            return;
        }
        Match match = handler.getMatchPlaying(sender);
        MatchTeam team = match.getTeam(sender.getUniqueId());
        LCWaypoint lcWaypoint = new LCWaypoint("Rally", sender.getLocation().getBlockX(), sender.getLocation().getBlockY(), sender.getLocation().getBlockZ(), sender.getLocation().getWorld().getUID().toString(), -16776961, true, true);
        RallyPoint rallyPoint = new RallyPoint(sender, sender.getLocation(), lcWaypoint);
        team.rally(rallyPoint);
    }
}
