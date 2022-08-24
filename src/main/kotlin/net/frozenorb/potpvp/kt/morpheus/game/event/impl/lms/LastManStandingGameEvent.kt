package net.frozenorb.potpvp.kt.morpheus.game.event.impl.lms

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEventLogic
import net.frozenorb.potpvp.kt.morpheus.game.util.team.GameTeamSizeParameter
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.util.potpvp.PotPvPLang
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

object LastManStandingGameEvent : GameEvent {

    public const val NAME = "LMS"
    public const val PERMISSION = "practice.host.sumo"
    public const val DESCRIPTION = "Compete against other players to be the last man standing."

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
        return ItemStack(Material.TNT)
    }

    override fun canStart(game: Game): Boolean {
        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            return game.players.size >= 4
        }

        return game.players.size >= 2
    }

    override fun getLogic(game: Game): GameEventLogic {
        return LastManStandingGameEventLogic(game)
    }

    override fun getScoreboardScores(player: Player, game: Game): List<String> {
        val toReturn = ArrayList<String>()
        val logic = game.logic as LastManStandingGameEventLogic
        var name = NAME

        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            name = "2v2 $name"
        }

        toReturn.add("&cEvent &7($name)")
        // todo fix max players for when game started xd
        toReturn.add("&6 ${PotPvPLang.LEFT_ARROW_NAKED} &fPlayers: &7${logic.getPlayersLeft()}/${game.getMaxPlayers()}")

        if (game.state == GameState.RUNNING) {
            // toReturn.add("&6 ${PotPvPLang.LEFT_ARROW_NAKED} &fKills: &7${player.getStatistic(Statistic.PLAYER_KILLS)}")
        }

        return toReturn
    }

    override fun getNameTag(game: Game, player: Player, viewer: Player): String {
        return ChatColor.RED.toString()
    }

    override fun getListeners(): List<Listener> {
        return arrayListOf(LastManStandingGameEventListeners())
    }

    override fun getParameters(): List<GameParameter> {
        return listOf(GameTeamSizeParameter, LastManStandingGameKitParameter)
    }

    override fun getMaxInstances(): Int {
        return 5
    }

}