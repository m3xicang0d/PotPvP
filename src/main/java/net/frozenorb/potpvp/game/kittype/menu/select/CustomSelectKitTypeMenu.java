package net.frozenorb.potpvp.game.kittype.menu.select;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.kt.util.Callback;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Similar to {@link SelectKitTypeMenu} but allows the user to set custom
 * descriptions/item counts for each KitType. For example, this is used by
 * the queue system to show the number of players in each queue prior to joining.
 */
public final class CustomSelectKitTypeMenu extends Menu {

    public final Callback<KitType> callback;
    public final Function<KitType, CustomKitTypeMeta> metaFunc;
    public final boolean ranked;
    private final String tt;

    @Override
    public String getTitle(Player player) {
        return tt;
    }

    /*    @Override
    public String getTitle(Player player) {
        return (ChatColor.RED + "");
    }*/

    public CustomSelectKitTypeMenu(Callback<KitType> callback, Function<KitType, CustomKitTypeMeta> metaFunc, String title , boolean ranked) {
        this.tt = title;
        setAutoUpdate(true);
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.metaFunc = Preconditions.checkNotNull(metaFunc, "metaFunc");
        this.ranked = ranked;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isHidden()) {
                continue;
            }

            if (kitType.equals(KitType.teamFight)) {
                continue;
            }

            if (ranked && !kitType.isSupportsRanked()) {
                continue;
            }

            CustomKitTypeMeta meta = metaFunc.apply(kitType);
            buttons.put(index++, new KitTypeButton(kitType, callback, meta.getDescription(), meta.getQuantity()));
        }

        return buttons;
    }

    @AllArgsConstructor
    public static final class CustomKitTypeMeta {

        @Getter public int quantity;
        @Getter public List<String> description;

    }

}