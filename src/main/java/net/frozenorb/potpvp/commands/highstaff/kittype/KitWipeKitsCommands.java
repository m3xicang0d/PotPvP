package net.frozenorb.potpvp.commands.highstaff.kittype;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KitWipeKitsCommands {

    @Command(names = "kit wipeKits Type", permission = "op", hidden = true)
    public static void kitWipeKitsType(Player sender, @Param(name = "kit type") KitType kitType) {
        int modified = PotPvPSI.getInstance().getKitHandler().wipeKitsWithType(kitType);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + modified + " " + kitType.getDisplayName() + " kits.");
        sender.sendMessage(ChatColor.GRAY + "^ We would have a proper count here if we ran recent versions of MongoDB");
    }

    @Command(names = "kit wipeKits Player", permission = "op", hidden = true)
    public static void kitWipeKitsPlayer(Player sender, @Param(name = "target") UUID target) {
        PotPvPSI.getInstance().getKitHandler().wipeKitsForPlayer(target);
        sender.sendMessage(ChatColor.YELLOW + "Wiped " + PotPvPSI.getInstance().getUuidCache().name(target) + "'s kits.");
    }
}