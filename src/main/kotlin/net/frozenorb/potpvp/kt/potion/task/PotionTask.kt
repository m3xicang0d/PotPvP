package net.frozenorb.potpvp.kt.potion.task

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.kt.potion.cache.PotionCache
import net.frozenorb.potpvp.game.kittype.HealingMethod
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
class PotionTask : BukkitRunnable() {

    override fun run() {
        val matchHandler = PotPvPSI.instance.matchHandler
        for(player in Bukkit.getServer().onlinePlayers) {
            val playing = matchHandler.getMatchPlaying(player) ?: continue
            val healingMethod: HealingMethod = playing.kitType.healingMethod ?: continue
            val count = healingMethod.count(player.inventory.contents)
            if(PotionCache.map.containsKey(player.uniqueId)) {
                PotionCache.map.replace(player.uniqueId, count)
            } else {
                PotionCache.map[player.uniqueId] = count
            }
        }
    }
}