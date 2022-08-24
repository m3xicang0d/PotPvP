package net.frozenorb.potpvp.game.kit;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class KitItems {

    private static final ConfigFile config = PotPvPSI.getInstance().getHotbarConfig();

    public KitItems() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final ItemStack OPEN_EDITOR_ITEM =
            (config.getBoolean("KIT-EDITOR.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("KIT-EDITOR.MATERIAL")))
                    .name(config.getString("KIT-EDITOR.NAME"))
                    .data(config.getInt("KIT-EDITOR.DATA"))
                    .lore(config.getStringList("KIT-EDITOR.LORE")).build()
                    : new ItemBuilder(Material.AIR).build();



}


