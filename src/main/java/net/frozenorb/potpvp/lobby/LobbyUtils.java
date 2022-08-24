package net.frozenorb.potpvp.lobby;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.follow.FollowHandler;
import net.frozenorb.potpvp.game.kit.KitItems;
import net.frozenorb.potpvp.game.kit.menu.editkit.EditKitMenu;
import net.frozenorb.potpvp.game.morpheus.EventItems;
import net.frozenorb.potpvp.game.queue.QueueHandler;
import net.frozenorb.potpvp.game.queue.QueueItems;
import net.frozenorb.potpvp.player.duel.DuelHandler;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.player.party.PartyItems;
import net.frozenorb.potpvp.player.rematch.RematchData;
import net.frozenorb.potpvp.player.rematch.RematchHandler;
import net.frozenorb.potpvp.player.rematch.RematchItems;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class LobbyUtils {

    public static void resetInventory(Player player) {
        if (!(Menu.getCurrentlyOpenedMenus().get(player.getName()) instanceof EditKitMenu) && player.getGameMode() != GameMode.CREATIVE) {
            PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents(null);
            if (partyHandler.hasParty(player)) {
                renderPartyItems(player, inventory, partyHandler.getParty(player));
            } else {
                renderSoloItems(player, inventory);
            }

            Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), player::updateInventory, 1L);
        }
    }

    public static void renderPartyItems(Player player, PlayerInventory inventory, Party party) {
        ConfigFile hotbar = PotPvPSI.getInstance().getHotbarConfig();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        int partySize;
        if (party.isLeader(player.getUniqueId())) {
            partySize = party.getMembers().size();
            if (partySize == 2) {
                if (!queueHandler.isQueuedUnranked(party)) {
                    inventory.setItem(hotbar.getInt("ASSING-CLASSES-LEADER.SLOT"), PartyItems.ASSIGN_CLASSES_LEADER);
                }

                if (!queueHandler.isQueuedRanked(party)) {
                    inventory.setItem(hotbar.getInt("PARTY-TEAM-SPLIT.SLOT"), PartyItems.START_TEAM_SPLIT_ITEM);
                    inventory.setItem(hotbar.getInt("PARTY-TEAM-FFA.SLOT"), PartyItems.START_FFA_ITEM);
                    inventory.setItem(hotbar.getInt("ASSING-CLASSES-LEADER.SLOT"), PartyItems.ASSIGN_CLASSES_LEADER);
                }

            } else if (partySize > 2 && !queueHandler.isQueued(party)) {
                inventory.setItem(hotbar.getInt("PARTY-TEAM-SPLIT.SLOT"), PartyItems.START_TEAM_SPLIT_ITEM);
                inventory.setItem(hotbar.getInt("PARTY-TEAM-FFA.SLOT"), PartyItems.START_FFA_ITEM);
                inventory.setItem(hotbar.getInt("ASSING-CLASSES-LEADER.SLOT"), PartyItems.ASSIGN_CLASSES_LEADER);
            }

        } else {
            partySize = party.getMembers().size();
            if (partySize >= 2) {
                inventory.setItem(hotbar.getInt("ASSING-CLASSES-PLAYER.SLOT"), PartyItems.ASSIGN_CLASSES_PLAYER);
            }
        }

        inventory.setItem(0, PartyItems.icon(party));
        inventory.setItem(hotbar.getInt("OTHER-PARTIES.slot"), PartyItems.OTHER_PARTIES_ITEM);
        inventory.setItem(hotbar.getInt("PARTY-KIT-EDITOR.SLOT"), PartyItems.OPEN_EDITOR_PARTY_ITEM);
        inventory.setItem(hotbar.getInt("LEAVE-PARTY.SLOT"), PartyItems.LEAVE_PARTY_ITEM);
    }

    public static void renderSoloItems(Player player, PlayerInventory inventory) {
        ConfigFile hotbar = PotPvPSI.getInstance().getHotbarConfig();
        RematchHandler rematchHandler = PotPvPSI.getInstance().getRematchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        FollowHandler followHandler = PotPvPSI.getInstance().getFollowHandler();
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
        boolean specMode = lobbyHandler.isInSpectatorMode(player);
        boolean followingSomeone = followHandler.getFollowing(player).isPresent();
        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || specMode);
        if (!specMode && !followingSomeone) {
            RematchData rematchData = rematchHandler.getRematchData(player);
            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                if (target != null) {
                    if (duelHandler.findInvite(player, target) != null) {
                        inventory.setItem(hotbar.getInt("SENT-REMATCH.SLOT"), RematchItems.SENT_REMATCH_ITEM);
                    } else if (duelHandler.findInvite(target, player) != null) {
                        inventory.setItem(hotbar.getInt("ACCEPT-REMATCH.SLOT"), RematchItems.ACCEPT_REMATCH_ITEM);
                    } else {
                        inventory.setItem(hotbar.getInt("REQUEST-REMATCH.SLOT"), RematchItems.REQUEST_REMATCH_ITEM);
                    }
                }
            }

            if (queueHandler.isQueuedRanked(player.getUniqueId())) {
                inventory.setItem(hotbar.getInt("LEAVE-UNRANKED.SLOT"), QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM);

            } else if (queueHandler.isQueuedUnranked(player.getUniqueId())) {
                inventory.setItem(hotbar.getInt("LEAVE-RANKED.SLOT"), QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM);

            } else {
                inventory.setItem(hotbar.getInt("JOIN-UNRANKED.SLOT"), QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM);
                inventory.setItem(hotbar.getInt("JOIN-RANKED.SLOT"), QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM);
                inventory.setItem(hotbar.getInt("SPECTATOR-MODE-ON.SLOT"), LobbyItems.ENABLE_SPEC_MODE_ITEM);
                inventory.setItem(hotbar.getInt("PARTY-CREATE.SLOT"), LobbyItems.PARTY_CREATE_ITEM);
                inventory.setItem(hotbar.getInt("LEADERBOARDS.SLOT"), LobbyItems.LEADERBOARDS_ITEM);
                inventory.setItem(hotbar.getInt("HOST-EVENTS.SLOT"), LobbyItems.EVENTS_ITEM);
                inventory.setItem(hotbar.getInt("KIT-EDITOR.SLOT"), KitItems.OPEN_EDITOR_ITEM);

                ItemStack eventItem = EventItems.getEventItem();
                if (player.hasPermission("potpvp.admin")) {
                    if (eventItem != null) {
                        inventory.setItem(6, eventItem);
                    }
                    if(PotPvPSI.getInstance().getHotbarConfig().getBoolean("PRACTICE-SETTINGS.ENABLED")) {
                        inventory.setItem(hotbar.getInt("PRACTICE-SETTINGS.SLOT"), LobbyItems.MANAGE_ITEM);
                    }
                } else if (eventItem != null) {
                    inventory.setItem(7, eventItem);
                }
            }
        } else {
            inventory.setItem(hotbar.getInt("SPECTATOR-MENU.SLOT"), LobbyItems.SPECTATE_MENU_ITEM);
            inventory.setItem(hotbar.getInt("SPECTATOR-RANDOM.SLOT"), LobbyItems.SPECTATE_RANDOM_ITEM);
            inventory.setItem(hotbar.getInt("SPECTATOR-MODE-OFF.SLOT"), LobbyItems.DISABLE_SPEC_MODE_ITEM);
            if (followingSomeone) {
                inventory.setItem(hotbar.getInt("UNFOLLOW-MODE.SLOT"), LobbyItems.UNFOLLOW_ITEM);
            }
        }
    }
}
