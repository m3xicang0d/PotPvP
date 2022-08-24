package net.frozenorb.potpvp.game.kit;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.PatchedPlayerUtils;
import net.frozenorb.potpvp.util.bukkit.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Kit {
    public String name;
    public int slot;
    public KitType type;
    public ItemStack[] inventoryContents;

    public Kit() {
    }

    public static Kit ofDefaultKitCustomName(KitType kitType, String name) {
        return ofDefaultKit(kitType, name, 0);
    }

    public static Kit ofDefaultKit(KitType kitType) {
        return ofDefaultKit(kitType, "Default Kit", 0);
    }

    public static Kit ofDefaultKit(KitType kitType, String name, int slot) {
        Kit kit = new Kit();
        kit.setName(name);
        kit.setType(kitType);
        kit.setSlot(slot);
        kit.setInventoryContents(kitType.getDefaultInventory());
        return kit;
    }

    public void apply(Player player) {
        PatchedPlayerUtils.resetInventory(player);
        player.getInventory().setArmorContents(this.type.getDefaultArmor());
        player.getInventory().setContents(this.inventoryContents);
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
    }

    public int countHeals() {
        return ItemUtils.countStacksMatching(this.inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
    }

    public int countDebuffs() {
        return ItemUtils.countStacksMatching(this.inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
    }

    public int countFood() {
        return ItemUtils.countStacksMatching(this.inventoryContents, ItemUtils.EDIBLE_PREDICATE);
    }

    public int countPearls() {
        return ItemUtils.countStacksMatching(this.inventoryContents, (v) -> {
            return v.getType() == Material.ENDER_PEARL;
        });
    }

    public boolean isSelectionItem(ItemStack itemStack) {
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            return false;
        } else {
            ItemMeta meta = itemStack.getItemMeta();
            return meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.YELLOW.toString() + ChatColor.BOLD + this.name);
        }
    }

    public ItemStack createSelectionItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + this.name);
        item.setItemMeta(itemMeta);
        return item;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public KitType getType() {
        return this.type;
    }

    public void setType(KitType type) {
        this.type = type;
    }

    public ItemStack[] getInventoryContents() {
        return this.inventoryContents;
    }

    public void setInventoryContents(ItemStack[] inventoryContents) {
        this.inventoryContents = inventoryContents;
    }
}

