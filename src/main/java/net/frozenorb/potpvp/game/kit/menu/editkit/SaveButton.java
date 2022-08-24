package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.Kit;
import net.frozenorb.potpvp.game.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.bukkit.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class SaveButton
extends Button {
    public final Kit kit;

    SaveButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + (Object)ChatColor.BOLD + "Save";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", (Object)ChatColor.GREEN + "Click this to save your kit.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.LIME.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        this.kit.setInventoryContents(player.getInventory().getContents());
        PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);
        new KitsMenu(this.kit.getType()).openMenu(player);
        ItemStack[] defaultInventory = this.kit.getType().getDefaultInventory();
        int foodInDefault = ItemUtils.countStacksMatching(defaultInventory, v -> v.getType().isEdible());
        int pearlsInDefault = ItemUtils.countStacksMatching(defaultInventory, v -> v.getType() == Material.ENDER_PEARL);
        if (foodInDefault > 0 && this.kit.countFood() == 0) {
            player.sendMessage((Object)ChatColor.RED + "Your saved kit is missing food.");
        }
        if (pearlsInDefault > 0 && this.kit.countPearls() == 0) {
            player.sendMessage((Object)ChatColor.RED + "Your saved kit is missing enderpearls.");
        }
    }
}

