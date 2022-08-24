package me.jesusmx.practice.practice.integration.scoreboard.provider.match

import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.entity.Player
import java.util.*
import java.util.function.BiConsumer


class FollowingScoreboard : BiConsumer<Player, MutableList<String>> {

    override fun accept(player : Player, scores : MutableList<String>) {
        scores.add("&a&lNow Following: *&f" + PotPvPSI.instance.uuidCache.name(PotPvPSI.instance.followHandler.getFollowing(player).get()))
    }
}