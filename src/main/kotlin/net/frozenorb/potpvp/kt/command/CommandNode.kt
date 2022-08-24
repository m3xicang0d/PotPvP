package net.frozenorb.potpvp.kt.command

import com.google.common.base.Strings
import mkremins.fanciful.FancyMessage
import net.frozenorb.potpvp.kt.command.data.Data
import net.frozenorb.potpvp.kt.command.data.argument.Arguments
import net.frozenorb.potpvp.kt.command.data.flag.FlagData
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterData
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType
import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.ChatColor
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class CommandNode(var name: String?,
                  var permission: String?,
                  var description: String?) {

    var async: Boolean = false
    var hidden: Boolean = false
    var vipFeature: Boolean = false
    val aliases: MutableSet<String> = HashSet()
    val children: MutableMap<String, CommandNode> = TreeMap()
    var validFlags: MutableList<String> = mutableListOf()
    var parameters: MutableList<Data> = mutableListOf()
    var parent: CommandNode? = null
    var logToConsole: Boolean = false
    var method: Method? = null
    var owningClass: Class<*>? = null

    constructor(clazz: Class<*>) : this(null, null, "ROOT NODE") {
        owningClass = clazz
    }

    constructor() : this(null, null, null)
    constructor(name: String) : this(name, null, "")
    constructor(name: String, permission: String?) : this(name, permission, "")

    fun registerCommand(commandNode: CommandNode) {
        if (commandNode.name != null) {
            commandNode.parent = this
            children[commandNode.name!!] = commandNode
        } else {
            throw IllegalArgumentException("Cannot register command without a name to root node")
        }
    }

    fun hasCommand(name: String): Boolean = children.containsKey(name.toLowerCase())
    fun getCommand(name: String): CommandNode? = children[name.toLowerCase()]
    fun hasCommands(): Boolean = children.isNotEmpty()

    fun findCommand(arguments: Arguments): CommandNode {
        if (arguments.arguments.isNotEmpty()) {
            val trySub = arguments.arguments[0]

            if (hasCommand(trySub)) {
                arguments.arguments.removeAt(0)

                val returnNode = getCommand(trySub) as CommandNode
                return returnNode.findCommand(arguments)
            }
        }

        return this
    }

    fun isValidFlag(test: String): Boolean {
        return if (test.length == 1) {
            validFlags.contains(test)
        } else {
            validFlags.contains(test.toLowerCase())
        }
    }

    fun canUse(sender: CommandSender): Boolean {
        if (permission == null) {
            return true
        }

        return when (permission) {
            "console" -> {
                sender is ConsoleCommandSender
            }
            "op" -> {
                sender.isOp
            }
            "" -> {
                true
            }
            else -> {
                sender.hasPermission(this.permission)
            }
        }
    }

    fun getUsage(realLabel: String): FancyMessage {
        val usage = FancyMessage("Usage: /$realLabel").color(ChatColor.RED)
        usage.tooltip("${ChatColor.YELLOW}$description")

        val flags = mutableListOf<FlagData>()
        flags.addAll(this.parameters.stream().filter { data -> data is FlagData }.map { data -> data as FlagData }.collect(Collectors.toList()))

        val parameters = mutableListOf<ParameterData>()
        parameters.addAll(this.parameters.stream().filter { data -> data is ParameterData }.map { data -> data as ParameterData }.collect(Collectors.toList()))

        var flagFirst = true

        if (flags.isNotEmpty()) {
            usage.then("(").color(ChatColor.RED)
            usage.tooltip("${ChatColor.YELLOW}$description")

            for (flag in flags) {
                val name = flag.names[0]

                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED)
                    usage.tooltip("${ChatColor.YELLOW}$description")
                }

                flagFirst = false

                usage.then("-$name").color(ChatColor.AQUA)
                usage.tooltip("${ChatColor.GRAY}${flag.description}")
            }

            usage.then(") ").color(ChatColor.RED)
            usage.tooltip("${ChatColor.YELLOW}$description")
        }

        if (parameters.isNotEmpty()) {
            for (index in parameters.indices) {
                val parameter = parameters[index]
                val required = parameter.defaultValue.isEmpty()

                usage.then((if (required) "<" else "[") + parameter.name + (if (parameter.wildcard) "..." else "") + (if (required) ">" else "]") + if (index != parameters.size - 1) " " else "").color(ChatColor.RED)
                usage.tooltip("${ChatColor.YELLOW}$description")
            }
        }

        return usage
    }

    fun getUsage(): FancyMessage {
        val usage = FancyMessage("")
        val flags = mutableListOf<FlagData>()
        flags.addAll(parameters.stream().filter { data -> data is FlagData }.map { data -> data as FlagData }.collect(Collectors.toList()))

        val parameters = mutableListOf<ParameterData>()
        parameters.addAll(parameters.stream().filter { data -> data is ParameterData }.map { data -> data as ParameterData }.collect(Collectors.toList()))

        var flagFirst = true

        if (flags.isNotEmpty()) {
            usage.then("(").color(ChatColor.RED)
            usage.tooltip("${ChatColor.YELLOW}$description")

            for (flag in flags) {
                val name = flag.names[0]

                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED)
                    usage.tooltip("${ChatColor.YELLOW}$description")
                }

                flagFirst = false

                usage.then("-$name").color(ChatColor.AQUA)
                usage.tooltip("${ChatColor.GRAY}${flag.description}")
            }

            usage.then(") ").color(ChatColor.RED)
            usage.tooltip("${ChatColor.YELLOW}$description")
        }

        if (parameters.isNotEmpty()) {
            for (index in parameters.indices) {
                val parameter = parameters[index]
                val required = parameter.defaultValue.isEmpty()

                usage.then((if (required) "<" else "[") + parameter.name + (if (parameter.wildcard) "..." else "") + (if (required) ">" else "]") + if (index != parameters.size - 1) " " else "").color(ChatColor.RED)
                usage.tooltip("${ChatColor.YELLOW}$description")
            }
        }

        return usage
    }

    @Throws(CommandException::class)
    operator fun invoke(sender: CommandSender, arguments: Arguments): Boolean {
        if (method == null) {
            if (hasCommands()) {
                if (getSubCommands(sender, true).isEmpty()) {
                    if (hidden) {
                        sender.sendMessage(CommandCompat.getUnknownCommandMessage())
                    } else {
                        var noPermissionMessage = "${ChatColor.RED}You don't have permission to use this command."
                        if (vipFeature) noPermissionMessage = "${ChatColor.RED}This is a VIP feature, to unlock it, get a rank at ${ChatColor.YELLOW}store.cougar.rip"
                        sender.sendMessage(noPermissionMessage)
                    }
                }
            } else {
                sender.sendMessage(CommandCompat.getUnknownCommandMessage())
            }

            return true
        }

        val methodParamCount = method!!.parameterCount
        val objects = ArrayList<Any>(methodParamCount)
        objects.add(sender)

        var index = 0
        for (data in this.parameters) {
            if (data is FlagData) {
                var value = data.defaultValue

                for (s in data.names) {
                    if (arguments.hasFlag(s)) {
                        value = !value
                        break
                    }
                }

                objects.add(data.methodIndex, value)
            } else {
                if (data !is ParameterData) {
                    continue
                }

                var argument: String?
                argument = if (index < arguments.arguments.size) {
                    arguments.arguments[index]
                } else {
                    if (data.defaultValue.isEmpty()) {
                        return false
                    } else {
                        data.defaultValue
                    }
                }

                if (data.wildcard && (argument.isEmpty() || argument != data.defaultValue)) {
                    argument = arguments.join(index)
                }

                var type: ParameterType<*>? = PotPvPSI.instance.commandHandler.getParameterType(data.type)

                if (data.parameterType != null) {
                    try {
                        type = data.parameterType.newInstance() as ParameterType<*>
                    } catch (e1: InstantiationException) {
                        throw CommandException("Failed to create ParameterType instance: " + data.parameterType.name, e1)
                    } catch (e2: IllegalAccessException) {
                        throw CommandException("Failed to create ParameterType instance: " + data.parameterType.name, e2)
                    }
                }

                if (type == null) {
                    sender.sendMessage("${ChatColor.RED}No data type found: ${(data.parameterType
                            ?: data.type).simpleName}")
                    return true
                }

                val result = type.transform(sender, argument) ?: return true
                objects.add(data.methodIndex, result)

                ++index
            }
        }

        try {
            val start = System.currentTimeMillis()

            method!!.invoke(null, *objects.toTypedArray())

            val stop = System.currentTimeMillis()

            val executionThreshold = 10 // command threshold

            if (!async && logToConsole && stop - start >= executionThreshold) {
                PotPvPSI.instance.logger.warning("Command '/" + getFullLabel() + "' took " + (stop - start) + "ms!")
            }

            return true
        } catch (e1: IllegalAccessException) {
            throw CommandException("An error occurred while executing the command", e1)
        } catch (e2: InvocationTargetException) {
            throw CommandException("An error occurred while executing the command", e2)
        }
    }

    fun getSubCommands(sender: CommandSender, print: Boolean): List<String> {
        val commands = ArrayList<String>()
        if (canUse(sender)) {
            val command = (if (sender is Player) "/" else "") + this.getFullLabel() + " " + getUsage().toOldMessageFormat() + if (Strings.isNullOrEmpty(this.description)) "" else "${ChatColor.GRAY} - $description"

            if (parent == null) {
                commands.add(command)
            } else if (parent?.name != null && CommandHandler.rootNode.getCommand(this.parent!!.name!!) !== this.parent) {
                commands.add(command)
            }

            if (this.hasCommands()) {
                for (n in children.values) {
                    commands.addAll(n.getSubCommands(sender, false))
                }
            }
        }

        if (commands.isNotEmpty() && print) {
            for (command2 in commands) {
                sender.sendMessage("${ChatColor.RED} $command2")
            }
        }

        return commands
    }

    fun getRealAliases(): Set<String> {
        aliases.remove(name)
        return aliases
    }

    fun getFullLabel(): String {
        val labels = ArrayList<String>()
        var node: CommandNode? = this

        while (node != null) {
            val name = node.name

            if (name != null) {
                labels.add(name)
            }

            node = node.parent
        }

        labels.reverse()
        labels.removeAt(0)

        val builder = StringBuilder()
        labels.forEach { s -> builder.append(s).append(' ') }

        return builder.toString().trim { it <= ' ' }
    }

    fun getUsageForHelpTopic(): String {
        return if (method != null) {
            "/" + getFullLabel() + " " + ChatColor.stripColor(getUsage().toOldMessageFormat())
        } else ""
    }

}