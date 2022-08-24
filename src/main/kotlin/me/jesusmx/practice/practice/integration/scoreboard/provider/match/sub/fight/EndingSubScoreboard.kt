package me.jesusmx.practice.practice.integration.scoreboard.provider.match.sub.fight

import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.Match
import org.bukkit.entity.Player
import java.util.*

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class EndingSubScoreboard : SubScoreboard<Match>() {

    override fun accept(player : Player, scores : MutableList<String>, match : Match) {
        PotPvPSI.instance.scoreboardConfig.getStringList("IN-MATCH-END").forEach(scores::add)
    }
}