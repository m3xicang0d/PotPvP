package net.frozenorb.potpvp.commands.highstaff.kittype;

import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetDisplayColorCommand {

    @Command(names = {"kittype setdisplaycolor"}, permission = "op", description = "Sets a kit-type's display color")
    public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "displayColor", wildcard = true) String color) {
        kitType.setDisplayColor(ChatColor.valueOf(color.toUpperCase().replace(" ", "_")));
        kitType.saveAsync();

        player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's display color.");
    }

}
