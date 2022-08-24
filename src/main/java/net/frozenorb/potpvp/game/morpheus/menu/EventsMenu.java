package net.frozenorb.potpvp.game.morpheus.menu;

import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.kt.morpheus.game.GameState;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class EventsMenu extends Menu {

    public EventsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return (ChatColor.DARK_PURPLE + "Join an event");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        for (Game game : GameQueue.INSTANCE.getCurrentGames()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.AQUA + game.getEvent().getName() + " Event";
                }

                @Override
                public List<String> getDescription(Player player) {

                    List<String> lines = new ArrayList<>();

                    for (String line : Arrays.asList(
                        "&7&m-------------------------",
                        "&bPlayers&7: &f" + game.getPlayers().size() + (game.getMaxPlayers() == -1 ? "" : "&7/" + game.getMaxPlayers()),
                        "&bState&7: &f" + StringUtils.capitalize(game.getState().name().toLowerCase()),
                        "&bHosted By&7: &f" + game.getHost().getDisplayName(),
                        " ",
                        (game.getState() == GameState.STARTING ? "&aClick here to join." : "&7Click here to spectate."),
                        "&7&m-------------------------")) {
                        lines.add(ChatColor.translateAlternateColorCodes('&', line));
                    }

                    return lines;
                }

                @Override
                public Material getMaterial(Player player) {
                    return game.getEvent().getIcon().getType();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) game.getEvent().getIcon().getDurability();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    /*
                    if (game.getState() == GameState.STARTING) {
                        if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                            player.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                            return;
                        }
                        game.add(player);
                    } else {
                        game.addSpectator(player);
                    }
                    */
                }

            });
        }

        if (toReturn.isEmpty()) {
            player.closeInventory();
        }

        return toReturn;
    }

}
