package me.jesusmx.practice.practice.integration.scoreboard.provider.spectator

import me.jesusmx.practice.consumerapi.TriConsumer
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable.DurationVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable.OpponentVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable.PingVariable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.Match
import net.frozenorb.potpvp.game.match.MatchTeam
import org.bukkit.Bukkit
import org.bukkit.entity.Player


@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class SpectatorScoreboard : TriConsumer<Player, MutableList<String>, Match> {

    private val pingVariable = PingVariable()
    private val opponentVariable = OpponentVariable()
    private val durationVariable = DurationVariable()

    override fun accept(player : Player, scores : MutableList<String>, match : Match) {
        val teams: List<*> = match.teams
        if(teams.size != 2) return
        val oldTeam = match.getPreviousTeam(player.uniqueId)
        val teamOne = teams[0] as MatchTeam
        val teamTwo = teams[1] as MatchTeam
        val config = PotPvPSI.instance.scoreboardConfig
        if (teamOne.allMembers.size != 1 && teamTwo.allMembers.size != 1) {
            if (oldTeam == null) {
                scores.add("&a&lTeam A: &f" + teamOne.aliveMembers.size + "/" + teamOne.allMembers.size)
                scores.add("&c&lTeam B: &f" + teamTwo.aliveMembers.size + "/" + teamTwo.allMembers.size)
            } else {
                val otherTeam = if (oldTeam == teamOne) teamTwo else teamOne
                scores.add("&a&lTeam: &f" + oldTeam.aliveMembers.size + "/" + oldTeam.allMembers.size)
                scores.add("&c&lEnemies: &f" + otherTeam.aliveMembers.size + "/" + otherTeam.allMembers.size)
            }
        } else {
            config.getStringList("IN-SPECTATOR-MODE").stream()
                .map { it.replace("%duration%", durationVariable.format(player, match)) }
                .map { it.replace("%player-1%", PotPvPSI.instance.uuidCache.name(teamOne.firstMember)) }
                .map { it.replace("%player-2%", PotPvPSI.instance.uuidCache.name(teamTwo.firstMember)) }
                .map { it.replace("%ping-1%", pingVariable.format(player, Bukkit.getPlayer(teamOne.firstMember))) }
                .map { it.replace("%ping-2%", pingVariable.format(player, Bukkit.getPlayer(teamTwo.firstMember))) }
                .forEach(scores::add)
        }
    }
}