package net.frozenorb.potpvp.commands.player;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.lobby.menu.StatisticsMenu;
import org.bukkit.entity.Player;

public class LeaderBoardCommand {

    @Command(names = {"leaderboard", "stats", "leaderboards"})
    public static void statistics(Player sender) {
        (new StatisticsMenu()).openMenu(sender);
    }
}
