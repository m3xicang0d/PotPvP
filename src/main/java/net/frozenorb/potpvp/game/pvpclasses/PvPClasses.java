package net.frozenorb.potpvp.game.pvpclasses;

import lombok.Getter;
import net.frozenorb.potpvp.player.party.Party;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public enum PvPClasses {

    DIAMOND(Material.DIAMOND_CHESTPLATE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
    BARD(Material.GOLD_CHESTPLATE, 1, 2, 2),
    ARCHER(Material.LEATHER_CHESTPLATE, 1, 2, 2),
    ROGUE(Material.CHAINMAIL_CHESTPLATE, 1, 2, 2);

    @Getter public final Material icon;
    @Getter public final int maxForFive;
    @Getter public final int maxForTen;
    @Getter public final int maxForTwenty;

    PvPClasses(Material icon, int maxForFive, int maxForTen, int maxForTwenty) {
        this.icon = icon;
        this.maxForFive = maxForFive;
        this.maxForTen = maxForTen;
        this.maxForTwenty = maxForTwenty;
    }

    public boolean allowed(Party party) {
        int current = (int) party.getKits().values().stream().filter(pvPClasses -> pvPClasses == this).count();
        int size = party.getMembers().size();

        if (size < 10 && current >= maxForFive) {
            return false;
        }

        if (size < 20 && current >= maxForTen) {
            return false;
        }

        return current < maxForTwenty;
    }

    public String getName() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }

}
