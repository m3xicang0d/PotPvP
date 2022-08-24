package net.frozenorb.potpvp.game.match.listener;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.Kit;
import net.frozenorb.potpvp.game.kit.KitHandler;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.game.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.game.pvpclasses.PvPClasses;
import net.frozenorb.potpvp.player.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public final class KitSelectionListener implements Listener {

    /**
     * Give players their kits when their match countdown starts
     */
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        Match match = event.getMatch();
        KitType kitType = match.getKitType();

        if (kitType.getId().toLowerCase(Locale.ROOT).contains("baseraiding")) return;
        if (kitType.getId().equalsIgnoreCase("SUMO")) return; // no kits for sumo

        for (Player player : Bukkit.getOnlinePlayers()) {
            MatchTeam team = match.getTeam(player.getUniqueId());

            if (team == null) {
                continue;
            }

            List<Kit> customKits = kitHandler.getKits(player, kitType);
            ItemStack defaultKitItem = Kit.ofDefaultKit(kitType).createSelectionItem();


            if (kitType.equals(KitType.teamFight)) {
                KitType bard = KitType.byId("BARD_HCF");
                KitType diamond = KitType.byId("DIAMOND_HCF");
                KitType archer = KitType.byId("ARCHER_HCF");

                Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

                if (party == null) {
                    Kit.ofDefaultKit(diamond).apply(player);
                } else {
                    PvPClasses kit = party.getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);

                    if (kit == null || kit == PvPClasses.DIAMOND) {
                        List<Kit> cKits = kitHandler.getKits(player, diamond);
                        if(cKits.isEmpty()) {
                            Kit.ofDefaultKit(diamond).apply(player);
                        } else {
                            for (Kit customKit : cKits) {
                                // subtract one to convert from 1-indexed kts to 0-indexed inventories
                                player.getInventory().setItem(customKit.getSlot() - 1, customKit.createSelectionItem());
                            }

                            player.getInventory().setItem(8, defaultKitItem);
                        }
                    } else if (kit == PvPClasses.BARD) {
                        List<Kit> cKits = kitHandler.getKits(player, bard);
                        if(cKits.isEmpty()) {
                            Kit.ofDefaultKit(bard).apply(player);
                        } else {
                            for (Kit customKit : cKits) {
                                // subtract one to convert from 1-indexed kts to 0-indexed inventories
                                player.getInventory().setItem(customKit.getSlot() - 1, customKit.createSelectionItem());
                            }

                            player.getInventory().setItem(8, defaultKitItem);
                        }
                    } else {
                        List<Kit> cKits = kitHandler.getKits(player, archer);
                        if(cKits.isEmpty()) {
                            Kit.ofDefaultKit(archer).apply(player);
                        } else {
                            for (Kit customKit : cKits) {
                                // subtract one to convert from 1-indexed kts to 0-indexed inventories
                                player.getInventory().setItem(customKit.getSlot() - 1, customKit.createSelectionItem());
                            }

                            player.getInventory().setItem(8, defaultKitItem);
                        }
                    }

                }

            } else {
                // if they have no kits saved place default in 0, otherwise
                // the default goes in 9 and they get custom kits from 1-4
                if (customKits.isEmpty()) {
                    player.getInventory().setItem(0, defaultKitItem);
                } else {
                    for (Kit customKit : customKits) {
                        // subtract one to convert from 1-indexed kts to 0-indexed inventories
                        player.getInventory().setItem(customKit.getSlot() - 1, customKit.createSelectionItem());
                    }

                    player.getInventory().setItem(8, defaultKitItem);
                }
            }


            player.updateInventory();
        }
    }

    /**
     * Don't let players drop their kit selection books via the Q key
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        KitType kitType = match.getKitType();
        if(kitType.id.toLowerCase(Locale.ROOT).contains("baseraiding")) return;


        for (Kit kit : kitHandler.getKits(event.getPlayer(), kitType)) {
            if (kit.isSelectionItem(droppedItem)) {
                event.setCancelled(true);
                return;
            }
        }

        Kit defaultKit = Kit.ofDefaultKit(kitType);

        if (defaultKit.isSelectionItem(droppedItem)) {
            event.setCancelled(true);
        }
    }

    /**
     * Don't let players drop their kit selection items via death
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        KitType kitType = match.getKitType();
        if(kitType.id.toLowerCase(Locale.ROOT).contains("baseraiding")) return;


        for (Kit kit : kitHandler.getKits(event.getEntity(), kitType)) {
            event.getDrops().remove(kit.createSelectionItem());
        }

        event.getDrops().remove(Kit.ofDefaultKit(kitType).createSelectionItem());
    }

    /**
     * Give players their kits upon right click
     */
    // no ignoreCancelled = true because right click on air
    // events are by default cancelled (wtf Bukkit)
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        ItemStack clickedItem = event.getItem();
        KitType kitType = match.getKitType();
        if(kitType.id.toLowerCase(Locale.ROOT).contains("baseraiding")) return;
        Player player = event.getPlayer();

        if (kitType.equals(KitType.teamFight)) {
            KitType bard = KitType.byId("BARD_HCF");
            KitType diamond = KitType.byId("DIAMOND_HCF");
            KitType archer = KitType.byId("ARCHER_HCF");

            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);

            if (party == null) {
                Kit.ofDefaultKit(diamond).apply(player);
            } else {
                PvPClasses kit = party.getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);

                if (kit == null || kit == PvPClasses.DIAMOND) {
                    for (Kit k : kitHandler.getKits(player, diamond)) {
                        if (k.isSelectionItem(clickedItem)) {
                            k.apply(player);
                            player.sendMessage(ChatColor.YELLOW + "You equipped your \"" + k.getName() + "\" " + diamond.getDisplayName() + " kit.");
                            return;
                        }
                    }
                } else if (kit == PvPClasses.BARD) {
                    for (Kit k : kitHandler.getKits(player, bard)) {
                        if (k.isSelectionItem(clickedItem)) {
                            k.apply(player);
                            player.sendMessage(ChatColor.YELLOW + "You equipped your \"" + k.getName() + "\" " + bard.getDisplayName() + " kit.");
                            return;
                        }
                    }
                } else {
                    for (Kit k : kitHandler.getKits(player, archer)) {
                        if (k.isSelectionItem(clickedItem)) {
                            k.apply(player);
                            player.sendMessage(ChatColor.YELLOW + "You equipped your \"" + k.getName() + "\" " + archer.getDisplayName() + " kit.");
                            return;
                        }
                    }
                }
            }
        } else {
            for (Kit kit : kitHandler.getKits(player, kitType)) {
                if (kit.isSelectionItem(clickedItem)) {
                    kit.apply(player);
                    player.sendMessage(ChatColor.YELLOW + "You equipped your \"" + kit.getName() + "\" " + kitType.getDisplayName() + " kit.");
                    return;
                }
            }

            Kit defaultKit = Kit.ofDefaultKit(kitType);

            if (defaultKit.isSelectionItem(clickedItem)) {
                defaultKit.apply(player);
                player.sendMessage(ChatColor.YELLOW + "You equipped the default kit for " + kitType.getDisplayName() + ".");
            }
        }

    }

}