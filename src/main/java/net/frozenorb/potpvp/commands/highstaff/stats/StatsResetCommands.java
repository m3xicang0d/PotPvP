package net.frozenorb.potpvp.commands.highstaff.stats;

import com.google.common.base.Objects;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.util.menu.menus.ConfirmMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsResetCommands {
    public static String REDIS_PREFIX = "PotPvP:statsResetToken:";

    @Command(names = {"statsreset addtoken"}, permission = "op", async = true)
    public static void addToken(CommandSender sender, @Param(name = "player") String playerName, @Param(name = "amount") int amount) {
        UUID uuid = PotPvPSI.getInstance().uuidCache.uuid(playerName);

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + "Unable to locate '" + playerName + "'.");
            return;
        }

        addTokens(uuid, amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " token" + (amount == 1 ? "" : "s") + " to " + PotPvPSI.getInstance().getUuidCache().name(uuid) + ".");
    }

    @Command(names = {"statsreset"}, permission = "", async = true)
    public static void reset(Player sender) {
        int tokens = getTokens(sender.getUniqueId());
        if (tokens <= 0) {
            sender.sendMessage(ChatColor.RED + "You need at least one token to reset your stats.");
            return;
        }

        Bukkit.getScheduler().runTask(PotPvPSI.getInstance(), () -> {
            new ConfirmMenu("Stats reset", (reset) -> {
                if (!reset) {
                    sender.sendMessage(ChatColor.RED + "Stats reset aborted.");
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
                    PotPvPSI.getInstance().getEloHandler().resetElo(sender.getUniqueId());
                    removeTokens(sender.getUniqueId(), 1);
                    sender.sendMessage(ChatColor.GREEN + "Reset your stats! Used one token. " + tokens + " token" + (tokens == 1 ? "" : "s") + " left.");
                });

            }).openMenu(sender);
        });
    }

    public static int getTokens(UUID player) {
        return PotPvPSI.getInstance().redis.runBackboneRedisCommand((redis) -> {
            return Integer.valueOf(Objects.firstNonNull(redis.get(REDIS_PREFIX + player.toString()), "0"));
        });
    }

    public static void addTokens(UUID player, int amountBy) {
        PotPvPSI.getInstance().redis.runBackboneRedisCommand((redis) -> {
            redis.incrBy(REDIS_PREFIX + player.toString(), amountBy);
            return null;
        });
    }

    public static void removeTokens(UUID player, int amountBy) {
        PotPvPSI.getInstance().redis.runBackboneRedisCommand((redis) -> {
            redis.decrBy(REDIS_PREFIX + player.toString(), amountBy);
            return null;
        });
    }
}
