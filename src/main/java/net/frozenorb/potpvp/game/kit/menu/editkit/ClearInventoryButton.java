/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class ClearInventoryButton
extends Button {
    ClearInventoryButton() {
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Clear Inventory";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", ChatColor.RED + "This will clear your inventory", ChatColor.RED + "so you can start over.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.YELLOW.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.getInventory().clear();
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), ((Player)player)::updateInventory, 1L);
    }
}

