package net.frozenorb.potpvp.kt.command.data.parameter.impl

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.ArrayList
import java.util.UUID
import org.bukkit.command.CommandSender
import java.lang.Exception

class UUIDParameterType : ParameterType<UUID?> {

    override fun transform(sender: CommandSender, source: String): UUID? {
        if (sender is Player && (source.equals("self", ignoreCase = true) || source == "")) {
            return sender.uniqueId
        }

        return try {
            UUID.fromString(source)
        } catch (e: Exception) {
            val fromCache = PotPvPSI.instance.uuidCache.uuid(source)

            if (fromCache != null) {
                return fromCache
            }

            sender.sendMessage("${ChatColor.RED}$source is not a valid UUID.")
            null
        }
    }

    override fun tabComplete(sender: Player, flags: Set<String>, source: String): List<String> {
        val completions = ArrayList<String>()

        for (player in Bukkit.getOnlinePlayers()) {
            // TODO: add support for visibility engine
            if (!sender.canSee(player)) {
                continue
            }

            completions.add(player.name)
        }

        return completions
    }

}