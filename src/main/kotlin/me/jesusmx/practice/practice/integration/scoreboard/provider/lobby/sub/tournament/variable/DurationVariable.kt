package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.game.tournament.Tournament
import net.frozenorb.potpvp.kt.util.TimeUtils.formatIntoMMSS
import org.bukkit.entity.Player

class DurationVariable : Variable<Tournament>() {
    override fun format(player: Player, t: Tournament): String {
        return formatIntoMMSS((System.currentTimeMillis() - t.roundStartedAt).toInt() / 1000)
    }
}