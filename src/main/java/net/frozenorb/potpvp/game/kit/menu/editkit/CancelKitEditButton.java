/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.InventoryView
 */
package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.game.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class CancelKitEditButton
extends Button {
    public final KitType kitType;

    CancelKitEditButton(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Cancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", ChatColor.RED + ChatColor.BOLD.toString() + "Click this to abort editing your kit,", ChatColor.RED + "and return to the kit menu.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.RED.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);
        new KitsMenu(this.kitType).openMenu(player);
    }
}

