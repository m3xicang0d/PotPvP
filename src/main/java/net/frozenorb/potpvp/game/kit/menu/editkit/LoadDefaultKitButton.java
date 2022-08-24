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
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

final class LoadDefaultKitButton
extends Button {
    public final KitType kitType;

    LoadDefaultKitButton(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + (Object)ChatColor.BOLD + "Load default kit";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", (Object)ChatColor.YELLOW + "Click this to load the default kit", (Object)ChatColor.YELLOW + "into the kit editing menu.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.GRAY.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.getInventory().setContents(this.kitType.getDefaultInventory());
        Bukkit.getScheduler().runTaskLater((Plugin)PotPvPSI.getInstance(), ((Player)player)::updateInventory, 1L);
    }
}

