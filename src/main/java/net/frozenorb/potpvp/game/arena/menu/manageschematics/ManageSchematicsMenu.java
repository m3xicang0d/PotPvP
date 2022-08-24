package net.frozenorb.potpvp.game.arena.menu.manageschematics;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.commands.highstaff.manage.ManageCommand;
import net.frozenorb.potpvp.game.arena.ArenaHandler;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.util.menu.MenuBackButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ManageSchematicsMenu extends Menu {

    public ManageSchematicsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return ("Manage schematics");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        buttons.put(index++, new MenuBackButton(p -> new ManageCommand.ManageMenu().openMenu(p)));

        for (ArenaSchematic schematic : arenaHandler.getSchematics()) {
            buttons.put(index++, new ManageSchematicButton(schematic));
        }

        return buttons;
    }

}