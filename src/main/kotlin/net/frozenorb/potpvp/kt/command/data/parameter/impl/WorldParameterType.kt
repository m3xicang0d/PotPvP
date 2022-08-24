package net.frozenorb.potpvp.kt.command.data.parameter.impl

import java.util.stream.Collectors
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.World

class WorldParameterType : ParameterType<World?> {

    override fun transform(sender: CommandSender, source: String): World? {
        val world = Bukkit.getWorld(source)

        if (world == null) {
            sender.sendMessage("${ChatColor.RED}No world with the name $source found.")
            return null
        }

        return world
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return Bukkit.getWorlds().stream().map { world -> world.name }.collect(Collectors.toList<String>())
    }

}