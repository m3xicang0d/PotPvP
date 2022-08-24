package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.commands.highstaff.manage.ManageCommand;
import net.frozenorb.potpvp.commands.player.LeaderBoardCommand;
import net.frozenorb.potpvp.game.follow.command.UnfollowCommand;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchState;
import net.frozenorb.potpvp.game.morpheus.command.HostCommand;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.lobby.LobbyItems;
import net.frozenorb.potpvp.lobby.menu.SpectateMenu;
import net.frozenorb.potpvp.player.party.command.PartyCreateCommand;
import net.frozenorb.potpvp.util.bukkit.ItemListener;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class LobbyItemListener extends ItemListener {
    public final Map<UUID, Long> canUseRandomSpecItem = new HashMap();

    public LobbyItemListener(LobbyHandler lobbyHandler) {
        this.addHandler(LobbyItems.MANAGE_ITEM, (p) -> {
            if (p.hasPermission("potpvp.admin")) {
                ManageCommand.manage(p);
            }

        });
        this.addHandler(LobbyItems.DISABLE_SPEC_MODE_ITEM, (player) -> {
            if (lobbyHandler.isInLobby(player)) {
                lobbyHandler.setSpectatorMode(player, false);
            }

        });
        this.addHandler(LobbyItems.ENABLE_SPEC_MODE_ITEM, (player) -> {
            if (lobbyHandler.isInLobby(player) && PotPvPValidation.canUseSpectateItem(player)) {
                lobbyHandler.setSpectatorMode(player, true);
            }

        });
        this.addHandler(LobbyItems.SPECTATE_MENU_ITEM, (player) -> {
            if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                (new SpectateMenu()).openMenu(player);
            }

        });
        this.addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, (player) -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
            if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                if ((Long)this.canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                    player.sendMessage(ChatColor.RED + "Please wait before doing this again!");
                } else {
                    List<Match> matches = new ArrayList(matchHandler.getHostedMatches());
                    matches.removeIf((m) -> {
                        return m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING;
                    });
                    if (matches.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "There are no matches available to spectate.");
                    } else {
                        Match currentlySpectating = matchHandler.getMatchSpectating(player);
                        Match newSpectating = (Match)matches.get(ThreadLocalRandom.current().nextInt(matches.size()));
                        if (currentlySpectating != null) {
                            currentlySpectating.removeSpectator(player, false);
                        }

                        newSpectating.addSpectator(player, (Player)null);
                        this.canUseRandomSpecItem.put(player.getUniqueId(), System.currentTimeMillis() + 3000L);
                    }

                }
            }
        });
        this.addHandler(LobbyItems.UNFOLLOW_ITEM, UnfollowCommand::unfollow);
        this.addHandler(LobbyItems.PARTY_CREATE_ITEM, PartyCreateCommand::partyCreate);
        this.addHandler(LobbyItems.LEADERBOARDS_ITEM, LeaderBoardCommand::statistics);
        this.addHandler(LobbyItems.EVENTS_ITEM, HostCommand::host);
    }

    @EventHandler
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }
}
