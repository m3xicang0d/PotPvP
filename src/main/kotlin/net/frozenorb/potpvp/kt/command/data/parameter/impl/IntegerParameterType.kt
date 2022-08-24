package net.frozenorb.potpvp.kt.command.data.parameter.impl

import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

class IntegerParameterType : ParameterType<Int?> {

    override fun transform(sender: CommandSender, value: String): Int? {
        return try {
            Integer.parseInt(value)
        } catch (exception: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED} $value is not a valid number.")
            null
        }
    }

    override fun tabComplete(sender: Player, flags: Set<String>, prefix: String): List<String> {
        return listOf()
    }

}