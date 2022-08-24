package net.frozenorb.potpvp.player.duel;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.player.duel.listener.DuelListener;
import net.frozenorb.potpvp.player.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DuelHandler {
    public static final int DUEL_INVITE_TIMEOUT_SECONDS = 30;
    private Set<DuelInvite> activeInvites = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DuelHandler() {
        Bukkit.getPluginManager().registerEvents((Listener)new DuelListener(), (Plugin)PotPvPSI.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)PotPvPSI.getInstance(), () -> this.activeInvites.removeIf(DuelInvite::isExpired), 20L, 20L);
    }

    public void insertInvite(DuelInvite invite) {
        this.activeInvites.add(invite);
    }

    public void removeInvite(DuelInvite invite) {
        this.activeInvites.remove(invite);
    }

    public void removeInvitesTo(Player player) {
        this.activeInvites.removeIf(i -> i instanceof PlayerDuelInvite && ((UUID)((PlayerDuelInvite)i).getTarget()).equals(player.getUniqueId()));
    }

    public void removeInvitesFrom(Player player) {
        this.activeInvites.removeIf(i -> i instanceof PlayerDuelInvite && ((UUID)((PlayerDuelInvite)i).getSender()).equals(player.getUniqueId()));
    }

    public void removeInvitesTo(Party party) {
        this.activeInvites.removeIf(i -> i instanceof PartyDuelInvite && ((PartyDuelInvite)i).getTarget() == party);
    }

    public void removeInvitesFrom(Party party) {
        this.activeInvites.removeIf(i -> i instanceof PartyDuelInvite && ((PartyDuelInvite)i).getSender() == party);
    }

    public PartyDuelInvite findInvite(Party sender, Party target) {
        for (DuelInvite invite : this.activeInvites) {
            PartyDuelInvite partyInvite;
            if (!(invite instanceof PartyDuelInvite) || (partyInvite = (PartyDuelInvite)invite).getSender() != sender || partyInvite.getTarget() != target) continue;
            return partyInvite;
        }
        return null;
    }

    public PlayerDuelInvite findInvite(Player sender, Player target) {
        for (DuelInvite invite : this.activeInvites) {
            PlayerDuelInvite playerInvite;
            if (!(invite instanceof PlayerDuelInvite) || !((UUID)(playerInvite = (PlayerDuelInvite)invite).getSender()).equals(sender.getUniqueId()) || !((UUID)playerInvite.getTarget()).equals(target.getUniqueId())) continue;
            return playerInvite;
        }
        return null;
    }
}

