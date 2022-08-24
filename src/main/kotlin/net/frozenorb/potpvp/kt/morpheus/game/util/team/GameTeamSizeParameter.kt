package net.frozenorb.potpvp.kt.morpheus.game.util.team

import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameterOption
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object GameTeamSizeParameter : GameParameter {

    public const val DISPLAY_NAME = "Team Size"

    override fun getDisplayName(): String {
        return DISPLAY_NAME
}

    override fun getOptions(): List<GameParameterOption> {
        return listOf(Singles, Duos)
    }

    object Singles : GameParameterOption {
        private const val DISPLAY_NAME = "1v1"
        private val icon = ItemStack(Material.DIAMOND_HELMET)

        override fun getDisplayName(): String {
            return DISPLAY_NAME
        }

        override fun getIcon(): ItemStack {
            return icon
        }
    }

    object Duos : GameParameterOption {
        private const val DISPLAY_NAME = "2v2"
        private val icon = ItemStack(Material.DIAMOND_HELMET, 2)

        override fun getDisplayName(): String {
            return DISPLAY_NAME
        }

        override fun getIcon(): ItemStack {
            return icon
        }
    }

}