package net.frozenorb.potpvp.commands.highstaff.kittype;

import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetDisplayNameCommand {

    @Command(names = {"kittype setdisplayname"}, permission = "op", description = "Sets a kit-type's display name")
    public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "displayName", wildcard = true) String displayName) {
        kitType.setDisplayName(displayName);
        kitType.saveAsync();

        player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display name.");
    }

}
