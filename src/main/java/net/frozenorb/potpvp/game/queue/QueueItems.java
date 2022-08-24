package net.frozenorb.potpvp.game.queue;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class QueueItems {

    private final ConfigFile config = PotPvPSI.getInstance().getHotbarConfig();

    public static final ItemStack JOIN_SOLO_UNRANKED_QUEUE_ITEM =
            (config.getBoolean("JOIN-UNRANKED.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("JOIN-UNRANKED.MATERIAL")))
                    .name(config.getString("JOIN-UNRANKED.NAME"))
                    .data(config.getInt("JOIN-UNRANKED.DATA"))
                    .lore(config.getStringList("JOIN-UNRANKED.LORE")).build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM =
            (config.getBoolean("JOIN-RANKED.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("JOIN-RANKED.MATERIAL")))
                    .name(config.getString("JOIN-RANKED.NAME"))
                    .data(config.getInt("JOIN-RANKED.DATA"))
                    .lore(config.getStringList("JOIN-RANKED.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();


    public static final ItemStack LEAVE_SOLO_UNRANKED_QUEUE_ITEM =
            (config.getBoolean("LEAVE-UNRANKED.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("LEAVE-UNRANKED.MATERIAL")))
                    .name(config.getString("LEAVE-UNRANKED.NAME"))
                    .data(config.getInt("LEAVE-UNRANKED.DATA"))
                    .lore(config.getStringList("LEAVE-UNRANKED.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();


    public static final ItemStack LEAVE_SOLO_RANKED_QUEUE_ITEM =
            (config.getBoolean("LEAVE-RANKED.ENABLED"))
            ? new ItemBuilder(Material.valueOf(config.getString("LEAVE-RANKED.MATERIAL")))
                    .name(config.getString("LEAVE-RANKED.NAME"))
                    .data(config.getInt("LEAVE-RANKED.DATA"))
                    .lore(config.getStringList("LEAVE-RANKED.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack JOIN_SOLO_PREMIUM_QUEUE_ITEM =
            (config.getBoolean("hotbar.join-premium.enabled"))
                    ? new ItemBuilder(Material.valueOf(config.getString("hotbar.join-premium.material")))
                    .name(config.getString("hotbar.join-premium.name"))
                    .data(config.getInt("hotbar.join-premium.data"))
                    .lore(config.getStringList("hotbar.join-premium.lore"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();
}