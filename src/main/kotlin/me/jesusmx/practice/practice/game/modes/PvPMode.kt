package me.jesusmx.practice.practice.game.modes

import org.bukkit.Bukkit
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.Match
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class PvPMode : Listener {

    //Pendiente
    open fun lines(match: Match, player: Player, opponent: Player): List<String?>? {
        return null
    }

    fun register() {
        Bukkit.getServer().pluginManager.registerEvents(this, PotPvPSI.instance)
//        println("Registered ${javaClass.name}")
    }
}