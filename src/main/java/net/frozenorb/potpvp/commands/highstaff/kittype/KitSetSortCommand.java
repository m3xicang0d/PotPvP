package net.frozenorb.potpvp.commands.highstaff.kittype;

import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class KitSetSortCommand {

    @Command(names = {"kittype setsort"}, permission = "op", description = "Sets a kit-type's sort")
    public static void execute(Player player, @Param(name = "kittype") KitType kitType, @Param(name = "sort") int sort) {
        kitType.setSort(sort);
        kitType.saveAsync();

        KitType.getAllTypes().sort(Comparator.comparing(KitType::getSort));

        player.sendMessage(ChatColor.GREEN + "You've updated this kit-type's sort.");
    }

}
