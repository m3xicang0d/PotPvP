package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinMessageListener implements Listener {

    @EventHandler
    public void RegisterListeners(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);
        player.getInventory().clear();
        if (PotPvPSI.getInstance().getConfig().getBoolean("SETTINGS.JOIN-MESSAGE.ENABLED")) {
            PotPvPSI.getInstance().getConfig().getStringList("SETTINGS.JOIN-MESSAGE.LINES").stream()
                    .map(line -> line.replace("%player%", player.getName()))
                    .forEach(m -> player.sendMessage(CC.translate(m)));
        }
        player.updateInventory();
    }
}
