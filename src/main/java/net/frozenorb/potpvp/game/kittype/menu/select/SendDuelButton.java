package net.frozenorb.potpvp.game.kittype.menu.select;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.kt.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

public class SendDuelButton extends Button {

    public Set<String> maps;
    public Callback<Set<String>> mapsCallback;

    public List<String> getDescription(Player arg0) {
        return ImmutableList.of();
    }

    public Material getMaterial(Player arg0) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player arg0) {
        return DyeColor.LIME.getWoolData();
    }

    public String getName(Player player) {
        return ChatColor.GREEN + "Send duel";
    }

    public void clicked(Player player, int slot, ClickType clickType) {
        if (this.maps.size() < 2) {
            player.sendMessage(ChatColor.RED + "You must select at least two maps.");
        } else {
            this.mapsCallback.callback(this.maps);
        }
    }

    @ConstructorProperties({"maps", "mapsCallback"})
    public SendDuelButton(Set<String> maps, Callback<Set<String>> mapsCallback) {
        this.maps = maps;
        this.mapsCallback = mapsCallback;
    }
}
