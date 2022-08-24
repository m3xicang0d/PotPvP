package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.ranked

import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable.KitTypeVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable.SearchEloVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable.TimeVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable.YourEloVariable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.queue.MatchQueueEntry
import net.frozenorb.potpvp.util.CC
import org.bukkit.entity.Player
import java.util.stream.Collectors

class RankedSubScoreboard : SubScoreboard<MatchQueueEntry>() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()
    private val kitTypeVariable = KitTypeVariable()
    private val timeVariable = TimeVariable()
    private val yourEloVariable = YourEloVariable()
    private val searchEloVariable = SearchEloVariable()

    override fun accept(player : Player, scores : MutableList<String>, entry : MatchQueueEntry) {
        scores.addAll(
            CC.translate(PotPvPSI.instance.scoreboardConfig.getStringList("IN-LOBBY-RANKED-QUEUE").stream()
                .map { it.replace("%online%", onlineVariable.format(player, true)) }
                .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
                .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
                .map { it.replace("%kittype%", kitTypeVariable.format(player, entry.queue)) }
                .map { it.replace("%time%", timeVariable.format(player, entry)) }
                .map { it.replace("%your-elo%", yourEloVariable.format(player, entry)) }
                .map { it.replace("%search-elo%", searchEloVariable.format(player, entry)) }
                .collect(Collectors.toList())))
    }
}