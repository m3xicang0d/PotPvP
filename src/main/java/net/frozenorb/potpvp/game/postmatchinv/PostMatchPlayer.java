package net.frozenorb.potpvp.game.postmatchinv;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.frozenorb.potpvp.game.kittype.HealingMethod;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public final class PostMatchPlayer {

    @Getter public final UUID playerUuid;
    @Getter public final String lastUsername;
    @Getter public final ItemStack[] armor;
    @Getter public final ItemStack[] inventory;
    @Getter public final List<PotionEffect> potionEffects;
    @Getter public final int hunger;
    @Getter public final double health; // out of 10
    @Getter public final transient HealingMethod healingMethodUsed;
    @Getter public final int totalHits;
    @Getter public final int longestCombo;
    @Getter public final int missedPots;
    @Getter public final int ping;

    public PostMatchPlayer(Player player, HealingMethod healingMethodUsed, int totalHits, int longestCombo, int missedPots) {
        this.playerUuid = player.getUniqueId();
        this.lastUsername = player.getName();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = ImmutableList.copyOf(player.getActivePotionEffects());
        this.hunger = player.getFoodLevel();
        this.health = player.getHealth() / 2;
        this.healingMethodUsed = healingMethodUsed;
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
        this.missedPots = missedPots;
        this.ping = PlayerUtils.getPing(player);
    }
}