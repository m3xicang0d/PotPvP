package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.progress

import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.ActualRoundVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.DurationVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.TeamSizeVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.tournament.Tournament
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors

class TournamentProgressSubScoreboard : SubScoreboard<Tournament>() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()

    private val actualRoundVariable = ActualRoundVariable()
    private val teamSizeVariable = TeamSizeVariable()
    private val durationVariable = DurationVariable()

    override fun accept(player : Player, scores : MutableList<String>, tournament : Tournament) {
        scores.addAll(PotPvPSI.instance.scoreboardConfig.getStringList("TOURNAMENT-IN-PROGRESS").stream()
            .map { it.replace("%online%", onlineVariable.format(player, true)) }
            .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
            .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
            .map { it.replace("%actual-round%", actualRoundVariable.format(player, tournament)) }
            .map { it.replace("%team-size%", teamSizeVariable.format(player, tournament)) }
            .map { it.replace("%duration-tournament%", durationVariable.format(player, tournament)) }
            .collect(Collectors.toList()))
    }
}