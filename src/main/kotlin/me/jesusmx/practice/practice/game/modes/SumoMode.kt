package me.jesusmx.practice.practice.game.modes

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.MatchState
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

class SumoMode : PvPMode() {

    @EventHandler
    fun onPlayerMove(event : PlayerMoveEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player) ?: return
        if(match.state != MatchState.COUNTDOWN) return
        if(match.sumo) {
            event.isCancelled = true
        }
    }
}