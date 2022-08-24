package net.frozenorb.potpvp.kt.morpheus.game.event.impl.sumo

import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.nametag.PotPvPNametagProvider
import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEventLogic
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.brackets.BracketsGameEventListeners
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.t4g.T4gGameEventLogic
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.kt.morpheus.game.util.team.GameTeamSizeParameter
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

object SumoGameEvent : GameEvent {

    const val NAME = "Sumo"
    const val PERMISSION = "practice.host.sumo"
    const val DESCRIPTION = "Knock people off the sumo platform."

    init {

    }

    override fun getName(): String {
        return NAME
    }

    override fun getPermission(): String {
        return PERMISSION
    }

    override fun getDescription(): String {
        return DESCRIPTION
    }

    override fun getIcon(): ItemStack {
        return ItemStack(Material.LEASH)
    }

    override fun canStart(game: Game): Boolean {
        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            return game.players.size >= 4
        }

        return game.players.size >= 2
    }

    override fun getLogic(game: Game): GameEventLogic {
        return SumoGameEventLogic(game)
    }

    override fun getScoreboardScores(player: Player, game: Game): List<String> {
        game.startingAt
        var toReturn : MutableList<String> = ArrayList()
        val logic = game.logic as SumoGameEventLogic
        var name = NAME

        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            name = "2v2 $name"
        }

        toReturn.add("&a$name Event")
        toReturn.add("");
        val config = PotPvPSI.instance.scoreboardConfig
        if(game.state == GameState.QUEUED) {
            toReturn = config.getStringList("EVENT-SUMO-QUEUED").stream()
                .map { it.replace("%online%", OnlineVariable().format(player, true)) }
                .map { it.replace("%fighting%", FightingVariable().format(player, true)) }
                .map { it.replace("%in-queue%", InQueueVariable().format(player, true)) }
                .collect(Collectors.toList())
        } else if(game.state == GameState.STARTING) {
            toReturn = config.getStringList("EVENT-SUMO-STARTING").stream()
                .map { it.replace("%online%", OnlineVariable().format(player, true)) }
                .map { it.replace("%fighting%", FightingVariable().format(player, true)) }
                .map { it.replace("%in-queue%", InQueueVariable().format(player, true)) }
                .map { it.replace("%seconds%", (((game.startingAt + 500 - System.currentTimeMillis()) / 1000).toInt()).toString()) }
                .map { it.replace("%total-players%", logic.game.players.size.toString()) }
                .collect(Collectors.toList())
        } else if(game.state == GameState.RUNNING) {
            toReturn = config.getStringList("EVENT-SUMO-RUNNING").stream()
                .map { it.replace("%online%", OnlineVariable().format(player, true)) }
                .map { it.replace("%fighting%", FightingVariable().format(player, true)) }
                .map { it.replace("%in-queue%", InQueueVariable().format(player, true)) }
                .map { it.replace("%actual-round%", logic.getRound().toString()) }
                .collect(Collectors.toList())
            if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) == null) {
                val fighter = logic.getNextParticipant(null)
                val opponent = logic.getNextParticipant(fighter)
                if (opponent != null && fighter != null) {
                    toReturn = toReturn.stream()
                        .map { it.replace("%player-1%", fighter.getName()) }
                        .map { it.replace("%player-2%", opponent.getName()) }
                        .collect(Collectors.toList())
                }
            }
        } else {
            //GameState.ENDED
        }
        /*if (game.state == GameState.STARTING) {
            toReturn.add("&aStarts in: &f${(((game.startingAt + 500 - System.currentTimeMillis()) / 1000).toInt())}")
            toReturn.add("&aPlayers: &f${logic.getPlayersLeft()}")
        } else {
            toReturn.add("&aPlayers: &f${logic.getPlayersLeft()}&7/&f${game.players.size}")
        }

        if (game.state == GameState.RUNNING) {
            if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) == null) {
                val fighter = logic.getNextParticipant(null)
                val opponent = logic.getNextParticipant(fighter)

                if (opponent != null && fighter != null) {
                    toReturn.add("&a&r&7&m--------------------")
                    toReturn.add("&aRound: &f#${logic.getRound()}")
                    toReturn.add("&a${fighter.getName()}&c vs.&a ${opponent.getName()}")
                }
            }
        }
*/
        return toReturn
    }

    override fun getNameTag(game: Game, player: Player, viewer: Player): String {
        if (T4gGameEventLogic.isTagged(player)) {
            return ChatColor.RED.toString();
        } else {
            if (game.spectators.contains(player)) {
                return ChatColor.GRAY.toString();
            }
        }

        return "PotPvPNametagProvider.getNameColorRank(player)";
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(SumoGameEventListeners(), BracketsGameEventListeners())
    }

    override fun getParameters(): List<GameParameter> {
        return listOf();
    }



}