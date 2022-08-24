package net.frozenorb.potpvp.kt.morpheus.game

import net.frozenorb.potpvp.kt.morpheus.game.task.GameStartTask
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object GameQueue {

    public val runningGames = ArrayList<Game>()
    val games = LinkedList<Game>()
    public val maxGameInstances = 3;

    fun run(plugin: JavaPlugin) {
        object: BukkitRunnable() {
            override fun run() {
                check(plugin)
            }
        }.runTaskTimer(plugin, 20L, 20L)
    }

    public fun check(plugin: JavaPlugin) {
        val game = games.peek()
        if (game != null) {
            if (game.state == GameState.QUEUED) {

                var cancelled = runningGames.size > 0

                if (!(game.host.isOnline)) {
                    games.remove()
                    cancelled = true
                }

                if (!(cancelled)) {
                    games.remove()
                    runningGames.add(game)
                    GameStartTask(plugin, game)
                }
            }
        }

        val iterator = runningGames.iterator()
        while (iterator.hasNext()) {
            val runningGame = iterator.next()

            if (runningGame.state == GameState.ENDED) {
                iterator.remove()
                continue
            }

            var onlinePlayers = 0
            for (player in runningGame.players) {
                if (player.isOnline) onlinePlayers++
            }

            if (runningGame.state != GameState.STARTING && (runningGame.players.isEmpty() || onlinePlayers == 0)) {
                iterator.remove()
                game.end()
                continue
            }

        }

    }

    fun add(game: Game) {
        games.add(game)
    }

    fun size(): Int {
        return games.size
    }

    fun getCurrentGames(): List<Game> {
        return runningGames
    }

    fun getCurrentGame(player: Player): Game? {
        runningGames.filter { it.players.contains(player) }.find { return it }

        return null
    }

    fun getCurrentGame(): Game? {
        if (runningGames.size == 0) return null;

        return runningGames.first();
    }


    fun playerCanHostGame(player: Player): Boolean {
        if (player.hasPermission("practice.host.bypass") && size() < 10) return true;

        var result = true

        if (size() >= maxGameInstances) {
            player.sendMessage(ChatColor.RED.toString() + "The event queue is full! please wait until a slot is released.")
            result = false;
        }

        if (isHosting(games, player) || isHosting(runningGames, player)) {
            player.sendMessage(ChatColor.RED.toString() + "You've already queued an event!")
            result = false
        }

        return result
    }

    fun isHosting(games: List<Game>, player: Player): Boolean {
        var result = false;
        for (game in games) {
            if (game.host == player) {
                result = true;
                break;
            }
        }
        return result;
    }



}