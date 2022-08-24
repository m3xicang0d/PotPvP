package net.frozenorb.potpvp.game.postmatchinv.menu;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.game.kittype.HealingMethod;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

final class PostMatchHealsLeftButton extends Button {

    public final UUID player;
    public final HealingMethod healingMethod;
    public final int healsRemaining;
    public final int missedHeals;

    PostMatchHealsLeftButton(UUID player, HealingMethod healingMethod, int healsRemaining, int missedHeals) {
        this.player = player;
        this.healingMethod = healingMethod;
        this.healsRemaining = healsRemaining;
        this.missedHeals = missedHeals;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + healsRemaining + " " + (healsRemaining == 1 ? healingMethod.getLongSingular() : healingMethod.getLongPlural());
    }

    @Override
    public List<String> getDescription(Player player) {
        if (healingMethod == HealingMethod.POTIONS) {
            return ImmutableList.of(
                ChatColor.YELLOW + " missed " + missedHeals + " pot" + (missedHeals == 1 ? "." : "s.")
            );
        } else {
            return ImmutableList.of();
        }

    }

    @Override
    public Material getMaterial(Player player) {
        return healingMethod.getIconType();
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = super.getButtonItem(player);
        item.setDurability(healingMethod.getIconDurability());
        return item;
    }

    @Override
    public int getAmount(Player player) {
        return healsRemaining;
    }

}