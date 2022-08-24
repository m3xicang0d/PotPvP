package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.game.queue.MatchQueueEntry
import net.frozenorb.potpvp.kt.util.TimeUtils.formatIntoMMSS
import org.bukkit.entity.Player

class TimeVariable : Variable<MatchQueueEntry>() {
    override fun format(player: Player, t: MatchQueueEntry): String {
        return formatIntoMMSS(t.waitSeconds)
    }
}