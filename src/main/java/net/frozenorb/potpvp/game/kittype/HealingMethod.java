package net.frozenorb.potpvp.game.kittype;

import lombok.Getter;
import net.frozenorb.potpvp.util.bukkit.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public enum HealingMethod {

    POTIONS(
        "pot", "pots", // short singular/plural
        "health potion", "health potions", // long singular/plural
        Material.POTION,
        (short) 16421, // data for splash healing II pot
        i -> ItemUtils.countStacksMatching(i, ItemUtils.INSTANT_HEAL_POTION_PREDICATE)
    ),
    GOLDEN_APPLE(
        "gap", "gaps", // short singular/plural
        "golden apple", "golden apples", // long singular/plural
        Material.GOLDEN_APPLE,
        (short) 1, // data for enchanted golden apple
        items -> {
            int count = 0;

            for (ItemStack item : items) {
                if (item != null && item.getType() == Material.GOLDEN_APPLE && item.getData().getData() == (byte) 1) {
                    count += Math.max(1, item.getAmount());
                }
            }

            return count;
        }
    ),
    SOUP(
        "soup", "soup", // short singular/plural
        "soup", "soup", // long singular/plural
        Material.MUSHROOM_SOUP,
        (short) 0,
        i -> ItemUtils.countStacksMatching(i, ItemUtils.SOUP_PREDICATE)
    );

    @Getter public final String shortSingular;
    @Getter public final String shortPlural;
    @Getter public final String longSingular;
    @Getter public final String longPlural;

    @Getter public final Material iconType;
    @Getter public final short iconDurability;
    public final Function<ItemStack[], Integer> countFunction;

    HealingMethod(String shortSingular, String shortPlural, String longSingular, String longPlural, Material iconType, short iconDurability, Function<ItemStack[], Integer> countFunction) {
        this.shortSingular = shortSingular;
        this.shortPlural = shortPlural;
        this.longSingular = longSingular;
        this.longPlural = longPlural;
        this.iconType = iconType;
        this.iconDurability = iconDurability;
        this.countFunction = countFunction;
    }

    public int count(ItemStack[] items) {
        return countFunction.apply(items);
    }

}
