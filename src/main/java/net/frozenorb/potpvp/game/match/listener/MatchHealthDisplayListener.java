package net.frozenorb.potpvp.game.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.match.event.MatchSpectatorJoinEvent;
import net.frozenorb.potpvp.game.match.event.MatchSpectatorLeaveEvent;
import net.frozenorb.potpvp.game.match.event.MatchStartEvent;
import net.frozenorb.potpvp.game.match.event.MatchTerminateEvent;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles registering and un-registering the Objective that shows the health
 * below player's name-tags. This is also responsible for listening to health
 * changes and sending the update score packets manually, for consistency.
 */
public final class MatchHealthDisplayListener implements Listener {

    public static final String OBJECTIVE_NAME = "HealthDisplay";

    @EventHandler
    public void onMatchCountdownStart(MatchStartEvent event) {
        Match match = event.getMatch();

        if (!match.getKitType().isHealthShown()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            // initialize the objective for all of the recipients (players + spectators)
            for (Player player : getRecipients(match)) {
                initialize(player);
            }

            // send the health of the players in the match to all of the recipients
            for (Player player : getPlayers(match)) {
                sendToAll(player, match);
            }
        }, 1L);
    }

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();

        if (!match.getKitType().isHealthShown()) {
            return;
        }

        // clear the objective for all players and spectators
        for (Player player : getRecipients(match)) {
            clearAll(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        // remove the dead player's scores
        if (match.getKitType().isHealthShown()) {
            for (Player viewer : getRecipients(match)) {
                clear(viewer, player);
            }
        }
    }

    @EventHandler
    public void onSpectatorJoin(MatchSpectatorJoinEvent event) {
        Match match = event.getMatch();

        if (!match.getKitType().isHealthShown()) {
            return;
        }

        // initialize the objective and send everyone's health to the spectator who joined
        initialize(event.getSpectator());
        sendAllTo(event.getSpectator(), match);
    }

    @EventHandler
    public void onSpectatorLeave(MatchSpectatorLeaveEvent event) {
        if (!event.getMatch().getKitType().isHealthShown()) {
            return;
        }

        // clear the spectator's health display objective
        clearAll(event.getSpectator());
    }

    @EventHandler
    public void onHealt(EntityRegainHealthEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        // send the health change to everyone
        if (match.getKitType().isHealthShown()) {
            sendToAll(player, match);
        }
    }

/*    @EventHandler
    public void onHealthChange(PlayerHealthChangeEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(player);

        // send the health change to everyone
        if (match.getKitType().isHealthShown()) {
            sendToAll(player, match);
        }
    }

 */

    public void sendAllTo(Player viewer, Match match) {
        Objective objective = viewer.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

        if (objective == null) {
            return; // not initialized
        }

        for (Player target : getPlayers(match)) {
            try {
                PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
                aField.set(packet, target.getDisplayName());
                bField.set(packet, OBJECTIVE_NAME);
                cField.set(packet, getHealth(target));
                dField.set(packet, 0);

                ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToAll(Player target, Match match) {
        try {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
            aField.set(packet, target.getDisplayName());
            bField.set(packet, OBJECTIVE_NAME);
            cField.set(packet, getHealth(target));
            dField.set(packet, 0);

            for (Player viewer : getRecipients(match)) {
                ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize(Player player) {
        if (player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) == null) {
            Objective objective = player.getScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy");
            objective.setDisplayName(ChatColor.DARK_RED + "❤");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
    }

    public void clearAll(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
        if (objective != null) {
            objective.unregister();
        }
        player.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    public void clear(Player viewer, Player target) {
        viewer.getScoreboard().resetScores(target.getDisplayName());
    }

    public List<Player> getRecipients(Match match) {
        List<Player> recipients = new ArrayList<>();
        recipients.addAll(getPlayers(match));
        match.getSpectators().stream().map(Bukkit::getPlayer).forEach(recipients::add);
        return recipients;
    }

    public List<Player> getPlayers(Match match) {
        List<Player> players = new ArrayList<>();

        for (MatchTeam team : match.getTeams()) {
            team.getAliveMembers().stream().map(Bukkit::getPlayer).forEach(players::add);
        }

        return players;
    }

    public int getHealth(Player player) {
        return (int) Math.ceil(player.getHealth() + ((CraftPlayer) player).getHandle().getAbsorptionHearts());
    }

    public static Field aField = null;
    public static Field bField = null;
    public static Field cField = null;
    public static Field dField = null;

    static {
        try {
            aField = PacketPlayOutScoreboardScore.class.getDeclaredField("a");
            aField.setAccessible(true);

            bField = PacketPlayOutScoreboardScore.class.getDeclaredField("b");
            bField.setAccessible(true);

            cField = PacketPlayOutScoreboardScore.class.getDeclaredField("c");
            cField.setAccessible(true);

            dField = PacketPlayOutScoreboardScore.class.getDeclaredField("d");
            dField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}