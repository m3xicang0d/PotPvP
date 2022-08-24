package net.frozenorb.potpvp.game.postmatchinv.menu;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.HealingMethod;
import net.frozenorb.potpvp.game.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.game.postmatchinv.PostMatchPlayer;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class PostMatchMenu extends Menu {

    public final PostMatchPlayer target;

    @Override
    public String getTitle(Player player) {
        return ("Showing inventory of " + PotPvPSI.getInstance().getUuidCache().name(target.getPlayerUuid()));
    }

    public PostMatchMenu(PostMatchPlayer target) {
        this.target = Preconditions.checkNotNull(target, "target");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int x = 0;
        int y = 0;

        List<ItemStack> targetInv = new ArrayList<>(Arrays.asList(target.getInventory()));

        // we want the hotbar (the first 9 items) to be at the bottom (end),
        // not the top (start) of the list, so we rotate them.
        for (int i = 0; i < 9; i++) {
            targetInv.add(targetInv.remove(0));
        }

        for (ItemStack inventoryItem : targetInv) {
            buttons.put(getSlot(x, y), Button.fromItem(inventoryItem));

            if (x++ > 7) {
                x = 0;
                y++;
            }
        }

        x = 3; // start armor backwards, helm first

        for (ItemStack armorItem : target.getArmor()) {
            buttons.put(getSlot(x--, y), Button.fromItem(armorItem));
        }

        y++; // advance line for status buttons

        int position = 0;
        buttons.put(getSlot(position++, y), new PostMatchHealthButton(target.getHealth() / 2));
        buttons.put(getSlot(position++, y), new PostMatchFoodLevelButton(target.getHunger()));
        buttons.put(getSlot(position++, y), new PostMatchPotionEffectsButton(target.getPotionEffects()));

        HealingMethod healingMethod = target.getHealingMethodUsed();

        if (healingMethod != null) {
            int count = healingMethod.count(targetInv.toArray(new ItemStack[targetInv.size()]));
            buttons.put(getSlot(position++, y), new PostMatchHealsLeftButton(target.getPlayerUuid(), healingMethod, count, target.getMissedPots()));
        }

        buttons.put(getSlot(position++, y), new PostMatchStatisticsButton(target.getTotalHits(), target.getLongestCombo()));
        // swap to other player button (for 1v1s)
        PostMatchInvHandler postMatchInvHandler = PotPvPSI.getInstance().getPostMatchInvHandler();
        Collection<PostMatchPlayer> postMatchPlayers = postMatchInvHandler.getPostMatchData(player.getUniqueId()).values();

        if (postMatchPlayers.size() == 2) {
            PostMatchPlayer otherPlayer = null;

            for (PostMatchPlayer postMatchPlayer : postMatchPlayers) {
                if (!postMatchPlayer.getPlayerUuid().equals(target.getPlayerUuid())) {
                    otherPlayer = postMatchPlayer;
                }
            }

            buttons.put(getSlot(8, y), new PostMatchSwapTargetButton(otherPlayer));
        }

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

}