package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.sub.tournament.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.game.tournament.Tournament
import org.bukkit.entity.Player

class TournamentTypeVariable : Variable<Tournament>() {
    override fun format(player: Player, t: Tournament): String {
        return if (t.requiredPartySize < 3) "Players" else "Teams"
    }
}