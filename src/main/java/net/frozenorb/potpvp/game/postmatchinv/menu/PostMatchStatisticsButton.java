package net.frozenorb.potpvp.game.postmatchinv.menu;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchStatisticsButton extends Button {

    public final int totalHits;
    public final int longestCombo;

    PostMatchStatisticsButton(int totalHits, int longestCombo) {
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(ChatColor.AQUA + "Longest Combo" + ChatColor.GRAY.toString() + " - " + longestCombo + " Hit" + (longestCombo == 1 ? "" : "s"), ChatColor.AQUA + "Total Hits" + ChatColor.GRAY.toString() + " - " + totalHits + " Hit" + (totalHits == 1 ? "" : "s"));
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

}