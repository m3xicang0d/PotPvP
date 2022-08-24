package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.entity.Player

class InQueueVariable : Variable<Boolean>() {

    override fun format(player: Player, t: Boolean): String {
        return PotPvPSI.instance.queueHandler.queuedCount.toString()
    }
}