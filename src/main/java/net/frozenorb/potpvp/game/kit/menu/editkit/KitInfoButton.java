package net.frozenorb.potpvp.game.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.game.kit.Kit;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class KitInfoButton
extends Button {
    public final Kit kit;

    KitInfoButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + (Object)ChatColor.BOLD + "Editing: " + (Object)ChatColor.AQUA + this.kit.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of((Object)ChatColor.GRAY + "You are editing '" + this.kit.getName() + "'");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }
}

