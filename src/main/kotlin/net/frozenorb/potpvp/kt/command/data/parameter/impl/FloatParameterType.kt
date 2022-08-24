package net.frozenorb.potpvp.kt.command.data.parameter.impl

import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

class FloatParameterType : ParameterType<Float?> {

    override fun transform(sender: CommandSender, value: String): Float? {
        if (value.toLowerCase().contains("e")) {
            sender.sendMessage(ChatColor.RED.toString() + value + " is not a valid number.")
            return null
        }

        try {
            val parsed = java.lang.Float.parseFloat(value)

            if (java.lang.Float.isNaN(parsed) || !java.lang.Float.isFinite(parsed)) {
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