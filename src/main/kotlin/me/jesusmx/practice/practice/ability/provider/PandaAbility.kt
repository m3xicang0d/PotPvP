package me.jesusmx.practice.practice.ability.provider

import dev.panda.ability.PandaAbilityAPI
import me.jesusmx.practice.practice.ability.AbilitySystem
import org.bukkit.entity.Player

class PandaAbility : AbilitySystem {

    var PandaAbility = PandaAbilityAPI()

    override fun isOnGlobalCooldown(player: Player): Boolean {
        return PandaAbility.globalCooldown.hasGlobalCooldown(player)
    }

    override fun getRemaining(player: Player): String {
     return PandaAbility.globalCooldown.getGlobalCooldown(player)
    }
}