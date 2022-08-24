package net.frozenorb.potpvp.game.postmatchinv.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.game.postmatchinv.PostMatchPlayer;
import net.frozenorb.potpvp.game.postmatchinv.menu.PostMatchMenu;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class CheckPostMatchInvCommand {

    @Command(names = {"checkPostMatchInv", "_"}, permission = "")
    public static void checkPostMatchInv(Player sender, @Param(name = "target") UUID target) {
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());

        if (players.containsKey(target)) {
            new PostMatchMenu(players.get(target)).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Data for " + PotPvPSI.getInstance().getUuidCache().name(target) + " not found.");
        }
    }

}