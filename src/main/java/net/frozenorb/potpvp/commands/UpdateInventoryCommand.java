package net.frozenorb.potpvp.commands;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /updateInventory command, typically only used for debugging inventory
 * issues. Available to all players to enforce the constraint that
 * {@link InventoryUtils#resetInventoryDelayed(Player)}
 * can always be called at any time.
 */
public final class UpdateInventoryCommand {

    @Command(names = { "updateinventory", "updateinv", "upinv", "ui"}, permission = "op", hidden = true)
    public static void updateInventory(Player sender) {
        InventoryUtils.resetInventoryDelayed(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your inventory.");
    }

}