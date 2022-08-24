package net.frozenorb.potpvp.game.match;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class SpectatorItems {

    // these items both do the same thing but we change the name if
    // clicking the item will reuslt in the player being removed
    // from their party. both serve the function of returning a player
    // to the lobby.

    private ConfigFile config = PotPvPSI.instance.getHotbarConfig();

    public static final ItemStack SHOW_SPECTATORS_ITEM =
            (config.getBoolean("SPECTATOR-SHOW-SPECTATORS.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-SHOW-SPECTATORS.MATERIAL")))
                    .name(config.getString("SPECTATOR-SHOW-SPECTATORS.NAME"))
                    .data(config.getInt("SPECTATOR-SHOW-SPECTATORS.DATA"))
                    .lore(config.getStringList("SPECTATOR-SHOW-SPECTATORS.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();


    public static final ItemStack HIDE_SPECTATORS_ITEM =
            (config.getBoolean("SPECTATOR-HIDE-SPECTATORS.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-HIDE-SPECTATORS.MATERIAL")))
                    .name(config.getString("SPECTATOR-HIDE-SPECTATORS.NAME"))
                    .data(config.getInt("SPECTATOR-HIDE-SPECTATORS.DATA"))
                    .lore(config.getStringList("SPECTATOR-HIDE-SPECTATORS.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack VIEW_INVENTORY_ITEM =
            (config.getBoolean("SPECTATOR-VIEW-INVENTORY.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-VIEW-INVENTORY.MATERIAL")))
                    .name(config.getString("SPECTATOR-VIEW-INVENTORY.NAME"))
                    .data(config.getInt("SPECTATOR-VIEW-INVENTORY.DATA"))
                    .lore(config.getStringList("SPECTATOR-VIEW-INVENTORY.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack RETURN_TO_LOBBY_ITEM =
           (config.getBoolean("RETURN-LOBBY.ENABLED"))
                   ? new ItemBuilder(Material.valueOf(config.getString("RETURN-LOBBY.MATERIAL")))
                   .name(config.getString("RETURN-LOBBY.NAME"))
                   .data(config.getInt("RETURN-LOBBY.DATA"))
                   .lore(config.getStringList("RETURN-LOBBY.LORE"))
                   .build()
                   : new ItemBuilder(Material.AIR).build();

    public static final ItemStack LEAVE_PARTY_ITEM =
            (config.getBoolean("SPECTATOR-LEAVE-PARTY.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-LEAVE-PARTY.MATERIAL")))
                    .name(config.getString("SPECTATOR-LEAVE-PARTY.NAME"))
                    .data(config.getInt("SPECTATOR-LEAVE-PARTY.DATA"))
                    .lore(config.getStringList("SPECTATOR-LEAVE-PARTY.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack SPECTATE_RANDOM_PARTY_ITEM =
            (config.getBoolean("SPECTATOR-RANDOM-MATCH.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-RANDOM-MATCH.MATERIAL")))
                    .name(config.getString("SPECTATOR-RANDOM-MATCH.NAME"))
                    .data(config.getInt("SPECTATOR-RANDOM-MATCH.DATA"))
                    .lore(config.getStringList("SPECTATOR-RANDOM-MATCH.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static ItemStack SPECTATE_MENU_PARTY_ITEM =
            (config.getBoolean("SPECTATOR-MENU-PLAYERS.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("SPECTATOR-MENU-PLAYERS.MATERIAL")))
                    .name(config.getString("SPECTATOR-MENU-PLAYERS.NAME"))
                    .data(config.getInt("SPECTATOR-MENU-PLAYERS.DATA"))
                    .lore(config.getStringList("SPECTATOR-MENU-PLAYERS.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();



    public static ItemStack getSpectatorToggler(boolean canSeeSpectators) {
        return canSeeSpectators ? HIDE_SPECTATORS_ITEM : SHOW_SPECTATORS_ITEM;
    }
}