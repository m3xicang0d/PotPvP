package net.frozenorb.potpvp.player.setting.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.setting.menu.SettingsMenu;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.entity.Player;

/**
 * /settings, accessible by all users, opens a {@link SettingsMenu}
 */
public final class SettingsCommand {

    @Command(names = {"prefs", "options", "settings", "setting"})
    public static void prefs(Player sender) {
        if (PotPvPValidation.isInGame(sender)) return;
        new SettingsMenu().openMenu(sender);
    }

}