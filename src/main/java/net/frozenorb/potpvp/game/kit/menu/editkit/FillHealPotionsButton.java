package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class FillHealPotionsButton extends Button {

    public final short durability;

    FillHealPotionsButton(short durability) {
        this.durability = durability;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.BLUE + "Fill Empty Space";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", ChatColor.LIGHT_PURPLE + "Fill your empty inventory space", ChatColor.LIGHT_PURPLE + "with Splash Health Potions.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.POTION;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack superItem = super.getButtonItem(player);
        superItem.setDurability(this.durability);
        return superItem;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ItemStack potion = new ItemStack(Material.POTION);
        potion.setDurability(this.durability);
        for (int i = 0; i < player.getInventory().getSize(); ++i) {
            player.getInventory().addItem(new ItemStack[]{potion});
        }
    }
}

