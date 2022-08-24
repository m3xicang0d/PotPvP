package net.frozenorb.potpvp.commands;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EditPotionModifyCommand {

    @Command(names = {"editPotion modify"}, permission = "op")
    public static void editPotionModify(Player sender, @Param(name = "effect") String effect, @Param(name = "seconds") int seconds, @Param(name = "amplifier") int amplifier) {
        PotionEffectType effectType = PotionEffectType.getByName(effect.toUpperCase());
        ItemStack hand = sender.getItemInHand();

        if (hand == null || hand.getType() != Material.POTION) {
            sender.sendMessage(ChatColor.RED + "Please hold a potion.");
            return;
        }

        if (effectType == null) {
            sender.sendMessage(ChatColor.RED + "Could not parse " + effect);
            return;
        }

        PotionMeta meta = (PotionMeta) hand.getItemMeta();
        meta.addCustomEffect(new PotionEffect(effectType, seconds * 20, amplifier), true);
        hand.setItemMeta(meta);

        sender.sendMessage(ChatColor.RED + "Modified effect " + effectType.getName() + ": Level " + amplifier + " for " + seconds + " seconds.");
        sender.updateInventory();
    }

}