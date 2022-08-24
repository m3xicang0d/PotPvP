package me.jesusmx.practice.practice.integration.scoreboard.provider.game

import net.frozenorb.potpvp.kt.morpheus.game.GameQueue.getCurrentGame
import me.jesusmx.practice.practice.integration.scoreboard.other.Scoreboard
import org.bukkit.entity.Player
import java.util.*

class GameScoreGetter : Scoreboard() {

    override fun accept(player: Player, scores: MutableList<String>) {
        val game = getCurrentGame(player) ?: return
        if (!game.players.contains(player)) return
        scores.addAll(game.event.getScoreboardScores(player, game))
    }
}