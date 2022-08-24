package me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.kt.util.PlayerUtils
import org.bukkit.entity.Player

class PingVariable : Variable<Player?>() {
    override fun format(player: Player, p: Player?): String {
        if(p == null) {
            return "Connection Lost"
        }
        return PlayerUtils.getPing(p).toString()
    }
}