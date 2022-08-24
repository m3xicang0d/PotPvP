package net.frozenorb.potpvp.kt.morpheus.game.event.impl.lms

import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameterOption
import net.frozenorb.potpvp.game.kittype.KitType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object LastManStandingGameKitParameter : GameParameter {

    private const val DISPLAY_NAME = "Kit"
    private val options = listOf(
            LastManStandingGameKitOption(KitType.byId("teamfight"))
    )

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun getOptions(): List<GameParameterOption> {
        return options
    }

    class LastManStandingGameKitOption(val kit: KitType) : GameParameterOption {
        override fun getDisplayName(): String {
            return kit.displayName
        }

        override fun getIcon(): ItemStack {
            val icon = ItemStack(kit.icon.itemType)

            icon.data = kit.icon

            return icon
        }

        public fun getItems(): Array<ItemStack> {
            return kit.defaultInventory
        }

        public fun getArmor(): Array<ItemStack> {
            return kit.defaultArmor
        }

        fun apply(player: Player) {
            player.health = player.maxHealth
            player.foodLevel = 20
            player.inventory.armorContents = getArmor()
            player.inventory.contents = getItems()

            player.updateInventory()
        }
    }

}