package net.frozenorb.potpvp.util.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.function.Consumer;

public final class MenuBackButton extends Button {

    public final Consumer<Player> openPreviousMenuConsumer;

    public MenuBackButton(Consumer<Player> openPreviousMenuConsumer) {
        this.openPreviousMenuConsumer = Preconditions.checkNotNull(openPreviousMenuConsumer, "openPreviousMenuConsumer");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED + "Click here to return to",
            ChatColor.RED + "the previous menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        openPreviousMenuConsumer.accept(player);
    }

}