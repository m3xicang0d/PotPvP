package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.countdown

import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.TimeVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.tournament.Tournament
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors

class TournamentStartingCountdownSubScoreboard : SubScoreboard<Tournament>() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()

    private val timeVariable = TimeVariable()

    override fun accept(player : Player, scores : MutableList<String>, tournament : Tournament) {
        scores.addAll(PotPvPSI.instance.scoreboardConfig.getStringList("TOURNAMENT-COUNTDOWN-STARTING").stream()
            .map { it.replace("%online%", onlineVariable.format(player, true)) }
            .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
            .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
            .map { it.replace("%time%", timeVariable.format(player, tournament)) }
            .collect(Collectors.toList()))
    }
}