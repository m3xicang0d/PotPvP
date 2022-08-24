package net.frozenorb.potpvp.kt.morpheus.game.bukkit.event

import net.frozenorb.potpvp.kt.morpheus.game.Game
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerJoinGameEvent(val player: Player, val game: Game) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}