package net.frozenorb.potpvp.kt.morpheus.game.bukkit.event

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class GameStateChangeEvent(val game: Game, val to: GameState) : Event() {

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }

    init {
        game.state = to
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}