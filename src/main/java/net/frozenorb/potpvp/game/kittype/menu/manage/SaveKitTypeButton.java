package net.frozenorb.potpvp.game.kittype.menu.manage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class SaveKitTypeButton extends Button {

    public final KitType type;

    SaveKitTypeButton(KitType type) {
        this.type = type;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Save";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click this to save the kit type."
        );
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
        ItemStack[] fullInv = player.getOpenInventory().getTopInventory().getContents();
        List<ItemStack> kitInventory = Lists.newArrayList();

        for (int y = 2; y <= 5; y++) {
            for (int x = 0; x <= 8; x++) {
                kitInventory.add(fullInv[9 * y + x]);
            }
        }

        type.setEditorItems(kitInventory.toArray(new ItemStack[kitInventory.size()]));
        type.saveAsync();

        player.closeInventory();
    }
}
