package net.frozenorb.potpvp.kt.command.data.parameter

import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

interface ParameterType<T> {

    fun transform(sender: CommandSender, source: String): T

    fun tabComplete(player: Player, flags: Set<String>, source: String): List<String>

}
