package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

final class ArmorButton extends Button {

    public final ItemStack item;

    ArmorButton(ItemStack item) {
        this.item = Preconditions.checkNotNull(item, "item");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack newItem = this.item.clone();
        ItemMeta itemMeta = newItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setLore(ImmutableList.of("", ChatColor.AQUA + "This is automatically equipped."));
            newItem.setItemMeta(itemMeta);
        }
        return newItem;
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}

