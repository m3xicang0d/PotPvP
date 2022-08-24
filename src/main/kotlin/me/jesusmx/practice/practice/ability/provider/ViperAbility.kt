package me.jesusmx.practice.practice.ability.provider

import me.jesusmx.practice.practice.ability.AbilitySystem
import org.bukkit.entity.Player

class ViperAbility : AbilitySystem {

    override fun isOnGlobalCooldown(player: Player): Boolean {
        return false
    }

    override fun getRemaining(player: Player): String {
        return "10s"
    }
}