package net.frozenorb.potpvp.player.party;

import lombok.experimental.UtilityClass;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.util.ItemUtils;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.ItemBuilder;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public final class PartyItems {

    private final ConfigFile config = PotPvPSI.getInstance().getHotbarConfig();

    public static final Material ICON_TYPE;

    public static final ItemStack LEAVE_PARTY_ITEM =
            (config.getBoolean("LEAVE-PARTY.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("LEAVE-PARTY.MATERIAL")))
                    .name(config.getString("LEAVE-PARTY.NAME"))
                    .data(config.getInt("LEAVE-PARTY.DATA"))
                    .lore(config.getStringList("LEAVE-PARTY.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack ASSIGN_CLASSES_PLAYER =
            (config.getBoolean("ASSING-CLASSES-PLAYER.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("ASSING-CLASSES-PLAYER.MATERIAL")))
                    .name(config.getString("ASSING-CLASSES-PLAYER.NAME"))
                    .data(config.getInt("ASSING-CLASSES-PLAYER.DATA"))
                    .lore(config.getStringList("ASSING-CLASSES-PLAYER.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack ASSIGN_CLASSES_LEADER =
            (config.getBoolean("ASSING-CLASSES-LEADER.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("ASSING-CLASSES-LEADER.MATERIAL")))
                    .name(config.getString("ASSING-CLASSES-LEADER.NAME"))
                    .data(config.getInt("ASSING-CLASSES-LEADER.DATA"))
                    .lore(config.getStringList("ASSING-CLASSES-LEADER.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack START_TEAM_SPLIT_ITEM =
            (config.getBoolean("PARTY-TEAM-SPLIT.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("PARTY-TEAM-SPLIT.MATERIAL")))
                    .name(config.getString("PARTY-TEAM-SPLIT.NAME"))
                    .data(config.getInt("PARTY-TEAM-SPLIT.DATA"))
                    .lore(config.getStringList("PARTY-TEAM-SPLIT.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack START_FFA_ITEM =
            (config.getBoolean("PARTY-TEAM-FFA.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("PARTY-TEAM-FFA.MATERIAL")))
                    .name(config.getString("PARTY-TEAM-FFA.NAME"))
                    .data(config.getInt("PARTY-TEAM-FFA.DATA"))
                    .lore(config.getStringList("PARTY-TEAM-FFA.LORE"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack OTHER_PARTIES_ITEM =
            (config.getBoolean("OTHER-PARTIES.enabled"))
                    ? new ItemBuilder(Material.valueOf(config.getString("OTHER-PARTIES.material")))
                    .name(config.getString("OTHER-PARTIES.name"))
                    .data(config.getInt("OTHER-PARTIES.data"))
                    .lore(config.getStringList("OTHER-PARTIES.lore"))
                    .build()
                    : new ItemBuilder(Material.AIR).build();

    public static final ItemStack OPEN_EDITOR_PARTY_ITEM =
            (config.getBoolean("PARTY-KIT-EDITOR.ENABLED"))
                    ? new ItemBuilder(Material.valueOf(config.getString("PARTY-KIT-EDITOR.MATERIAL")))
                    .name(config.getString("PARTY-KIT-EDITOR.NAME"))
                    .data(config.getInt("PARTY-KIT-EDITOR.DATA"))
                    .lore(config.getStringList("PARTY-KIT-EDITOR.LORE")).build()
                    : new ItemBuilder(Material.AIR).build();


    static {
        ICON_TYPE = Material.NETHER_STAR;
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);
        String leaderName = PotPvPSI.getInstance().getUuidCache().name(party.getLeader());
        String displayName = CC.translate("&b"+leaderName + "&b's Party");
        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
