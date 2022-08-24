package me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.Match
import net.frozenorb.potpvp.util.CC
import org.bukkit.entity.Player

class OpponentVariable : Variable<Match>() {

    override fun format(player: Player, match: Match): String {
        val teams = match.teams
        val ourTeam = match.getTeam(player.uniqueId)
        val otherTeam = if (teams[0] == ourTeam) teams[1] else teams[0]

        return CC.translate(config.getString("GLOBAL.OPPONENT")
            .replace("%match_opponent%", PotPvPSI.instance.uuidCache.name(otherTeam.firstMember)))
    }
}