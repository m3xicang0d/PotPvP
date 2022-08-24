package me.jesusmx.practice.practice.game.modes

import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.kit.Kit
import net.frozenorb.potpvp.game.kittype.KitType
import net.frozenorb.potpvp.game.match.event.MatchCountdownStartEvent
import net.frozenorb.potpvp.game.match.event.MatchEndEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


class BaseRaidingMode : PvPMode() {

    @EventHandler
    fun onMatchCountdownStart(event : MatchCountdownStartEvent) {
        val kitHandler = PotPvPSI.instance.kitHandler
        val match = event.match
        if (!match.kitType.id.lowercase().contains("baseraiding")) return
        val raiderKit = KitType.byId("HCF_RAIDER")
        val trapperKit = KitType.byId("HCF_TRAPPER")
        for(team in match.teams) {
            for(uuid in team.allMembers) {
                val player = Bukkit.getPlayer(uuid)
                val kit : KitType = if(match.trapper == uuid) {
                    player.setMetadata("RAIDER", FixedMetadataValue(PotPvPSI.instance, true))
                    //REVISAR EFFECTS
                    player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
                    player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, Int.MAX_VALUE, 1))
                    trapperKit
                } else {
                    player.setMetadata("TRAPPER", FixedMetadataValue(PotPvPSI.instance, true))
                    player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
                    player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, Int.MAX_VALUE, 1))
                    raiderKit
                }
                if (match.getTeam(player.uniqueId) == null) continue

                val customKits = kitHandler.getKits(player, kit)
                val defaultKitItem = Kit.ofDefaultKit(kit).createSelectionItem()

                if (customKits.isEmpty()) {
                    player.inventory.setItem(0, defaultKitItem)
                } else {
                    for (customKit in customKits) {
                        // subtract one to convert from 1-indexed kts to 0-indexed inventories
                        player.inventory.setItem(customKit.getSlot() - 1, customKit.createSelectionItem())
                    }

                    player.inventory.setItem(8, defaultKitItem)
                }
            }
        }
    }

    @EventHandler
    fun onMatchEnd(event : MatchEndEvent) {
        val match = event.match ?: return
        if(!match.isBaseRaiding) return
        for(team in match.teams) {
            for(uuid in team.allMembers) {
                val player = Bukkit.getPlayer(uuid)
                if(player.hasMetadata("RAIDER")) {
                    player.removeMetadata("RAIDER", PotPvPSI.instance)
                }
                if(player.hasMetadata("TRAPPER")) {
                    player.removeMetadata("TRAPPER", PotPvPSI.instance)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event : PlayerInteractEvent) {
        if (!event.hasItem() || !event.action.name.contains("RIGHT_")) {
            return
        }

        val matchHandler = PotPvPSI.instance.matchHandler
        val match = matchHandler.getMatchPlaying(event.player) ?: return

        val kitType = match.kitType
        if(!kitType.id.lowercase().contains("baseraiding")) return
        val kitHandler = PotPvPSI.instance.kitHandler
        val clickedItem = event.item
        val player = event.player

        val raiderKit = KitType.byId("HCF_RAIDER")
        val trapperKit = KitType.byId("HCF_TRAPPER")
        val kt = if(player.hasMetadata("TRAPPER")) {
            trapperKit
        } else {
            raiderKit
        }
        for (kit in kitHandler.getKits(player, kt)) {
            if (kit.isSelectionItem(clickedItem)) {
                kit.apply(player)
                player.sendMessage(ChatColor.YELLOW.toString() + "You equipped your \"" + kit.getName() + "\" " + kt.getDisplayName() + " kit.")
                return
            }
        }

        val defaultKit = Kit.ofDefaultKit(kt)

        if (defaultKit.isSelectionItem(clickedItem)) {
            defaultKit.apply(player)
            player.sendMessage(ChatColor.YELLOW.toString() + "You equipped the default kit for " + kt.getDisplayName() + ".")
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if(player.hasMetadata("RAIDER")) {
            player.removeMetadata("RAIDER", PotPvPSI.instance)
        }
        if(player.hasMetadata("TRAPPER")) {
            player.removeMetadata("TRAPPER", PotPvPSI.instance)
        }
        val matchHandler = PotPvPSI.instance.matchHandler
        val match = matchHandler.getMatchPlaying(event.entity) ?: return
        val kitType = match.kitType
        if(!kitType.id.lowercase().contains("baseraiding")) return


        val raiderKit = KitType.byId("HCF_RAIDER")
        val trapperKit = KitType.byId("HCF_TRAPPER")
        val kt = if(player.hasMetadata("TRAPPER")) {
            trapperKit
        } else {
            raiderKit
        }

        val kitHandler = PotPvPSI.instance.kitHandler
        for (kit in kitHandler.getKits(event.entity, kt)) {
            event.drops.remove(kit.createSelectionItem())
        }
        event.drops.remove(Kit.ofDefaultKit(kt).createSelectionItem())
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerBreakBlock(event : BlockBreakEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player)?: return
        if(!match.kitType.id.lowercase().contains("baseraiding")) return
        event.isCancelled = !player.hasMetadata("TRAPPER")
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerPlaceBlock(event : BlockPlaceEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player)?: return
        if(!match.kitType.id.lowercase().contains("baseraiding")) return
        event.isCancelled = !player.hasMetadata("TRAPPER")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerOpenFence(event : PlayerInteractEvent) {
        val player = event.player
        val match = PotPvPSI.instance.matchHandler.getMatchPlaying(player)?: return
        if(!match.kitType.id.lowercase().contains("baseraiding")) return
        // Prevent spectators from opening fence gates
        if(event.clickedBlock == null) return
        if(event.clickedBlock.type == Material.FENCE_GATE || event.clickedBlock.type == Material.WOOD_DOOR) {
            event.isCancelled = !player.hasMetadata("TRAPPER")
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val matchHandler = PotPvPSI.instance.matchHandler
        val match = matchHandler.getMatchPlaying(event.player) ?: return
        val kitType = match.kitType
        if(!kitType.id.lowercase().contains("baseraiding")) return
        val player = event.player
        val raiderKit = KitType.byId("HCF_RAIDER")
        val trapperKit = KitType.byId("HCF_TRAPPER")
        val kt = if(player.hasMetadata("TRAPPER")) {
            trapperKit
        } else {
            raiderKit
        }

        val kitHandler = PotPvPSI.instance.kitHandler
        val droppedItem = event.itemDrop.itemStack
        for (kit in kitHandler.getKits(event.player, kt)) {
            if (kit.isSelectionItem(droppedItem)) {
                event.isCancelled = true
                return
            }
        }
        val defaultKit = Kit.ofDefaultKit(kt)
        if (defaultKit.isSelectionItem(droppedItem)) {
            event.isCancelled = true
        }
    }
}