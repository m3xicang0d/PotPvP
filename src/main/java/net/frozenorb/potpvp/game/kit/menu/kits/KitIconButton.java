package net.frozenorb.potpvp.game.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.Kit;
import net.frozenorb.potpvp.game.kit.KitHandler;
import net.frozenorb.potpvp.game.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Optional;

final class KitIconButton
extends Button {
    public final Optional<Kit> kitOpt;
    public final KitType kitType;
    public final int slot;

    KitIconButton(Optional<Kit> kitOpt, KitType kitType, int slot) {
        this.kitOpt = Preconditions.checkNotNull(kitOpt, "kitOpt");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA.toString() + (Object)ChatColor.BOLD + this.kitOpt.map(Kit::getName).orElse("Create Kit");
    }

    @Override
    public List<String> getDescription(Player player) {
        return this.kitOpt.map(kit -> ImmutableList.of("", (Object)ChatColor.GREEN + "Heals: " + (Object)ChatColor.WHITE + kit.countHeals(), (Object)ChatColor.RED + "Debuffs: " + (Object)ChatColor.WHITE + kit.countDebuffs())).orElse(ImmutableList.of());
    }

    @Override
    public Material getMaterial(Player player) {
        return this.kitOpt.isPresent() ? Material.DIAMOND_SWORD : Material.STONE_SWORD;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Kit resolvedKit = this.kitOpt.orElseGet(() -> {
            KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
            return kitHandler.saveDefaultKit(player, this.kitType, this.slot);
        });
        new EditKitMenu(resolvedKit).openMenu(player);
    }
}

