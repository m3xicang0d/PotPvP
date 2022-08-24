package net.frozenorb.potpvp.game.kittype.menu.select;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class ToggleAllButton extends Button {

    public Set<String> allMaps;
    public Set<String> maps;

    @Override
    public List<String> getDescription(Player arg0) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player arg0) {
        return maps.isEmpty() ? Material.REDSTONE_TORCH_ON : Material.REDSTONE_TORCH_OFF;
    }

    @Override
    public String getName(Player arg0) {
        return maps.isEmpty() ? ChatColor.GREEN + "Enable all maps" : ChatColor.RED + "Disable all maps";
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (maps.isEmpty()) {
            maps.addAll(allMaps);
        } else {
            maps.clear();
        }
    }
}
