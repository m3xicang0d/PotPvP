package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby

import me.jesusmx.practice.practice.integration.scoreboard.other.Scoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.waiting.TournamentWaitingSubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.party.PartyScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.ranked.RankedSubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.unranked.UnrankedSubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.countdown.TournamentStartedCountdownSubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.countdown.TournamentStartingCountdownSubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.progress.TournamentProgressSubScoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.queue.MatchQueueEntry
import net.frozenorb.potpvp.game.tournament.Tournament.TournamentStage
import net.frozenorb.potpvp.util.CC
import org.bukkit.entity.Player
import java.util.stream.Collectors

class LobbyScoreboard : Scoreboard() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()

    private val rankedSubScoreboard = RankedSubScoreboard()
    private val unrankedSubScoreboard = UnrankedSubScoreboard()
    private val partySubScoreboard = PartyScoreboard()

    private val tournamentWaitingSubScoreboard = TournamentWaitingSubScoreboard()
    private val tournamentStartingCountdownSubScoreboard = TournamentStartingCountdownSubScoreboard()
    private val tournamentStartedCountdownSubScoreboard = TournamentStartedCountdownSubScoreboard()
    private val tournamentProgressSubScoreboard = TournamentProgressSubScoreboard()

    override fun accept(player : Player, scores : MutableList<String>) {
        val tournament = PotPvPSI.instance.tournamentHandler.getTournament()
        if(tournament == null) {
            val entry = getQueueEntry(player)
            if (entry == null) {
                if(PotPvPSI.instance.partyHandler.hasParty(player)) {
                    partySubScoreboard.accept(player, scores)
                } else {
                    scores.addAll(CC.translate(PotPvPSI.instance.scoreboardConfig.getStringList("IN-LOBBY").stream()
                            .map { it.replace("%online%", onlineVariable.format(player, true)) }
                            .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
                            .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
                            .collect(Collectors.toList())))
                }
            } else {
                val queue = entry.queue

                if (queue.ranked) {
                    rankedSubScoreboard.accept(player, scores, entry)
                } else {
                    unrankedSubScoreboard.accept(player, scores, entry)
                }
            }
        } else {
            if (tournament.stage == TournamentStage.WAITING_FOR_TEAMS) {
                tournamentWaitingSubScoreboard.accept(player, scores, tournament)
            } else if (tournament.stage == TournamentStage.COUNTDOWN) {
                if (tournament.currentRound == 0) {
                    tournamentStartingCountdownSubScoreboard.accept(player, scores, tournament)
                } else {
                    tournamentStartedCountdownSubScoreboard.accept(player, scores, tournament)
                }
            } else if (tournament.stage == TournamentStage.IN_PROGRESS) {
                tournamentProgressSubScoreboard.accept(player, scores, tournament)
            }
        }
    }

    private fun getQueueEntry(player: Player): MatchQueueEntry? {
        val partyHandler = PotPvPSI.instance.partyHandler
        val queueHandler = PotPvPSI.instance.queueHandler
        return if (partyHandler.hasParty(player)) {
            queueHandler.getQueueEntry(partyHandler.getParty(player))
        } else {
            queueHandler.getQueueEntry(player.uniqueId)
        }
    }
}