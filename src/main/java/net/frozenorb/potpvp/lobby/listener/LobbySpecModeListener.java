package net.frozenorb.potpvp.lobby.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.player.party.event.PartyCreateEvent;
import net.frozenorb.potpvp.player.party.event.PartyMemberJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class LobbySpecModeListener implements Listener {

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        PotPvPSI.getInstance().getLobbyHandler().setSpectatorMode(event.getMember(), false);
    }

    @EventHandler
    public void onPartyCreate(PartyCreateEvent event) {
        Player leader = Bukkit.getPlayer(event.getParty().getLeader());
        PotPvPSI.getInstance().getLobbyHandler().setSpectatorMode(leader, false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPSI.getInstance().getLobbyHandler().setSpectatorMode(event.getPlayer(), false);
    }

}