package net.frozenorb.potpvp.game.postmatchinv.menu;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchHealthButton extends Button {

    public final double health;

    PostMatchHealthButton(double health) {
        this.health = health;
    }

    public ChatColor getHealthColor(double health) {
        ChatColor healthColor;

        if (health > 10) {
            healthColor = ChatColor.GREEN;
        } else if (health > 6) {
            healthColor = ChatColor.YELLOW;
        } else if (health > 4) {
            healthColor = ChatColor.GOLD;
        } else if (health > 1.5) {
            healthColor = ChatColor.RED;
        } else {
            healthColor = ChatColor.DARK_RED;
        }

        return healthColor;
    }

    @Override
    public String getName(Player player) {
        return getHealthColor(health).toString() + Math.floor(health * 2) / 2 + ChatColor.GRAY + " / " + ChatColor.GREEN + "10";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SPECKLED_MELON;
    }

    @Override
    public int getAmount(Player player) {
        return (int) Math.ceil(health);
    }

}