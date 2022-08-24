package me.jesusmx.practice.practice.integration.scoreboard.other

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.entity.Player

abstract class Variable<T> {

    val config = PotPvPSI.instance.scoreboardConfig

    abstract fun format(player : Player, t : T) : String

}