package net.frozenorb.potpvp.game.arena.menu.select;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.kt.util.Callback;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class SelectArenaMenu extends Menu {

    private final KitType kitType;
    private final Callback<ArenaSchematic> mapCallback;
    private final String tt;
    Set<String> enabledSchematics = Sets.newHashSet();

    public SelectArenaMenu(KitType kitType, Callback<ArenaSchematic> mapCallback, String title) {
        this. tt = (ChatColor.BLUE.toString() + ChatColor.BOLD + title);

        this.kitType = kitType;
        this.mapCallback = mapCallback;

        for (ArenaSchematic schematic : PotPvPSI.getInstance().getArenaHandler().getSchematics()) {
            if (MatchHandler.canUseSchematic(this.kitType, schematic) &&
                    schematic.isEnabled() &&
                    !schematic.isGameMapOnly()) {
                enabledSchematics.add(schematic.getName());
            }
        }
    }

    @Override
    public String getTitle(Player player) {
        return tt;
    }

    @Override
    public Map<Integer, Button> getButtons(Player arg0) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = 0;
        for (String mapName : enabledSchematics) {
            buttons.put(i++, new ArenaButton(mapName, mapCallback));
        }

        return buttons;
    }

}
