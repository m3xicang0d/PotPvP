package net.frozenorb.potpvp.kt.morpheus.game.bukkit.event

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerQuitGameEvent(val player: Player, val game: Game) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    init {
        Bukkit.getScheduler().runTaskLater(PotPvPSI.instance, {
            if (player != null && player.isOnline) {
                PotPvPSI.instance.lobbyHandler.returnToLobby(player)
            }
            game.players.remove(player)
            game.spectators.remove(player)
        }, 2L)
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}