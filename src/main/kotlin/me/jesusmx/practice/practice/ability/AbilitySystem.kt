package me.jesusmx.practice.practice.ability

import org.bukkit.entity.Player

interface AbilitySystem {

    fun isOnGlobalCooldown(player : Player) : Boolean

    fun getRemaining(player : Player) : String
}