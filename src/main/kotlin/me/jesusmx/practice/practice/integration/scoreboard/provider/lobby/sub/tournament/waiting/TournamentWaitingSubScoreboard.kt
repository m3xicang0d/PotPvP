package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.waiting

import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.*
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.AlreadyVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.MaxVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.TeamSizeVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable.TournamentTypeVariable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.tournament.Tournament
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors
class TournamentWaitingSubScoreboard : SubScoreboard<Tournament>() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()

    private val kitTypeVariable = KitTypeVariable()
    private val teamSizeVariable = TeamSizeVariable()
    private val tournamentTypeVariable = TournamentTypeVariable()
    private val alreadyVariable = AlreadyVariable()
    private val maxVariable = MaxVariable()

    override fun accept(player : Player, scores : MutableList<String>, tournament : Tournament) {
        scores.addAll(PotPvPSI.instance.scoreboardConfig.getStringList("TOURNAMENT-WAITING-TEAMS").stream()
            .map { it.replace("%online%", onlineVariable.format(player, true)) }
            .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
            .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
            .map { it.replace("%kittype%", kitTypeVariable.format(player, tournament)) }
            .map { it.replace("%team-size%", teamSizeVariable.format(player, tournament)) }
            .map { it.replace("%type-tournament%", tournamentTypeVariable.format(player, tournament)) }
            .map { it.replace("%already%", alreadyVariable.format(player, tournament)) }
            .map { it.replace("%max%", maxVariable.format(player, tournament)) }
            .collect(Collectors.toList()))    }
}