package me.jesusmx.practice.practice.game.modes

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.match.Match
import net.frozenorb.potpvp.game.match.event.MatchStartEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType



@Suppress("unused")
class BoxingMode : PvPMode() {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onMatchStart(event: MatchStartEvent) {
        val match = event.match
        if (!match.kitType.id.equals("Boxing", true)) return
        match.teams.forEach { team ->
            team.aliveMembers.forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200000, 1), true)
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 1), true)
            }
        }
    }

    /*@EventHandler(priority = EventPriority.MONITOR)
    fun onFoodLevenChange(event : FoodLevelChangeEvent) {
        if(event.entity !is Player) return
        val player = event.entity as Player
        if(!PotPvPSI.instance.matchHandler.isPlayingMatch(player)) return
        event.isCancelled = true
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            val damaged: Player = event.entity as Player
            val damager: Player = event.damager as Player
            val match = PotPvPSI.instance.matchHandler.getMatchPlaying(damaged) ?: return
            if (match.kitType.id.equals("Boxing", true)) {
                event.damage = 0.0
                match.boxingHits[damager.uniqueId] = match.boxingHits.getOrDefault(damager.uniqueId, 0) + 1
                checkBoxingHits(match, damager, damaged)
            }
        }
    }

    fun checkBoxingHits(match : Match, damager : Player, damaged : Player) {
        val drHits = match.boxingHits.getOrDefault(damager.uniqueId, 0)
        if(drHits >= 100) {
            damaged.health = 0.0
        }
    }

    override fun lines(match : Match, player: Player, opponent: Player): MutableList<String> {
        val scores = mutableListOf<String>()
        val ownHits: Int = match.boxingHits.getOrDefault(player.uniqueId, 0)
        val opponentHits: Int = match.boxingHits.getOrDefault(opponent.uniqueId, 0)
        scores.add("&fHits: " + formatHits(ownHits, opponentHits))
        scores.add(" &fYou: &b$ownHits")
        scores.add(" &fThem: &b$opponentHits")
        return scores
    }

    companion object {
        @JvmStatic
        fun formatHits(own: Int, opp: Int): String {
            return if (own < opp) {
                "&c-" + (opp - own)
            } else "&a+" + (own - opp)
        }
    }
}