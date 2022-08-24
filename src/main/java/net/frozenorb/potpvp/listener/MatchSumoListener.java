package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchState;
import net.frozenorb.potpvp.game.match.MatchTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * @Author FxMxGRAGFX
 * @Project Practice
 **/

public class MatchSumoListener implements Listener {

    private HashMap<Match, HashMap<UUID, Boolean>> map = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) return;
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getDamager() instanceof Player)) return;
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        Match match = matchHandler.getMatchPlaying(damager);
        if(match == null) return;
        if(!match.getKitType().getId().equalsIgnoreCase("Sumo")) return;
        MatchTeam victimTeam = match.getTeam(player.getUniqueId());
        MatchTeam damagerTeam = match.getTeam(damager.getUniqueId());
        if (victimTeam != null && victimTeam != damagerTeam) {
            event.setDamage(0);
        }
    }
/*
    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);
        if(match == null) return;

        if (match.getKitType().getId().equalsIgnoreCase("Sumo")) {
            event.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);
        if(match == null) return;
        if(!match.getKitType().getId().equalsIgnoreCase("Sumo")) return;
        if(match.getState() != MatchState.IN_PROGRESS) return;
        if(match.isSpectator(player.getUniqueId())) return;
        if(player.getLocation().getBlock().getType().name().contains("WATER")) {
            match.markDead(player);
            match.addSpectator(player, null, true);
            Bukkit.getServer().getOnlinePlayers().forEach(t -> {
                t.hidePlayer(player);
            });
        }
    }
}