package net.frozenorb.potpvp.game.queue.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.game.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.game.queue.QueueHandler;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.player.party.event.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class QueueGeneralListener implements Listener {

    public final QueueHandler queueHandler;

    public QueueGeneralListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;
    }

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        queueHandler.leaveQueue(event.getParty(), true);
    }

    @EventHandler
    public void onPartyCreate(PartyCreateEvent event) {
        UUID leaderUuid = event.getParty().getLeader();
        Player leaderPlayer = Bukkit.getPlayer(leaderUuid);

        queueHandler.leaveQueue(leaderPlayer, false);
    }

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        queueHandler.leaveQueue(event.getMember(), false);
        leaveQueue(event.getParty(), event.getMember(), "joined");
    }

    @EventHandler
    public void onPartyMemberKick(PartyMemberKickEvent event) {
        leaveQueue(event.getParty(), event.getMember(), "was kicked");
    }

    @EventHandler
    public void onPartyMemberLeave(PartyMemberLeaveEvent event) {
        leaveQueue(event.getParty(), event.getMember(), "left");
    }

    public void leaveQueue(Party party, Player member, String action) {
        if (queueHandler.leaveQueue(party, true)) {
            party.message(ChatColor.YELLOW + "Your party has been removed from the queue because " + ChatColor.AQUA + member.getName() + ChatColor.YELLOW + " " + action + ".");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        queueHandler.leaveQueue(event.getPlayer(), true);
    }

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        queueHandler.leaveQueue(event.getSpectator(), true);
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberBukkit = Bukkit.getPlayer(member);
                Party memberParty = partyHandler.getParty(memberBukkit);

                queueHandler.leaveQueue(memberBukkit, true);

                if (memberParty != null && memberParty.isLeader(member)) {
                    queueHandler.leaveQueue(memberParty, true);
                }
            }
        }
    }

}