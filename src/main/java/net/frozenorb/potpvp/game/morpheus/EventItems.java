package net.frozenorb.potpvp.game.morpheus;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.kt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.md_5.bungee.api.ChatColor.LIGHT_PURPLE;

@UtilityClass
public final class EventItems {

    public static ItemStack getEventItem() {
        List<Game> game = GameQueue.INSTANCE.getCurrentGames();

        if (game.size() > 0) {
            return ItemBuilder.of(Material.EMERALD).name(LIGHT_PURPLE + "Join An Event").build();
        }

        return null;
    }

}