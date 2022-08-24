package net.frozenorb.potpvp.commands.staff;

import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class SilentCommand {

    @Command(names = {"silent"}, permission = "potpvp.silent")
    public static void silent(Player sender) {
            sender.sendMessage(ChatColor.GREEN + "You are visible to other players, type /vanish to become invisible");
        }
    }