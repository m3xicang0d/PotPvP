package net.frozenorb.potpvp.game.follow.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UnfollowCommand {

    @Command(names = {"unfollow"}, permission = "")
    public static void unfollow(Player sender) {
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }

}