package net.frozenorb.potpvp.game.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.*;
import net.frozenorb.potpvp.game.match.command.LeaveCommand;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.FancyPlayerInventory;
import net.frozenorb.potpvp.util.bukkit.ItemListener;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SpectatorItemListener extends ItemListener {

    private ConfigFile messages = PotPvPSI.getInstance().getMessagesConfig();
    public Map<UUID, Long> toggleVisiblityUsable = new ConcurrentHashMap();

    public SpectatorItemListener(MatchHandler matchHandler) {
        this.setPreProcessPredicate(matchHandler::isSpectatingMatch);
        Consumer<Player> toggleSpectatorsConsumer = (player) -> {
            SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
            UUID playerUuid = player.getUniqueId();
            boolean togglePermitted = (Long)this.toggleVisiblityUsable.getOrDefault(playerUuid, 0L) < System.currentTimeMillis();
            if (!togglePermitted) {
                player.sendMessage(CC.translate(messages.getString("PARTY.SPECTATORS.COOLDOWN")));
            } else {
                boolean enabled = !settingHandler.getSetting(player, Setting.VIEW_OTHER_SPECTATORS);
                settingHandler.updateSetting(player, Setting.VIEW_OTHER_SPECTATORS, enabled);
                if (enabled) {
                    player.sendMessage(CC.translate(messages.getString("PARTY.SPECTATORS.SHOW")));
                } else {
                    player.sendMessage(CC.translate(messages.getString("PARTY.SPECTATORS.HIDE")));
                }

                MatchUtils.resetInventory(player);
                this.toggleVisiblityUsable.put(playerUuid, System.currentTimeMillis() + 3000L);
            }
        };
        this.addHandler(SpectatorItems.RETURN_TO_LOBBY_ITEM, LeaveCommand::leave);
        this.addHandler(SpectatorItems.LEAVE_PARTY_ITEM, LeaveCommand::leave);
        this.addHandler(SpectatorItems.SHOW_SPECTATORS_ITEM, toggleSpectatorsConsumer);
        this.addHandler(SpectatorItems.HIDE_SPECTATORS_ITEM, toggleSpectatorsConsumer);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
            Match clickerMatch = matchHandler.getMatchSpectating(event.getPlayer());
            Player clicker = event.getPlayer();
            if (clickerMatch != null && clicker.getItemInHand().isSimilar(SpectatorItems.VIEW_INVENTORY_ITEM)) {
                Player clicked = (Player)event.getRightClicked();
                MatchTeam clickedTeam = clickerMatch.getTeam(clicked.getUniqueId());
                if (clickedTeam == null) {
                    clicker.sendMessage(ChatColor.RED + "Cannot view inventory of " + clicked.getName());
                } else {
                    boolean bypassPerm = clicker.hasPermission("potpvp.inventory.all");
                    boolean sameTeam = clickedTeam.getAllMembers().contains(clicker.getUniqueId());
                    if (!bypassPerm && !sameTeam) {
                        clicker.sendMessage(CC.translate(messages.getString("PARTY.VIEW-INVENTORY.OTHER-TEAM").replaceAll("%player%", clicked.getName())));
                    } else {
                        clicker.sendMessage(CC.translate(messages.getString("PARTY.VIEW-INVENTORY.YOUR-TEAM").replaceAll("%player%", clicked.getName())));
                        FancyPlayerInventory.open(clicked, clicker);
                    }

                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.toggleVisiblityUsable.remove(event.getPlayer().getUniqueId());
    }
}