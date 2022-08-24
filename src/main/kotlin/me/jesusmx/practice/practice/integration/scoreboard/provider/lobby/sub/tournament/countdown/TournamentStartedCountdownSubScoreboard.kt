package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.countdown

import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.*
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.ActualRoundVariable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.tournament.Tournament
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors

class TournamentStartedCountdownSubScoreboard : SubScoreboard<Tournament>() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()

    private val actualRoundVariable = ActualRoundVariable()
    private val nextRoundVariable = NextRoundVariable()
    private val timeVariable = TimeVariable()


    override fun accept(player : Player, scores : MutableList<String>, tournament : Tournament) {
        scores.addAll(PotPvPSI.instance.scoreboardConfig.getStringList("TOURNAMENT-COUNTDOWN-STARTED").stream()
            .map { it.replace("%online%", onlineVariable.format(player, true)) }
            .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
            .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
            .map { it.replace("%actual-round%", actualRoundVariable.format(player, tournament)) }
            .map { it.replace("%next-round%", nextRoundVariable.format(player, tournament)) }
            .map { it.replace("%time%", timeVariable.format(player, tournament)) }
            .collect(Collectors.toList()))
    }
}