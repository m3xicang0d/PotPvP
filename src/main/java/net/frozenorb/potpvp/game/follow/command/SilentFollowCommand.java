package net.frozenorb.potpvp.game.follow.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.command.LeaveCommand;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SilentFollowCommand {

    @Command(names = {"silentfollow", "tp"}, permission = "potpvp.silent")
    public static void silentfollow(Player sender, @Param(name = "target") Player target) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        if (matchHandler.isPlayingMatch(sender)) {
            sender.sendMessage(ChatColor.RED + "You can't use this command while on a match.");
            return;
        }
        if (PotPvPSI.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }

}
