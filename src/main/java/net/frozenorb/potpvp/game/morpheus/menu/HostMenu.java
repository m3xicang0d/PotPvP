package net.frozenorb.potpvp.game.morpheus.menu;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HostMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return (CC.translate(PotPvPSI.getInstance().getConfig().getString("TITLES.EVENT")));
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (GameEvent event : GameEvent.getEvents()) {
            toReturn.put(toReturn.size(), new HostEventButton(event));
        }

        return toReturn;
    }

}
