package net.frozenorb.potpvp.util.tablist.shared;

import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.tablist.shared.entry.TabElement;
import net.frozenorb.potpvp.util.tablist.shared.entry.TabElementHandler;
import net.frozenorb.potpvp.util.tablist.shared.skin.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class TabHandler {

    private final TabAdapter adapter;
    private final TabElementHandler handler;
    private final long ticks;

    public static Map<String, SkinType> tabSkins = new HashMap<>();

    /**
     * Constructor to make a new tab handler
     *
     * @param adapter the adapter to send the tab with
     * @param handler the handler to get the elements from
     * @param plugin  the plugin to register the thread to
     * @param ticks   the amount it should update
     */
    public TabHandler(TabAdapter adapter, TabElementHandler handler, PotPvPSI plugin, long ticks) {
        ConfigFile config = plugin.getTablistHeadsConfig();
        ConfigurationSection section = config.getConfiguration().getConfigurationSection("HEADS");
        int i = 0;
        if(section != null) {
            for (String s : section.getKeys(false)) {
                String path = "HEADS." + s;
                String value = config.getString(path + ".VALUE");
                String signature = config.getString(path + ".SIGNATURE");
                tabSkins.put(s.toUpperCase(), new SkinType(new String[]{value, signature}));
                i++;
            }
        }
        System.out.println("Registered " + i + " custom tab heads");
        this.adapter = adapter;
        this.handler = handler;
        this.ticks = ticks;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> Bukkit.getOnlinePlayers().forEach(this::sendUpdate), 1L, ticks / 20, TimeUnit.SECONDS);
        // register listener for hiding players from tab
        //Bukkit.getPluginManager().registerEvents(new PlayerListener(this), plugin);
    }

    /**
     * Update the tablist for a player
     *
     * @param player the player to update it for
     */
    public void sendUpdate(Player player) {
        TabElement tabElement = this.handler.getElement(player);
        this.adapter
                .setupProfiles(player)
                .showRealPlayers(player).addFakePlayers(player)
                .hideRealPlayers(player).handleElement(player, tabElement)
                .sendHeaderFooter(player, tabElement.getHeader(), tabElement.getFooter());
    }

}