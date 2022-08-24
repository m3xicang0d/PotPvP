package net.frozenorb.potpvp.lobby;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class LobbyItems {

    private final ConfigFile config = PotPvPSI.getInstance().getHotbarConfig();

    public static ItemStack ENABLE_SPEC_MODE_ITEM =
            (config.getBoolean("SPECTATOR-MODE-ON.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-MODE-ON.MATERIAL")))
                    .name(config.getString("SPECTATOR-MODE-ON.NAME"))
                    .data(config.getInt("SPECTATOR-MODE-ON.DATA"))
                    .lore(config.getStringList("SPECTATOR-MODE-ON.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack SPECTATE_RANDOM_ITEM =
            (config.getBoolean("SPECTATOR-RANDOM.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-RANDOM.MATERIAL")))
                    .name(config.getString("SPECTATOR-RANDOM.NAME"))
                    .data(config.getInt("SPECTATOR-RANDOM.DATA"))
                    .lore(config.getStringList("SPECTATOR-RANDOM.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack DISABLE_SPEC_MODE_ITEM =
            (config.getBoolean("SPECTATOR-MODE-OFF.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-MODE-OFF.MATERIAL")))
                    .name(config.getString("SPECTATOR-MODE-OFF.NAME"))
                    .data(config.getInt("SPECTATOR-MODE-OFF.DATA"))
                    .lore(config.getStringList("SPECTATOR-MODE-OFF.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack SPECTATE_MENU_ITEM =
            (config.getBoolean("SPECTATOR-MENU.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-MENU.MATERIAL")))
                    .name(config.getString("SPECTATOR-MENU.NAME"))
                    .data(config.getInt("SPECTATOR-MENU.DATA"))
                    .lore(config.getStringList("SPECTATOR-MENU.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack MANAGE_ITEM =
            (config.getBoolean("PRACTICE-SETTINGS.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("PRACTICE-SETTINGS.MATERIAL")))
                    .name(config.getString("PRACTICE-SETTINGS.NAME"))
                    .data(config.getInt("PRACTICE-SETTINGS.DATA"))
                    .lore(config.getStringList("PRACTICE-SETTINGS.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();


    public static ItemStack UNFOLLOW_ITEM =
            (config.getBoolean("UNFOLLOW-MODE.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("UNFOLLOW-MODE.MATERIAL")))
                    .name(config.getString("UNFOLLOW-MODE.NAME"))
                    .data(config.getInt("UNFOLLOW-MODE.DATA"))
                    .lore(config.getStringList("UNFOLLOW-MODE.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack PARTY_CREATE_ITEM =
            (config.getBoolean("PARTY-CREATE.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("PARTY-CREATE.MATERIAL")))
                    .name(config.getString("PARTY-CREATE.NAME"))
                    .data(config.getInt("PARTY-CREATE.DATA"))
                    .lore(config.getStringList("PARTY-CREATE.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack LEADERBOARDS_ITEM =
            (config.getBoolean("LEADERBOARDS.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("LEADERBOARDS.MATERIAL")))
                    .name(config.getString("LEADERBOARDS.NAME"))
                    .data(config.getInt("LEADERBOARDS.DATA"))
                    .lore(config.getStringList("LEADERBOARDS.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack EVENTS_ITEM =
            (config.getBoolean("HOST-EVENTS.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("HOST-EVENTS.MATERIAL")))
                    .name(config.getString("HOST-EVENTS.NAME"))
                    .data(config.getInt("HOST-EVENTS.DATA"))
                    .lore(config.getStringList("HOST-EVENTS.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

}