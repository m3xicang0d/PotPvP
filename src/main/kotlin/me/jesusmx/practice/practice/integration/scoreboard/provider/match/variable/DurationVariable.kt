package me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable

import net.frozenorb.potpvp.game.match.Match
import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.kt.util.TimeUtils
import net.frozenorb.potpvp.util.CC
import org.bukkit.entity.Player
import java.time.Instant
import java.time.temporal.ChronoUnit


class DurationVariable : Variable<Match>() {

    override fun format(player: Player, match: Match): String {
        return CC.translate(config.getString("GLOBAL.DURATION")
            .replace("%match_duration%", getDuration(match)))
    }

    private fun getDuration(match: Match): String {
        val startedAt = match.startedAt
        val endedAt = match.endedAt
        if (startedAt == null) return CC.translate(config.getString("GLOBAL.STARTING-MATCH"))
        return TimeUtils.formatLongIntoMMSS(
            ChronoUnit.SECONDS.between(
                startedAt.toInstant(),
                if (endedAt == null) Instant.now() else endedAt.toInstant()
            )
        )
    }


}