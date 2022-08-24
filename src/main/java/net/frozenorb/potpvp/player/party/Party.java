package net.frozenorb.potpvp.player.party;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.pvpclasses.PvPClasses;
import net.frozenorb.potpvp.player.party.event.*;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.VisibilityUtils;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a collection of players which can perform
 * various actions (ex queue, have elo, etc) together.
 * <p>
 * All members, the leader, and all {@link PartyInvite}
 * targets (although not senders) are guaranteed to be online.
 */
public final class Party {

    /**
     * {@link UUID} of this party
     * <p>
     * New UUID = new Party
     */
    @Getter public final UUID partyId = new UUID(ThreadLocalRandom.current().nextLong(), ThreadLocalRandom.current().nextLong());

    // the maximum party size for non-op leaders
    public static final int MAX_SIZE = 30;

    /**
     * Leader of the party, given permission to perform
     * administrative commands (and perform actions like queueing)
     * on behalf of the party. Guaranteed to be online.
     */
    @Getter public UUID leader;

    @Getter public Map<UUID, PvPClasses> kits = new HashMap<>();

    /**
     * All players who are currently part of this party.
     * Each player will only be a member of one party at a time.
     * Guaranteed to all be online.
     */
    public final Set<UUID> members = Sets.newLinkedHashSet();

    /**
     * All active (non-expired) {@link PartyInvite}s. Players can have
     * active invitations from more than one party at a time. All targets
     * (but not senders) are guaranteed to be online.
     */
    public final Set<PartyInvite> invites = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Current access restriction in place for joining this party
     *
     * @see PartyAccessRestriction
     */
    @Getter @Setter public PartyAccessRestriction accessRestriction = PartyAccessRestriction.INVITE_ONLY;

    /**
     * Password requires to join this party, only active if
     * {@link #accessRestriction} is {@link PartyAccessRestriction#PASSWORD}.
     *
     * @see PartyAccessRestriction#PASSWORD
     */
    @Getter @Setter public String password = null;

    Party(UUID leader) {
        this.leader = Preconditions.checkNotNull(leader, "leader");
        this.members.add(leader);

        PotPvPSI.getInstance().getPartyHandler().updatePartyCache(leader, this);
        Bukkit.getPluginManager().callEvent(new PartyCreateEvent(this));
    }

    /**
     * Checks if the player provided is a member of this party
     *
     * @param playerUuid the player to check
     * @return true if the player provided is a member of this party,
     * false otherwise.
     */
    public boolean isMember(UUID playerUuid) {
        return members.contains(playerUuid);
    }

    /**
     * Checks if the player provided is the leader of this party
     *
     * @param playerUuid the player to check
     * @return true if the player provided is the leader of this party,
     * false otherwise.
     */
    public boolean isLeader(UUID playerUuid) {
        return leader.equals(playerUuid);
    }

    public Set<UUID> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    public Set<PartyInvite> getInvites() {
        return ImmutableSet.copyOf(invites);
    }

    public PartyInvite getInvite(UUID target) {
        for (PartyInvite invite : invites) {
            if (invite.getTarget().equals(target)) {
                return invite;
            }
        }

        return null;
    }

    public void revokeInvite(PartyInvite invite) {
        invites.remove(invite);
    }

    public void invite(Player target) {
        PartyInvite invite = new PartyInvite(this, target.getUniqueId());
        target.spigot().sendMessage(PartyLang.inviteAcceptPrompt(this));
        this.message(ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " has been invited to join your party.");
        this.invites.add(invite);
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            this.invites.remove(invite);
        }, 600L);
    }

    public void join(Player player) {
        if (members.contains(player.getUniqueId())) {
            return;
        }

        if (!PotPvPValidation.canJoinParty(player, this)) {
            return;
        }

        PartyInvite invite = getInvite(player.getUniqueId());

        if (invite != null) {
            revokeInvite(invite);
        }

        Player leaderBukkit = Bukkit.getPlayer(leader);
        player.sendMessage(ChatColor.GREEN + "You joined " + ChatColor.YELLOW + leaderBukkit.getName() + ChatColor.GREEN + "'s party.");

        message(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " joined the party.");

        members.add(player.getUniqueId());
        PotPvPSI.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), this);

        Bukkit.getPluginManager().callEvent(new PartyMemberJoinEvent(player, this));

        //forEachOnline(VisibilityUtils::updateVisibility);
        resetInventoriesDelayed();
    }

    public void leave(Player player) {
        if (members.size() <= 1) {
            disband();
            return;
        }

        // If kicked players was not a member, stop flow.
        if (!members.remove(player.getUniqueId())) return;

        PotPvPSI.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), null);

        // randomly elect new leader if needed
        if (leader.equals(player.getUniqueId())) assignLeaderRandomly();

        player.sendMessage(ChatColor.RED + "You have left your party.");
        message(ChatColor.YELLOW + player.getName() + ChatColor.RED + " has left your party.");

        VisibilityUtils.updateVisibility(player);
        //forEachOnline(VisibilityUtils::updateVisibility);

        Bukkit.getPluginManager().callEvent(new PartyMemberLeaveEvent(player, this));

        InventoryUtils.resetInventoryDelayed(player);
        resetInventoriesDelayed();
    }

    /**
     * Randomly assigns a new party leader among current party members.
     */
    public void assignLeaderRandomly() {
        Player newLeader = getRandomMember();
        this.leader = newLeader.getUniqueId();
        message(ChatColor.YELLOW + newLeader.getName() + ChatColor.GREEN + " has been randomly assigned as a leader of your party.");
    }

    public void setLeader(Player player) {
        this.leader = player.getUniqueId();

        message(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " is now the leader of your party.");
        resetInventoriesDelayed();
    }

    public void disband() {
        Bukkit.getPluginManager().callEvent(new PartyDisbandEvent(this));
        PotPvPSI.getInstance().getPartyHandler().unregisterParty(this);

        forEachOnline(player -> {
            VisibilityUtils.updateVisibility(player);
            PotPvPSI.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), null);
        });

        message(ChatColor.RED + "The party was disbanded.");
        resetInventoriesDelayed();
    }

    public void kick(Player player) {
        // In case kicked player was the last one, disband the party.
        if (members.size() <= 1) {
            disband();
            return;
        }

        // If kicked players was not a member, stop flow.
        if (!members.remove(player.getUniqueId())) return;

        // randomly elect new leader if needed
        if (leader.equals(player.getUniqueId())) assignLeaderRandomly();

        PotPvPSI.getInstance().getPartyHandler().updatePartyCache(player.getUniqueId(), null);

        player.sendMessage(ChatColor.RED + "You were kicked from the party.");
        message(ChatColor.YELLOW + player.getName() + ChatColor.RED + " was kicked from the party.");

        /*VisibilityUtils.updateVisibility(player);
        forEachOnline(VisibilityUtils::updateVisibility);*/

        Bukkit.getPluginManager().callEvent(new PartyMemberKickEvent(player, this));

        InventoryUtils.resetInventoryDelayed(player);
        resetInventoriesDelayed();
    }

    /**
     * Sends a basic chat message to all members
     *
     * @param message the message to send
     */
    public void message(String message) {
        forEachOnline(p -> p.sendMessage(message));
    }

    /**
     * Plays a sound for all members
     *
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSound(Sound sound, float pitch) {
        forEachOnline(p -> p.playSound(p.getLocation(), sound, 10F, pitch));
    }

    /**
     * Resets all members' inventories
     *
     * @see InventoryUtils#resetInventoryDelayed(Player)
     */
    public void resetInventoriesDelayed() {
        // we use one runnable and then call resetInventoriesNow instead of
        // directly using to InventoryUtils#resetInventoryDelayed to reduce
        // the number of tasks we submit to the scheduler
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), this::resetInventoriesNow, InventoryUtils.RESET_DELAY_TICKS);
    }

    /**
     * Resets all members' inventories
     *
     * @see InventoryUtils#resetInventoryNow(Player)
     */
    public void resetInventoriesNow() {
        forEachOnline(InventoryUtils::resetInventoryNow);
    }

    public void forEachOnline(Consumer<Player> consumer) {
        for (UUID member : members) {
            Player memberBukkit = Bukkit.getPlayer(member);

            if (memberBukkit != null) {
                consumer.accept(memberBukkit);
            }
        }
    }

    /**
     * Obtains a list of party online players.
     * In case that for some weird reason a offline member stills here, will be ignored.
     *
     * @return List of online party players.
     */
    public List<Player> getPlayerMembers() {
        return members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Obtains a random party member.
     * In case that for some weird reason a offline member stills here, will be ignored.
     *
     * @return Random Player from party online members.
     */
    public Player getRandomMember() {
        int randomNumber = RandomUtils.nextInt(getPlayerMembers().size());
        return getPlayerMembers().get(randomNumber);
    }

}