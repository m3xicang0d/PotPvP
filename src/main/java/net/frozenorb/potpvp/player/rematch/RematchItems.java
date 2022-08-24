package net.frozenorb.potpvp.player.rematch;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.GREEN;

@UtilityClass
public final class RematchItems {

    private final ConfigFile config = PotPvPSI.getInstance().getHotbarConfig();

    public static final ItemStack REQUEST_REMATCH_ITEM =
            (config.getBoolean("REQUEST-REMATCH.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("REQUEST-REMATCH.MATERIAL")))
                    .name(config.getString("REQUEST-REMATCH.NAME"))
                    .data(config.getInt("REQUEST-REMATCH.DATA"))
                    .lore(config.getStringList("REQUEST-REMATCH.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack SENT_REMATCH_ITEM =
            (config.getBoolean("SENT-REMATCH.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SENT-REMATCH.MATERIAL")))
                    .name(config.getString("SENT-REMATCH.NAME"))
                    .data(config.getInt("SENT-REMATCH.DATA"))
                    .lore(config.getStringList("SENT-REMATCH.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();


    public static final ItemStack ACCEPT_REMATCH_ITEM =
            (config.getBoolean("ACCEPT-REMATCH.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("ACCEPT-REMATCH.MATERIAL")))
                    .name(config.getString("ACCEPT-REMATCH.NAME"))
                    .data(config.getInt("ACCEPT-REMATCH.DATA"))
                    .lore(config.getStringList("ACCEPT-REMATCH.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();


    public static final ItemStack ACCEPT_DUEL_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack DUEL_SELECTOR = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

    static {
        ItemUtils.setDisplayName(DUEL_SELECTOR, GREEN + "Duel Selector");
        ItemUtils.setDisplayName(ACCEPT_DUEL_ITEM, GREEN + "Accept ");
    }

}