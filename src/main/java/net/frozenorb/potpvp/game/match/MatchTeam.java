package net.frozenorb.potpvp.game.match;

import com.google.common.collect.ImmutableSet;
import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.integration.lunar.RallyPoint;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Represents one team participating in a {@link Match}
 */
public final class MatchTeam {

    /**
     * All players who were ever part of this team, including those who logged off / died
     */
    @Getter public final Set<UUID> allMembers;
    @Getter public Map<UUID, RallyPoint> rallys = new HashMap<>();

    /**
     * All players who are currently alive.
     */
    public final Set<UUID> aliveMembers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // convenience constructor for 1v1s, queues, etc
    public MatchTeam(UUID initialMember) {
        this(ImmutableSet.of(initialMember));
    }


    public MatchTeam(Collection<UUID> initialMembers) {
        this.allMembers = ImmutableSet.copyOf(initialMembers);
        this.aliveMembers.addAll(initialMembers);
    }

    public void rally(RallyPoint point) {
        Player sender = point.getPlayer();
        UUID uuid = sender.getUniqueId();
        if(rallys.containsKey(uuid)) {
            updatePoint(point);
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("RALLY.POINT")));
        } else {
            addPoint(point);
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("RALLY.POINT")));
        }

    }

    public void addPoint(RallyPoint point) {
        rallys.put(point.getPlayer().getUniqueId(), point);
        aliveMembers.forEach(uuid -> {
            LunarClientAPI.getInstance().sendWaypoint(Bukkit.getPlayer(uuid), point.getLcWaypoint());
        });
    }

    public void updatePoint(RallyPoint point) {
        RallyPoint input = rallys.get(point.getPlayer().getUniqueId());
        if(input.getLocation() == point.getLocation()) return;
        aliveMembers.forEach(t -> {
            removePoint(t, input);
        });
        rallys.remove(point.getPlayer().getUniqueId());
        addPoint(point);
    }

    public void removePoint(RallyPoint point) {
        allMembers.forEach(uuid -> {
            removePoint(uuid, point);
        });
        rallys.remove(point.getPlayer().getUniqueId());
    }

    public void removePoint(UUID uuid, RallyPoint point) {
        LunarClientAPI.getInstance().removeWaypoint(Bukkit.getPlayer(uuid), point.getLcWaypoint());
    }

    public void removePoints(UUID uuid) {
        if(rallys.isEmpty()) return;
        if(!rallys.containsKey(uuid)) return;
        rallys.keySet().forEach(t -> {
            removePoint(uuid, rallys.get(t));
        });
        getAliveMembers().forEach(t -> {
            removePoint(t, rallys.get(uuid));
        });
        rallys.remove(uuid);
    }

    public void removePoints() {
        if(rallys.isEmpty()) return;
        rallys.keySet().forEach(uuid -> {
            allMembers.forEach(p -> {
                LunarClientAPI.getInstance().removeWaypoint(Bukkit.getPlayer(p), rallys.get(uuid).getLcWaypoint());
            });
        });
        rallys.clear();
    }

    /**
     * Marks the given player as dead (will no longer appear in {@link MatchTeam#getAliveMembers()}, etc)
     *
     * @param playerUuid the player to mark as dead
     */
    public void markDead(UUID playerUuid) {
        aliveMembers.remove(playerUuid);
        removePoints(playerUuid);
    }

    /**
     * Checks if the given player is still alive (shorthand for .getAliveMembers().contains())
     *
     * @param playerUuid the player to check
     * @return if the given player is still alive
     */
    public boolean isAlive(UUID playerUuid) {
        return aliveMembers.contains(playerUuid);
    }

    /**
     * Gets a immutable set of all alive team members
     *
     * @return immutable set of all alive team members
     * @see MatchTeam#aliveMembers
     */
    public Set<UUID> getAliveMembers() {
        return ImmutableSet.copyOf(aliveMembers);
    }

    public UUID getFirstAliveMember() {
        if (!aliveMembers.iterator().hasNext()) {
            return null;
        }
        return aliveMembers.iterator().next();
    }

    public UUID getFirstMember() {
        return allMembers.iterator().next();
    }

    /**
     * Sends a basic chat message to all alive members
     *
     * @param message the message to send
     * @see MatchTeam#aliveMembers
     */
    public void messageAlive(String message) {
        forEachAlive(p -> p.sendMessage(message));
    }

    /**
     * Plays a sound for all alive members
     *
     * @param sound the Sound to play
     * @param pitch the pitch to play the provided sound at
     */
    public void playSoundAlive(Sound sound, float pitch) {
        forEachAlive(p -> p.playSound(p.getLocation(), sound, 10F, pitch));
    }

    public void forEachAlive(Consumer<Player> consumer) {
        for (UUID member : aliveMembers) {
            Player memberBukkit = Bukkit.getPlayer(member);

            if (memberBukkit != null) {
                consumer.accept(memberBukkit);
            }
        }
    }

}