package net.frozenorb.potpvp.game.morpheus.menu.parameter;

import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent;
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter;
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameterOption;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class HostParametersMenu extends Menu {

    public final GameEvent event;
    public final List<HostParameterButton> buttons = new ArrayList<>();

    @Override
    public String getTitle(Player player) {
        return (ChatColor.DARK_PURPLE + event.getName() + " options");
    }

    public HostParametersMenu(GameEvent event) {
        setUpdateAfterClick(true);
        setPlaceholder(true);

        for (GameParameter parameter : event.getParameters()) {
            buttons.add(new HostParameterButton(parameter));
        }

        this.event = event;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (HostParameterButton button : buttons) {
            toReturn.put(toReturn.size(), button);
        }

        toReturn.put(8, new Button() { // todo change although i doubt one event would ever have more than 8 parameters lol
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Start " + event.getName();
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList(ChatColor.GRAY + "Click to start the event.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {

                if (GameQueue.INSTANCE.playerCanHostGame(player)) {
                    List<GameParameterOption> options = new ArrayList<>();

                    for (HostParameterButton hostParameterButton : buttons) {
                        options.add(hostParameterButton.getSelectedOption());
                    }

                    GameQueue.INSTANCE.add(new Game(event, player, options));
                    player.sendMessage(ChatColor.GREEN + "You've added a " + event.getName().toLowerCase() + " event to the queue.");
                }

                player.closeInventory();
            }
        });

        return toReturn;
    }

}
