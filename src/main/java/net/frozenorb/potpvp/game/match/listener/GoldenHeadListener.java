package net.frozenorb.potpvp.game.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.kt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public final class GoldenHeadListener implements Listener {

    public static final int HEALING_POINTS = 8; // half hearts, so 4 hearts
    public static final ItemStack GOLDEN_HEAD = ItemBuilder.of(Material.GOLDEN_APPLE)
        .name("&6&lGolden Head")
        .build();

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (matches(item)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, HEALING_POINTS * 25, 1), true);
        }

        // Remove absorption effect on specific
        // The delay is necessary since the effect hasn't yet been applied
        new BukkitRunnable() {
            @Override
            public void run() {
                removeAbsorptionEffect(player);
            }
        }.runTaskLater(PotPvPSI.getInstance(), 1);
    }

    public void removeAbsorptionEffect(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        if (!matchHandler.isPlayingMatch(player)) return;
        if (matchHandler.getMatchPlaying(player).getKitType().isAllowAbsorption()) return;
        player.removePotionEffect(PotionEffectType.ABSORPTION);
    }

    public boolean matches(ItemStack item) {
        return GOLDEN_HEAD.isSimilar(item);
    }

}