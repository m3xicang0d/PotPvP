package net.frozenorb.potpvp.kt.morpheus.game.event.impl.brackets

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.PlayerGameInteractionEvent
import net.frozenorb.potpvp.kt.morpheus.game.util.GameEventCountdown
import net.frozenorb.potpvp.kt.morpheus.game.util.team.GameTeam
import net.frozenorb.potpvp.kt.morpheus.game.util.team.GameTeamEventLogic
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.util.CC
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

open class BracketsGameEventLogic(val game: Game) : GameTeamEventLogic(game) {

    override fun start() {
        super.start()

        next()
    }

    fun check() {
        val winner = getWinner() ?: return
        val loser = getLoser() ?: return

        winner.reset()
        winner.round += 1
        winner.fighting = false

        participants.remove(loser)

        for (player in winner.players) {
            game.reset(player)
        }

        for (player in loser.players) {
            game.addSpectator(player)
        }

        if (getNextParticipant(winner) == null) {
            game.end()
            broadcastWinner(winner)
        } else {
            game.sendMessage("", ChatColor.GREEN.toString() + winner.getName() + ChatColor.RED + " beat " + ChatColor.GREEN + loser.getName() + ChatColor.RED + "!", "")
            next()
        }
    }

    fun broadcastWinner(winner: GameTeam) {
        var winnerName = winner.players[0].displayName
        for (player in Bukkit.getOnlinePlayers()) {

            CC.translate(PotPvPSI.instance.messagesConfig.getStringList("EVENT.WIN-MESSAGE")).stream()
                    .map{ it.replace("%game-name%", game.event.getName()) }
                    .map{ it.replace("%winner%", winnerName) }
                    .map{ it.replace("%defeated%", (getRound()?.minus(1)).toString()) }
                    .forEach(player::sendMessage)
        }
    }

    public fun next() {
        val fighter = getNextParticipant(null)
        val opponent = getNextParticipant(fighter)

        if (fighter != opponent && fighter != null && opponent != null) {

            if (fighter.round != opponent.round) {
                fighter.round = maxOf(fighter.round, opponent.round)
                opponent.round = fighter.round
            }

            game.sendMessage("", ChatColor.RED.toString() + "" + ChatColor.BOLD + "Next Matchup:", ChatColor.GREEN.toString() + fighter.getName() + ChatColor.RED + " vs. " + ChatColor.GREEN + opponent.getName(), "")

            fighter.starting = true
            opponent.starting = true

            GameEventCountdown(5,
                    object : BukkitRunnable() {
                        override fun run() {
                            fighter.starting = false
                            fighter.fighting = true

                            opponent.starting = false
                            opponent.fighting = true
                        }
                    }, fighter, opponent)

            val kit = game.getParameter(BracketsGameKitParameter.BracketsGameKitOption::class.java)

            fighter.players.forEachIndexed { index, player ->
                game.spectators.remove(player)
                player.inventory.clear()
                player.isSprinting = false
                player.updateInventory()
                player.velocity = Vector()
                player.teleport(game.getFirstSpawnLocations()[index])

                game.spectators.remove(player)
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(player, game))

                if (kit != null && kit is BracketsGameKitParameter.BracketsGameKitOption) {
                    kit.apply(player)
                }
            }

            opponent.players.forEachIndexed { index, player ->
                game.spectators.remove(player)
                player.inventory.clear()
                player.isSprinting = false
                player.velocity = Vector()
                player.updateInventory()
                player.teleport(game.getSecondSpawnLocations()[index])

                game.spectators.remove(player)
                Bukkit.getPluginManager().callEvent(PlayerGameInteractionEvent(player, game))

                if (kit != null && kit is BracketsGameKitParameter.BracketsGameKitOption) {
                    kit.apply(player)
                }
            }

            return
        }

        game.end()
    }

    fun getRound(): Int? {
        return 1 + (getNextParticipant(null)?.round ?: 0)
    }

    fun getNextParticipant(exclude: GameTeam?): GameTeam? {
        var current: GameTeam? = null

        for (participant in participants) {
            if (participant != exclude) {
                if (current == null || participant.round < current.round) {
                    current = participant
                }
            }
        }

        return current
    }

    public fun getWinner(): GameTeam? {
        for (participant in participants) {
            if ((participant.fighting || participant.starting) && !participant.isFinished()) {
                return participant
            }
        }
        return null
    }

    public fun getLoser(): GameTeam? {
        for (participant in participants) {
            if ((participant.fighting || participant.starting) && participant.isFinished()) {
                return participant
            }
        }

        return null
    }

    fun getPlayersLeft(): Int {
        if (game.state == GameState.STARTING) return game.players.size

        var toReturn = 0

        for (participant in participants) {
            toReturn += participant.players.size
        }

        return toReturn
    }

}