package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.queue.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.game.queue.MatchQueue
import org.bukkit.entity.Player

class KitTypeVariable : Variable<MatchQueue>() {

    override fun format(player: Player, t: MatchQueue): String {
        return t.kitType.coloredDisplayName
    }
}