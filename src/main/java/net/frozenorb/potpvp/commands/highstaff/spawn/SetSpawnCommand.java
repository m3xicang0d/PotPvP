package net.frozenorb.potpvp.commands.highstaff.spawn;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * /setspawn command, updates spawn location
 * (spawn location is used when teleporting players to the lobby)
 * <p>
 * {@link org.bukkit.World#setSpawnLocation(int, int, int, float, float)}
 * is a custom method provided by PowerSpigot which stores yaw/pitch along
 * with x/y/z. See net.frozenorb:mspigot-api in pom.xml
 */
public final class SetSpawnCommand {

    @Command(names={"setspawn"}, permission="op")
    public static void setSpawn(Player sender) {
        FileConfiguration config = PotPvPSI.getInstance().getConfig();
        Location l = sender.getLocation();
        String world = l.getWorld().getName();
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        float yaw = l.getYaw();
        float pitch = l.getPitch();
        config.set("WORLD.SPAWN.WORLD", world);
        config.set("WORLD.SPAWN.X", x);
        config.set("WORLD.SPAWN.Y", y);
        config.set("WORLD.SPAWN.Z", z);
        config.set("WORLD.SPAWN.YAW", yaw);
        config.set("WORLD.SPAWN.PITCH", pitch);
        PotPvPSI.getInstance().saveConfig();
        PotPvPSI.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Spawn point updated!");
    }
}