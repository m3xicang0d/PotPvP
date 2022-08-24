package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.game.tournament.Tournament
import org.bukkit.entity.Player

class ActualRoundVariable : Variable<Tournament>() {
    override fun format(player: Player, t: Tournament): String {
        return t.currentRound.toString()
    }
}