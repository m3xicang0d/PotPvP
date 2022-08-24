package net.frozenorb.potpvp.game.follow.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FollowCommand {

    @Command(names = {"follow"}, permission = "")
    public static void follow(Player sender, @Param(name = "target") Player target) {
        if (!PotPvPValidation.canFollowSomeone(sender, target)) {
            return;
        }

        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't follow yourself!");
            return;
        } else if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.hasPermission("kore.staff")) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators.");
                return;
            }
        }

        Game game = GameQueue.INSTANCE.getCurrentGame(target);
        if (game != null && game.getPlayers().contains(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is playing an event, type /play to watch!");
            return;
        }

        followHandler.getFollowing(sender).ifPresent(fo -> UnfollowCommand.unfollow(sender));

        if (matchHandler.isSpectatingMatch(sender)) {
            matchHandler.getMatchSpectating(sender).removeSpectator(sender);
        }

        followHandler.startFollowing(sender, target);
    }

}