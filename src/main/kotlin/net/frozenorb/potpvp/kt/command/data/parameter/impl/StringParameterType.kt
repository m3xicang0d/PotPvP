package net.frozenorb.potpvp.kt.command.data.parameter.impl

import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType

class StringParameterType : ParameterType<String> {

    override fun transform(sender: CommandSender, source: String): String {
        return source
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return listOf()
    }

}