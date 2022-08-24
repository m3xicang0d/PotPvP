package net.frozenorb.potpvp.game.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.KitHandler;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class KitDeleteButton
extends Button {
    public final KitType kitType;
    public final int slot;

    KitDeleteButton(KitType kitType, int slot) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + (Object)ChatColor.BOLD + "Delete";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", (Object)ChatColor.RED + "Click here to delete this kit", (Object)ChatColor.RED + "You will " + (Object)ChatColor.BOLD + "NOT" + (Object)ChatColor.RED + " be able to", (Object)ChatColor.RED + "recover this kit.");
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
        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        kitHandler.removeKit(player, this.kitType, this.slot);
    }
}

