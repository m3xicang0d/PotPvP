package net.frozenorb.potpvp.game.kittype.menu.select;

import com.google.common.base.Preconditions;
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
import java.util.Map;

public final class SelectKitTypeMenu extends Menu {

    public final boolean reset;
    public final Callback<KitType> callback;
    private final String tt;

    public SelectKitTypeMenu(Callback<KitType> callback, String title) {
        this(callback, true, title);
    }

    public SelectKitTypeMenu(Callback<KitType> callback, boolean reset, String title) {
        this.tt = ChatColor.BLUE.toString() + ChatColor.BOLD + title;

        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.reset = reset;
    }

    @Override
    public void onClose(Player player) {
        if (reset) {
            InventoryUtils.resetInventoryDelayed(player);
        }
    }

    @Override
    public String getTitle(Player player) {
        return tt;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
        for (KitType kitType : KitType.getAllTypes()) {
            //pero que ponemos para que puedan ver o que?
            //A eso voy xd
            if(kitType.isHidden() && !player.isOp()) {
                continue;
            }
            /*if (kitType.isHidden() && !player.isOp()) {
                continue;
            }*/

            if (kitType.equals(KitType.teamFight) && party == null) {
                continue;
            }

            buttons.put(index++, new KitTypeButton(kitType, callback));
        }

        /*if (party != null) {
            buttons.put(8, new KitTypeButton(KitType.teamFight, callback));
        }*/

        return buttons;
    }

}