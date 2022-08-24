package net.frozenorb.potpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.util.StringUtil;

import java.util.Collection;

public final class TabCompleteListener implements Listener {

    @EventHandler
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        String token = event.getLastToken();
        Collection<String> completions = event.getTabCompletions();

        completions.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StringUtil.startsWithIgnoreCase(player.getName(), token)) {
                completions.add(player.getName());
            }
        }
    }

}