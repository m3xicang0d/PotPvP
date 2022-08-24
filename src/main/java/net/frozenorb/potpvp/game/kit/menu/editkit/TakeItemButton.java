/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

final class TakeItemButton
extends Button {
    public final ItemStack item;

    TakeItemButton(ItemStack item) {
        this.item = Preconditions.checkNotNull(item, "item");
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return this.item;
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

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Bukkit.getScheduler().runTaskLater((Plugin)PotPvPSI.getInstance(), () -> player.getOpenInventory().getTopInventory().setItem(slot, this.item), 4L);
    }

    @Override
    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return false;
    }
}

