package net.frozenorb.potpvp.kt.morpheus.game.task

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.GameStateChangeEvent
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.arena.Arena
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.TimeUnit

class GameStartTask(val plugin: JavaPlugin, game: Game) {

    val startsAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60) - 5
    private val interval = 15

    init {
        val arena: Optional<Arena> = PotPvPSI.instance.arenaHandler.allocateUnusedArena {it.event == game.event && it.isEnabled}

        if (arena.isPresent) {
            Bukkit.getPluginManager().callEvent(GameStateChangeEvent(game, GameState.STARTING))
            game.startingAt = startsAt
            game.arena = arena.get()
            Task(game).runTaskTimer(plugin, 0, interval * 20L)
        } else {
            Bukkit.getPluginManager().callEvent(GameStateChangeEvent(game, GameState.ENDED))
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.isOp) {
                    player.sendMessage(ChatColor.RED.toString() + "Failed to start " + game.event.getName() + " due to lack of an arena.")
                }
            }
        }
    }

    inner class Task(val game: Game) : BukkitRunnable() {
        val before = TextComponent(ChatColor.GRAY.toString() + "█" + ChatColor.RED + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GREEN + "Type " + ChatColor.RED + "/play " + ChatColor.GREEN + "or ")
        val clickable = TextComponent("click here")
        val after = TextComponent(ChatColor.GREEN.toString() + " to join!")

        override fun run() {

            if (startsAt <= System.currentTimeMillis() || game.players.size == game.getMaxPlayers()) {
                object: BukkitRunnable() {
                    override fun run() {
                        game.start()
                    }
                }.runTask(plugin)
                cancel()
                return
            }

            for (player in Bukkit.getOnlinePlayers()) {
                // TODO: Maybe fix this section bellow, it looks odd

                val runCommand = ClickEvent.Action.RUN_COMMAND
                val showText = HoverEvent.Action.SHOW_TEXT

                clickable.color = net.md_5.bungee.api.ChatColor.RED
                clickable.clickEvent = ClickEvent(runCommand, "/play")
                clickable.hoverEvent = HoverEvent(showText, arrayOf<BaseComponent>(TextComponent(ChatColor.GREEN.toString() + "Click here to join!")))

                player.sendMessage(arrayOf("",
                        ChatColor.GRAY.toString() + "███████",
                        ChatColor.GRAY.toString() + "█" + ChatColor.RED + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.RED + ChatColor.BOLD + "${game.event.getName()} " + ChatColor.GREEN + "Event",
                        ChatColor.GRAY.toString() + "█" + ChatColor.RED + "█" + ChatColor.GRAY + "█████" + " ",
                        ChatColor.GRAY.toString() + "█" + ChatColor.RED + "████" + ChatColor.GRAY + "██" + " " +  game.host.displayName + ChatColor.GREEN + " is hosting an event!",
                        ChatColor.GRAY.toString() + "█" + ChatColor.RED + "█" + ChatColor.GRAY + "█████" + " " + ChatColor.GREEN + "Starts in " + ChatColor.AQUA + (formatIntoDetailedString(((startsAt + 500 - System.currentTimeMillis()) / 1000).toInt()))
                ))
                player.spigot().sendMessage(before, clickable, after)
                player.sendMessage(arrayOf(ChatColor.GRAY.toString() + "███████", ""))
            }

        }

        fun formatIntoDetailedString(secs: Int): String {
            return if (secs == 0) {
                "0 seconds"
            } else {
                val remainder = secs % 86400
                val days = secs / 86400
                val hours = remainder / 3600
                val minutes = remainder / 60 - hours * 60
                val seconds = remainder % 3600 - minutes * 60
                val fDays = if (days > 0) " " + days + " day" + (if (days > 1) "s" else "") else ""
                val fHours = if (hours > 0) " " + hours + " hour" + (if (hours > 1) "s" else "") else ""
                val fMinutes = if (minutes > 0) " " + minutes + " minute" + (if (minutes > 1) "s" else "") else ""
                val fSeconds = if (seconds > 0) " " + seconds + " second" + (if (seconds > 1) "s" else "") else ""
                (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
            }
        }

    }

}
