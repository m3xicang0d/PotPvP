package net.frozenorb.potpvp.kt.command.bukkit

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.kt.command.CommandCompat
import net.frozenorb.potpvp.kt.command.CommandNode
import net.frozenorb.potpvp.kt.command.data.argument.ArgumentProcessor
import net.frozenorb.potpvp.kt.command.data.flag.Flag
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterData
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.lang.StringBuilder
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class ExtendedCommand(val node: CommandNode, public val plugin: JavaPlugin) : Command(node.name, "", "/", node.getRealAliases().toList()), PluginIdentifiableCommand {

    override fun getPlugin(): Plugin {
        return plugin
    }

    override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
        val label = label.replace("${plugin.name.toLowerCase()}:", "")
        val newArgs = concat(label, args)
        val arguments = ArgumentProcessor().process(newArgs)
        val executionNode = node.findCommand(arguments)
        val realLabel = getFullLabel(executionNode)

        if (executionNode.canUse(sender)) {
            if (executionNode.async) {
                plugin.server.scheduler.runTaskAsynchronously(plugin) {
                    try {
                        if (!executionNode.invoke(sender, arguments)) {
                            executionNode.getUsage(realLabel).send(sender)
                        }
                    } catch (e: CommandException) {
                        e.printStackTrace()

                        executionNode.getUsage(realLabel).send(sender)
                        sender.sendMessage("${ChatColor.RED}An error occurred while processing your command.")

                        if (sender.isOp) {
                            sendStackTrace(sender, e)
                        }
                    }
                }
            } else {
                try {
                    if (!executionNode.invoke(sender, arguments)) {
                        executionNode.getUsage(realLabel).send(sender)
                    }
                } catch (e: CommandException) {
                    e.printStackTrace()

                    executionNode.getUsage(realLabel).send(sender)
                    sender.sendMessage("${ChatColor.RED}An error occurred while processing your command.")

                    if (sender.isOp) {
                        sendStackTrace(sender, e)
                    }
                }
            }
        } else if (executionNode.hidden) {
            sender.sendMessage(CommandCompat.getUnknownCommandMessage())
        } else {
            sender.sendMessage("${ChatColor.RED}No permission.")
        }

        return true
    }

    fun tabComplete(sender: CommandSender, cmdLine: String): List<String> {
        if (sender !is Player) {
            return listOf()
        }

        val rawArgs = cmdLine.replace("${plugin.name.toLowerCase()}:", "").split(" ")

        if (rawArgs.isEmpty()) {
            if (!node.canUse(sender)) {
                return listOf()
            }

            return listOf()
        } else {
            val arguments = ArgumentProcessor().process(rawArgs.toTypedArray())
            val realNode = node.findCommand(arguments)

            if (!realNode.canUse(sender)) {
                return listOf()
            }

            val realArgs = arguments.arguments
            var currentIndex = realArgs.size - 1

            if (currentIndex < 0) {
                currentIndex = 0
            }

            if (cmdLine.endsWith(" ") && realArgs.size >= 1) {
                ++currentIndex;
            }

            if (currentIndex < 0) {
                return listOf()
            }

            val completions: ArrayList<String> = arrayListOf()

            if (realNode.hasCommands()) {
                val name = if (realArgs.size == 0) "" else realArgs[realArgs.size - 1]
                completions.addAll(realNode.children.values.stream().filter { node -> node.name != null && node.canUse(sender) && (StringUtils.startsWithIgnoreCase(node.name, name) || StringUtils.isEmpty(name)) }.map { node -> node.name }.collect(Collectors.toList<String>()))

                if (completions.isNotEmpty()) {
                    return completions
                }
            }

            if (rawArgs[rawArgs.size - 1].equals(realNode.name, true) && !cmdLine.endsWith(" ")) {
                return listOf()
            }

            if (realNode.validFlags.isNotEmpty()) {
                for (flag in realNode.validFlags) {
                    val arg = rawArgs[rawArgs.size - 1]

                    if (Flag.FLAG_PATTERN.matcher(arg).matches() || arg == "-" && (StringUtils.startsWithIgnoreCase(flag, arg.substring(1, arg.length))) || arg == "-") {
                        completions.add("-$flag")
                    }
                }

                if (completions.isNotEmpty()) {
                    return completions
                }
            }

            try {
                val params = realNode.parameters.stream().filter { param -> param is ParameterData }.map { param -> param as ParameterData }.collect(Collectors.toList())
                val fixed = Math.max(0, currentIndex - 1)

                if (params.isEmpty()) {
                    return emptyList()
                }

                val data: ParameterData = params[fixed]!!
                val parameterType: ParameterType<*>? = PotPvPSI.instance.commandHandler.getParameterType(data.type)

                if (parameterType != null) {
                    if (currentIndex < realArgs.size && realArgs[currentIndex].equals(realNode.name, true)) {
                        realArgs.add("")
                        ++currentIndex
                    }

                    val argumentBeingCompleted = (if (currentIndex >= realArgs.size || realArgs.size == 0) "" else realArgs[currentIndex]).trim()
                    val suggested = parameterType.tabComplete(sender, data.tabCompleteFlags, argumentBeingCompleted)

                    completions.addAll(suggested.stream().filter { s -> StringUtils.startsWithIgnoreCase(s, argumentBeingCompleted) }.collect(Collectors.toList()))
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            return completions
        }
    }

    public fun concat(label: String, args: Array<String>): Array<String> {
        val list = arrayListOf(label)
        list.addAll(args.toList().stream().filter { it != null }.collect(Collectors.toList<String>()))
        return list.toTypedArray()
    }

    public fun getFullLabel(node: CommandNode?): String {
        var workingNode = node
        val labels = arrayListOf<String>()

        while (workingNode != null) {
            val name = workingNode.name

            if (name != null) {
                labels.add(name)
            }

            workingNode = workingNode.parent
        }

        labels.reverse()
        labels.removeAt(0)

        val builder = StringBuilder()

        labels.forEach { s ->
            builder.append(s).append(' ')
        }

        return builder.toString()
    }

    public fun sendStackTrace(sender: CommandSender, exception: Exception) {
        val rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception)

        sender.sendMessage("${ChatColor.RED}Message: $rootCauseMessage")

        val cause = ExceptionUtils.getStackTrace(exception)
        val tokenizer = StringTokenizer(cause)
        var exceptionType = ""
        var details = ""
        var parsingNeeded = false

        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()

            if (token.equals("Caused", true)) {
                tokenizer.nextToken()

                parsingNeeded = true
                exceptionType = tokenizer.nextToken()
            } else {
                if (token.equals("at", true) && parsingNeeded) {
                    details = tokenizer.nextToken()
                    break
                }

                continue
            }
        }

        sender.sendMessage("${ChatColor.RED}Exception: ${exceptionType.replace(":", "")}")
        sender.sendMessage("${ChatColor.RED}Details:")
        sender.sendMessage("${ChatColor.RED}$details")
    }

}