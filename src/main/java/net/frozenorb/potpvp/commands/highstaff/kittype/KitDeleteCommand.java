package net.frozenorb.potpvp.commands.highstaff.kittype;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDeleteCommand {

    @Command(names = {"kittype delete"}, permission = "op", description = "Deletes an existing kit-type")
    public static void execute(Player player, @Param(name = "kittype") KitType kitType) {
        kitType.deleteAsync();
        KitType.getAllTypes().remove(kitType);
        PotPvPSI.getInstance().getQueueHandler().removeQueues(kitType);

        player.sendMessage(ChatColor.GREEN + "You've deleted the kit-type by the ID \"" + kitType.getId() + "\".");
    }

}
