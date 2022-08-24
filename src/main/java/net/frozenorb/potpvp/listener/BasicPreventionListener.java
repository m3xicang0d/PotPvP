package net.frozenorb.potpvp.listener;

import net.frozenorb.potpvp.PotPvPSI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public final class BasicPreventionListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        // if we have a whitelist we probably are manually
        // controlling who can log in and don't need this
        if (true || Bukkit.hasWhitelist()) {
            return;
        }

        //        ProfileHandler profileHandler = Hydrogen.getInstance().getProfileHandler();
        //        Optional<Profile> profileOpt = profileHandler.getProfile(event.getPlayer().getUniqueId());
        //
        //        boolean allowed = false;
        //
        //        if (profileOpt.isPresent()) {
        //            Map<String, Boolean> perms = profileOpt.get().getPermissions();
        //            allowed = perms.getOrDefault("potpvp.vip", false);
        //        }
        //
        //        if (!allowed) {
        //            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.DARK_PURPLE + "PotPvP is VIP-only for testing");
        //        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntityType() == EntityType.ARROW) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!canInteractWithBlocks(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!canInteractWithBlocks(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public boolean canInteractWithBlocks(Player player) {
        if (PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
            // completely ignore players in matches, MatchBuildListener handles this.
            return true;
        }

        boolean inLobby = PotPvPSI.getInstance().getLobbyHandler().isInLobby(player);
        boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
        boolean isOp = player.isOp();
        boolean buildMeta = player.hasMetadata("Build");

        return inLobby && isCreative && isOp && buildMeta;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(null);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

}