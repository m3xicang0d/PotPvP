package net.frozenorb.potpvp.game.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.match.event.MatchEndEvent;
import net.frozenorb.potpvp.game.match.event.MatchStartEvent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Collection;
import java.util.Objects;

public class HorseListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        if (!match.getKitType().isSpawnHorses()) return;
        match.getTeams()
            .stream()
            .map(MatchTeam::getAllMembers)
            .flatMap(Collection::stream)
            .map(Bukkit::getPlayer)
            .filter(Objects::nonNull)
            .forEach(player -> spawnHorse(player, match));
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        match.getEntities().forEach(Entity::remove);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(player.getVehicle() == null) return;
        if (!player.getVehicle().getType().equals(EntityType.HORSE)) return;

        player.getVehicle().remove();
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        if (!matchHandler.isPlayingMatch(player)) event.setCancelled(true);
    }

    public void spawnHorse(Player player, Match match) {
        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        match.getEntities().add(horse);

        ((EntityLiving)((CraftEntity)horse).getHandle()).getAttributeInstance(GenericAttributes.c).setValue(0.2);
        horse.setMaxHealth(30);
        horse.setHealth(30);
        horse.setVariant(Horse.Variant.HORSE);
        horse.setAdult();
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));

        horse.setPassenger(player);
    }

}