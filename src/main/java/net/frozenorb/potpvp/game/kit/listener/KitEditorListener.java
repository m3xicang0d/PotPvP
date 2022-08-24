package net.frozenorb.potpvp.game.kit.listener;

import net.frozenorb.potpvp.game.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * "Modifications" needed to make the EditKitMenu work as expected
 */
public final class KitEditorListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }
        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) {
            event.setCancelled(true);
        }
    }
}
