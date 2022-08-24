package me.jesusmx.practice.practice.ability.provider

import club.skilled.slade.api.SladeAbilityAPI
import me.jesusmx.practice.practice.ability.AbilitySystem
import org.bukkit.entity.Player

class SladeAbility : AbilitySystem {

    override fun isOnGlobalCooldown(player: Player): Boolean {
        return SladeAbilityAPI().globalCooldown.hasGlobalCooldown(player)

    }

    override fun getRemaining(player: Player): String {
        return SladeAbilityAPI().globalCooldown.getGlobalCooldown(player)
    }
}