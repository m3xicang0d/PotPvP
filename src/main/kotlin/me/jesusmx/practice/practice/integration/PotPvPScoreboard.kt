package me.jesusmx.practice.practice.integration

import me.jesusmx.practice.practice.integration.scoreboard.other.Scoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.util.scoreboard.AssembleAdapter
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue.getCurrentGame
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import net.frozenorb.potpvp.player.setting.Setting
import org.bukkit.entity.Player

class PotPvPScoreboard(

    private val matchScoreboard : Scoreboard,
    private val lobbyScoreboard : Scoreboard,
    private val gameScoreGetter : Scoreboard,
    private val partyScoreGetter : Scoreboard
    ) : AssembleAdapter {

    override fun getTitle(player : Player): String {
        return PotPvPSI.instance.scoreboardConfig.getString("TITLE").replace("|", "&7&l⎜")
    }

    override fun getLines(player : Player): MutableList<String> {
        val toReturn = mutableListOf<String>()
        if (PotPvPSI.instance == null) return mutableListOf()

        val matchHandler = PotPvPSI.instance.matchHandler
        val settingHandler = PotPvPSI.instance.settingHandler
        if(!settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) return mutableListOf()
        if(matchHandler.isPlayingOrSpectatingMatch(player)) {
            matchScoreboard.accept(player, toReturn)
        } else {
            val game = getCurrentGame(player)
            if (game != null && game.players.contains(player) && game.state !== GameState.ENDED) {
                this.gameScoreGetter.accept(player, toReturn)
            } else {
                if(PotPvPSI.instance.partyHandler.hasParty(player)) {
                    partyScoreGetter.accept(player, toReturn)
                } else {
                    lobbyScoreboard.accept(player, toReturn)
                }
            }
        }
        return toReturn
    }
/*
    override fun getScores(scores: MutableList<String>, player: Player) {
        if (PotPvPSI.instance == null) return

        val matchHandler = PotPvPSI.instance.matchHandler
        val settingHandler = PotPvPSI.instance.settingHandler
        if(!settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) return
        if(matchHandler.isPlayingOrSpectatingMatch(player)) {
            matchScoreboard.accept(player, scores)
        } else {
            val game = getCurrentGame(player)
            if (game != null && game.players.contains(player) && game.state !== GameState.ENDED) {
                this.gameScoreGetter.accept(player, scores)
            } else {
                if(PotPvPSI.instance.partyHandler.hasParty(player)) {
                    partyScoreGetter.accept(player, scores)
                } else {
                    lobbyScoreboard.accept(player, scores)
                }
            }
        }
        *//*if (!scores.isEmpty()) {
            scores.addFirst("&a&7&m--------------------")
            scores.add("&f&7&m--------------------")
        }*//*
    }*/
}