package net.frozenorb.potpvp.kt.command.bukkit

import org.bukkit.help.HelpTopic
import org.bukkit.command.CommandSender
import net.frozenorb.potpvp.kt.command.CommandNode
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor

class ExtendedHelpTopic(public val node: CommandNode, aliases: Set<String>?) : HelpTopic() {

    init {
        name = "/" + node.name!!

        val description = node.description

        shortText = if (description!!.length < 32) {
            description
        } else {
            description.substring(0, 32)
        }

        val sb = StringBuilder()
        sb.append(ChatColor.GOLD)
        sb.append("Description: ")
        sb.append(ChatColor.WHITE)
        sb.append(node.description)
        sb.append("\n")
        sb.append(ChatColor.GOLD)
        sb.append("Usage: ")
        sb.append(ChatColor.WHITE)
        sb.append(node.getUsageForHelpTopic())

        if (aliases != null && aliases.isNotEmpty()) {
            sb.append("\n")
            sb.append(ChatColor.GOLD)
            sb.append("Aliases: ")
            sb.append(ChatColor.WHITE)
            sb.append(StringUtils.join(aliases as Collection<*>?, ", "))
        }

        fullText = sb.toString()
    }

    override fun canSee(commandSender: CommandSender): Boolean {
        return node.canUse(commandSender)
    }

}