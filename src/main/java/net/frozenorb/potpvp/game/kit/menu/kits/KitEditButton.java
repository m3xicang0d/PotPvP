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

final class KitEditButton extends Button {

    public final Optional<Kit> kitOpt;
    public final KitType kitType;
    public final int slot;

    KitEditButton(Optional<Kit> kitOpt, KitType kitType, int slot) {
        this.kitOpt = Preconditions.checkNotNull(kitOpt, "kitOpt");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA.toString() + ChatColor.BOLD + "Load/Edit";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of("", ChatColor.AQUA + "Click to edit this kit.");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BOOK;
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

