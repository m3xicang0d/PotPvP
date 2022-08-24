package net.frozenorb.potpvp.kt.command.data.parameter.impl

import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

class DoubleParameterType : ParameterType<Double?> {

    override fun transform(sender: CommandSender, value: String): Double? {
        if (value.toLowerCase().contains("e")) {
            sender.sendMessage(ChatColor.RED.toString() + value + " is not a valid number.")
            return null
        }

        try {
            val parsed = java.lang.Double.parseDouble(value)

            if (java.lang.Double.isNaN(parsed) || !java.lang.Double.isFinite(parsed)) {
                sender.sendMessage(ChatColor.RED.toString() + value + " is not a valid number.")
                return null
            }

            return parsed
        } catch (exception: NumberFormatException) {
            sender.sendMessage(ChatColor.RED.toString() + value + " is not a valid number.")
            return null
        }

    }

    override fun tabComplete(sender: Player, flags: Set<String>, prefix: String): List<String> {
        return listOf()
    }

}