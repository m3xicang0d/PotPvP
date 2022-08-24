package net.frozenorb.potpvp.game.morpheus.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.morpheus.menu.HostMenu;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.entity.Player;

public class HostCommand {

    private final ConfigFile config = PotPvPSI.getInstance().getMessagesConfig();

    @Command(names = {"host"}, permission = "potpvp.command.host")
    public static void host(Player sender) {

        if (sender.hasPermission("potpvp.command.host")) {
            new HostMenu().openMenu(sender);

        } else {
            PotPvPSI.getInstance().getMessagesConfig().getStringList("EVENTS.NO-PERMISSIONS")
                    .stream().map(CC::translate).forEach(sender::sendMessage);
        }
    }

}
