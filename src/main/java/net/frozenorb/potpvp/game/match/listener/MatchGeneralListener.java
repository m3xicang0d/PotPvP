package net.frozenorb.potpvp.game.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.Arena;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchState;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.kt.util.Cuboid;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.ENDER_PEARL;

public final class MatchGeneralListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getEntity();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null) {
            return;
        }

        // creates 'proper' player death animation (of the player falling over)
        // which we don't get due to our immediate respawn
        PlayerUtils.animateDeath(player, true);

        match.markDead(player);
        match.addSpectator(player, null, true);
        player.teleport(player.getLocation().add(0, 2, 0));

        // if we're ending the match we don't drop pots/bowls
        if (match.getState() == MatchState.ENDING) {
            //event.getDrops().removeIf(i -> i.getType() == Material.POTION || i.getType() == Material.GLASS_BOTTLE || i.getType() == Material.MUSHROOM_SOUP || i.getType() == Material.BOWL);
            event.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null) {
            return;
        }

        MatchState state = match.getState();

        if (state == MatchState.COUNTDOWN || state == MatchState.IN_PROGRESS) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

                // if this player has no relation to the match skgfip
                if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                    continue;
                }

                String playerPrefix = PotPvPNametagProvider.getNameColor(player, onlinePlayer);
                String playerFormatted = playerPrefix + player.getName();

                onlinePlayer.sendMessage(playerFormatted + ChatColor.YELLOW + " disconnected.");
            }
        }

        // run this regardless of match state
        match.markDead(player);
    }

    // "natural" teleports (like enderpearls) are forwarded down and
    // treated as a move event, plugin teleports (specifically
    // those originating in this plugin) are ignored.
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        switch (event.getCause()) {
            case PLUGIN:
            case COMMAND:
            case UNKNOWN:
                return;
            default:
                break;
        }

        if (event.getCause() == ENDER_PEARL) {
            handlePearl(event);
        }

        onPlayerMove(event);
    }

    public void handlePearl(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);

        if (match == null) {
            return;
        }

        Arena arena = match.getArena();
        ArenaSchematic schematic = PotPvPSI.getInstance().getArenaHandler().getSchematic(arena.getSchematic());

        if (!schematic.isAllowPearls()) {
            player.sendMessage(ChatColor.RED + "Enderpearls are disabled on this arena!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (
            from.getBlockX() == to.getBlockX() &&
                from.getBlockY() == to.getBlockY() &&
                from.getBlockZ() == to.getBlockZ()
        ) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);

        if (match == null) {
            return;
        }

        Arena arena = match.getArena();
        Cuboid bounds = arena.getBounds();

        // pretend the vertical bounds of the arena are 2 blocks lower than they
        // are to avoid issues with players hitting their heads on the glass (Jon said to do this)
        // looks kind of funny but in a high frequency event this is by far the fastest
        if (!bounds.contains(to) || !bounds.contains(to.getBlockX(), to.getBlockY() + 2, to.getBlockZ())) {
            // spectators get a nice message, players just get cancelled
            if (match.isSpectator(player.getUniqueId())) {
                player.teleport(arena.getSpectatorSpawn());
            } else if (to.getBlockY() >= bounds.getUpperY() || to.getBlockY() <= bounds.getLowerY()) { // if left vertically

                if ((match.getKitType().getId().equalsIgnoreCase("SUMO") || match.getKitType().getId().equalsIgnoreCase("SPLEEF"))) {
                    if (to.getBlockY() <= bounds.getLowerY() && bounds.getLowerY() - to.getBlockY() <= 20) return; // let the player fall 10 blocks
                    match.markDead(player);
                    match.addSpectator(player, null, true);
                }

                player.teleport(arena.getSpectatorSpawn());
            } else {
                if (match.getKitType().getId().equalsIgnoreCase("SUMO") || match.getKitType().getId().equalsIgnoreCase("SPLEEF")) { // if they left horizontally
                    match.markDead(player);
                    match.addSpectator(player, null, true);
                    player.teleport(arena.getSpectatorSpawn());
                }

                event.setCancelled(true);
            }
        } else if (to.getBlockY() + 5 < arena.getSpectatorSpawn().getBlockY()) { // if the player is still in the arena bounds but fell down from the spawn point
            if (match.getKitType().getId().equalsIgnoreCase("SUMO") || match.getKitType().getId().equalsIgnoreCase("SPLEEF")) {
                match.markDead(player);
                match.addSpectator(player, null, true);
                player.teleport(arena.getSpectatorSpawn());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(event.getPlayer());

        if (match == null) return;

        if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.CHEST ||
            event.getClickedBlock().getType() == Material.FURNACE ||
            event.getClickedBlock().getType() == Material.BEACON ||
            event.getClickedBlock().getType() == Material.BREWING_STAND ||
            event.getClickedBlock().getType() == Material.HOPPER)) {
            //event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
        }

        // Stop if player is not spectating
        if (!match.getSpectators().contains(event.getPlayer().getUniqueId())) return;

        // Prevent spectators from opening fence gates
        if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.FENCE_GATE || event.getClickedBlock().getType() == Material.TRAP_DOOR)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void fixFoodOnGame(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlaying(player);
        if(match == null) return;
        event.setCancelled(!match.getKitType().isFoodLevelChange());
    }

    /**
     * Prevents (non-fall) damage between ANY two players not on opposing {@link MatchTeam}s.
     * This includes cancelling damage from a player not in a match attacking a player in a match.
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        // in the context of an EntityDamageByEntityEvent, DamageCause.FALL
        // is the 0 hearts of damage and knockback applied when hitting
        // another player with a thrown enderpearl. We allow this damage
        // in order to be consistent with HCTeams
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player victim = (Player) event.getEntity();
        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        if (damager == null) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(damager);
        boolean isSpleef = match != null && match.getKitType().getId().equalsIgnoreCase("SPLEEF");
        boolean isSumo = match != null && match.getKitType().getId().equalsIgnoreCase("SUMO");

        // we only specifically allow damage where both players are in a match together
        // and not on the same team, everything else is cancelled.
        if (match != null) {
            MatchTeam victimTeam = match.getTeam(victim.getUniqueId());
            MatchTeam damagerTeam = match.getTeam(damager.getUniqueId());

            if (isSpleef && event.getDamager() instanceof Snowball) return;

            if (isSumo && victimTeam != null && victimTeam != damagerTeam) {
                // Ugly hack because people actually lose health & hunger in sumo somehow
                event.setDamage(0);
                return;
            }

            if (victimTeam != null && victimTeam != damagerTeam && !isSpleef) {
                return;
            }
        }


        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();
        Material itemType = itemStack.getType();
        String itemTypeName = itemType.name().toLowerCase();
        int heldSlot = player.getInventory().getHeldItemSlot();

        // don't let players drop swords, axes, and bows in the first slot
        if (PlayerUtils.hasOtherInventoryOpen(player) && heldSlot == 0 && (itemTypeName.contains("sword") || itemTypeName.contains("axe") || itemType == Material.BOW)) {
            player.sendMessage(ChatColor.RED + "You can't drop that while you're holding it in slot 1.");
            event.setCancelled(true);
        }

        // glass bottles and bowls are removed from inventories but
        // don't spawn items on the ground
        if (itemType == Material.GLASS_BOTTLE || itemType == Material.BOWL) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();

        if (!matchHandler.isPlayingMatch(player)) {
            return;
        }

        Match match = matchHandler.getMatchPlaying(event.getPlayer());
        if (match == null) return;

        if (match.getState() == MatchState.ENDING || match.getState() == MatchState.TERMINATED) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.POTION) return;

        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            //event.getPlayer().setItemInHand(null);
        }, 1L);
    }
}