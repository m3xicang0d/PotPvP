package net.frozenorb.potpvp.game.match;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Iterator;

public final class MatchUtils {
    public static void resetInventory(Player player) {
        ConfigFile hotbar = PotPvPSI.instance.getHotbarConfig();
        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchSpectating(player);
        if (match != null) {
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents((ItemStack[])null);
            if (match.getState() != MatchState.ENDING) {
                boolean canViewInventories = player.hasPermission("potpvp.inventory.all");
                if (!canViewInventories) {
                    Iterator var8 = match.getTeams().iterator();

                    while(var8.hasNext()) {
                        MatchTeam team = (MatchTeam)var8.next();
                        if (team.getAllMembers().contains(player.getUniqueId())) {
                            canViewInventories = true;
                            break;
                        }
                    }
                }

                if (canViewInventories) {
                    inventory.setItem(hotbar.getInt("SPECTATOR-VIEW-INVENTORY.SLOT"), SpectatorItems.VIEW_INVENTORY_ITEM);
                }

                if (settingHandler.getSetting(player, Setting.VIEW_OTHER_SPECTATORS)) {
                    inventory.setItem(hotbar.getInt("SPECTATOR-HIDE-SPECTATORS.SLOT"), SpectatorItems.HIDE_SPECTATORS_ITEM);
                } else {
                    inventory.setItem(hotbar.getInt("SPECTATOR-SHOW-SPECTATORS.SLOT"), SpectatorItems.SHOW_SPECTATORS_ITEM);
                }

                if (partyHandler.hasParty(player)) {
                    inventory.setItem(hotbar.getInt("SPECTATOR-LEAVE-PARTY.SLOT"), SpectatorItems.LEAVE_PARTY_ITEM);
                } else {
                    inventory.setItem(hotbar.getInt("RETURN-LOBBY.SLOT"), SpectatorItems.RETURN_TO_LOBBY_ITEM);
                    if (!followHandler.getFollowing(player).isPresent()) {
                        inventory.setItem(hotbar.getInt("SPECTATOR-RANDOM-MATCH.SLOT"), SpectatorItems.SPECTATE_RANDOM_PARTY_ITEM);
                        inventory.setItem(hotbar.getInt("SPECTATOR-MENU-PLAYERS.SLOT"), SpectatorItems.SPECTATE_MENU_PARTY_ITEM);
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
        }
    }

    public MatchUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
