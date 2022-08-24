package net.frozenorb.potpvp.util.bukkit;

import net.frozenorb.potpvp.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author UKry
 * @Project Practice
 **/

public class SkullBuilder {

    private final ItemStack stack;
    private final SkullMeta meta;

    public SkullBuilder(Player player) {
        this.stack = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
        this.meta = (SkullMeta) stack.getItemMeta();
        this.meta.setOwner(player.getName());
    }

    public SkullBuilder setName(String name) {
        this.meta.setDisplayName(CC.translate(name));
        return this;
    }

    public SkullBuilder setLore(List<String> lore) {
        this.meta.setLore(CC.translate(lore));
        return this;
    }

    public SkullBuilder setLore(String[] lore) {
        this.meta.setLore(Arrays.asList(lore));
        return this;
    }

    public SkullBuilder addLore(String input) {
        List<String> lore = this.meta.getLore();
        if(lore == null) lore = new ArrayList<>();
        lore.add(input);
        this.meta.setLore(lore);
        return this;
    }

    private void uM() {
        this.stack.setItemMeta(this.meta);
    }

    public ItemStack build() {
        this.uM();
        return this.stack;
    }
}