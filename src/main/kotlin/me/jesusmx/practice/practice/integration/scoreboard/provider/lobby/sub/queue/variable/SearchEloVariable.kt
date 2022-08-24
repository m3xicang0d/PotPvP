package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.queue.MatchQueueEntry
import net.frozenorb.potpvp.game.queue.QueueHandler
import org.bukkit.entity.Player

class SearchEloVariable : Variable<MatchQueueEntry>() {
    override fun format(player: Player, t: MatchQueueEntry): String {
        val elo: Int = PotPvPSI.instance.eloHandler.getElo(t.members, t.queue.kitType)
        val window: Int = t.waitSeconds * QueueHandler.RANKED_WINDOW_GROWTH_PER_SECOND
        return (elo + window).toString()
    }
}