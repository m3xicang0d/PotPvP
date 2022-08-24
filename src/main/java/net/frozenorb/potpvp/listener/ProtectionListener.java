package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class ProtectionListener implements Listener {

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0];
        List<String> blockedMessage = PotPvPSI.getInstance().getConfig().getStringList(("PROTECTION.BLOCKED-COMMANDS"));
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (blockedMessage.contains(command.toLowerCase())) {
            player.sendMessage(CC.translate(PotPvPSI.getInstance().getConfig().getString("PROTECTION.MESSAGE")));
            event.setCancelled(true);
        }
    }
}
