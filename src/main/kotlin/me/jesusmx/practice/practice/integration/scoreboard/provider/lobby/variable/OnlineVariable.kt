package me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class OnlineVariable : Variable<Boolean>() {

    override fun format(player: Player, match: Boolean): String {
        return Bukkit.getServer().onlinePlayers.size.toString()
    }
}