package net.frozenorb.potpvp.game.arena.menu.select;

import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.kt.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class ArenaButton extends Button {

    public String mapName;
    public Callback<ArenaSchematic> mapCallback;


    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + mapName;
    }

    @Override
    public List<String> getDescription(Player var1) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return PotPvPSI.getInstance().getArenaHandler()
            .getSchematic(mapName).getArenaItem();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        mapCallback.callback(PotPvPSI.getInstance().getArenaHandler().getSchematic(mapName));
    }
}
