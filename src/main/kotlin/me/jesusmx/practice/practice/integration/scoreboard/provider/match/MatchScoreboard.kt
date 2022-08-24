package me.jesusmx.practice.practice.integration.scoreboard.provider.match

import me.jesusmx.practice.practice.integration.scoreboard.provider.match.sub.fight.EndingSubScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.sub.fight.FighterScoreboard
import me.jesusmx.practice.practice.integration.scoreboard.other.Scoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.spectator.SpectatorScoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.Match
import net.frozenorb.potpvp.game.match.MatchState
import org.bukkit.entity.Player


class MatchScoreboard : Scoreboard() {

    private val followingScoreboard = FollowingScoreboard()

    private val fighterScoreboard = FighterScoreboard()
    private val spectatorScoreboard = SpectatorScoreboard()
    private val endingSubScoreboard = EndingSubScoreboard()

    override fun accept(player : Player, scores : MutableList<String>) {
        val match : Match = PotPvPSI.instance.matchHandler.getMatchPlayingOrSpectating(player)
        if(match.getTeam(player.uniqueId) != null) {
            if(match.state == MatchState.ENDING) {
                endingSubScoreboard.accept(player, scores, match)
            } else {
                fighterScoreboard.accept(player, scores, match)
            }
             //Playing
        } else {
            //Spectating
            spectatorScoreboard.accept(player, scores, match)
        }

        if (match.teams.size != 2) return

        //Following Options
        if (PotPvPSI.instance.followHandler.getFollowing(player).isPresent) followingScoreboard.accept(player, scores)
    }
}
