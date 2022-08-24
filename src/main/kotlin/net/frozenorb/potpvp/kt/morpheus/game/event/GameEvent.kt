package net.frozenorb.potpvp.kt.morpheus.game.event

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.sumo.SumoGameEvent
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.t4g.T4gGameEvent
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.kt.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

interface GameEvent {

    companion object {
        @JvmStatic val events = arrayListOf(SumoGameEvent, T4gGameEvent)
        @JvmStatic val leaveItem = ItemBuilder.of(Material.INK_SACK).data(1).name(ChatColor.RED.toString() + "Leave Event").build()
    }

    fun getName(): String
    fun getPermission(): String
    fun getDescription(): String
    fun getIcon(): ItemStack
    fun canStart(game: Game): Boolean
    fun getLogic(game: Game): GameEventLogic
    fun getNameTag(game: Game, player: Player, viewer: Player): String
    fun getScoreboardScores(player: Player, game: Game): List<String>
    fun getListeners(): List<Listener>
    fun getParameters(): List<GameParameter>
    fun getMaxInstances(): Int {
        return 1
    }

}