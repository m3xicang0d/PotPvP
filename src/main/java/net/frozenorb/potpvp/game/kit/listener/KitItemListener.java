package net.frozenorb.potpvp.game.kit.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.KitItems;
import net.frozenorb.potpvp.game.kit.menu.kits.KitsMenu;
import net.frozenorb.potpvp.game.kittype.menu.select.SelectKiToEditMenu;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.util.bukkit.ItemListener;
import org.bukkit.entity.Player;

public final class KitItemListener extends ItemListener {

    public KitItemListener() {
        this.addHandler(KitItems.OPEN_EDITOR_ITEM, player -> {
            LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
            if (lobbyHandler.isInLobby(player)) {
                new SelectKiToEditMenu(kitType -> new KitsMenu(kitType).openMenu(player), "Select a kit to edit...").openMenu((Player)player);
            }
        });
    }
}