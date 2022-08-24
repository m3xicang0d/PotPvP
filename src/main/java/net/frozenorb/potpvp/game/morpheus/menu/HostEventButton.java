package net.frozenorb.potpvp.game.morpheus.menu;

import net.frozenorb.potpvp.game.morpheus.menu.parameter.HostParametersMenu;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostEventButton extends Button {

    public final GameEvent event;

    HostEventButton(GameEvent event) {
        this.event = event;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (player.hasPermission(event.getPermission())) {
            if (event.getParameters().isEmpty()) {

                if (GameQueue.INSTANCE.playerCanHostGame(player)) {
                    GameQueue.INSTANCE.add(new Game(event, player, new ArrayList<>()));
                    player.sendMessage(ChatColor.GREEN + "You've added a " + event.getName().toLowerCase() + " event to the queue.");
                }

                player.closeInventory();
            } else {
                new HostParametersMenu(event).openMenu(player);
            }
        } else {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You do not have permission to host this event.");
        }
    }

    @Override
    public String getName(Player player) {
        if (player.hasPermission(event.getPermission())) {
            return ChatColor.GREEN + event.getName();
        }
        return ChatColor.RED + event.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return Collections.singletonList(ChatColor.GRAY + event.getDescription());
    }

    @Override
    public Material getMaterial(Player player) {
        return event.getIcon().getType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) event.getIcon().getDurability();
    }
}
