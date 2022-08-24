package net.frozenorb.potpvp.kt.morpheus.game

import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.GameStateChangeEvent
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.PlayerJoinGameEvent
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.lms.LastManStandingGameEventLogic
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameterOption
import net.frozenorb.potpvp.kt.morpheus.game.util.team.GameTeamSizeParameter
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.arena.Arena
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Game(val event: GameEvent, val host: Player, val parameters: List<GameParameterOption>) {

    var state = GameState.QUEUED
    var startingAt = 0L
    var players = HashSet<Player>()
    val logic = event.getLogic(this)
    val spectators = HashSet<Player>()
    lateinit var arena: Arena

    fun addSpectator(player: Player) {
        if (state == GameState.ENDED) {
            return
        }

        spectators.add(player)
        players.add(player)

        sendMessage(player.displayName + ChatColor.GRAY + " is now spectating.")

        reset(player)

        if (logic is LastManStandingGameEventLogic) {
            player.teleport(arena.team1Spawn)
        } else {
            player.teleport(arena.spectatorSpawn)
        }

        Bukkit.getPluginManager().callEvent(PlayerJoinGameEvent(player, this))
    }

    fun add(player: Player) {
        val other = GameQueue.getCurrentGame(player)

        if (other != null) {
            return
        }

        if (state != GameState.STARTING) {
            return
        }

        players.add(player)

        sendMessage(player.displayName + ChatColor.GRAY + " joined the event.")

        reset(player)

        Bukkit.getPluginManager().callEvent(PlayerJoinGameEvent(player, this))
    }

    public fun resetSpectator(player: Player) {
        player.inventory.clear()
        player.inventory.heldItemSlot = 0
        player.inventory.armorContents = null
        player.gameMode = GameMode.CREATIVE
        player.inventory.setItem(8, GameEvent.leaveItem)
        player.health = player.maxHealth
        player.foodLevel = 20

        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }

        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0))

        player.updateInventory()
    }

    fun reset(player: Player) {

        if (spectators.contains(player)) {
            resetSpectator(player)
            return
        }

        player.teleport(arena.spectatorSpawn.clone().add(0.0, -1.0, 0.0))

        player.inventory.clear()
        player.inventory.heldItemSlot = 0
        player.inventory.armorContents = null
        player.gameMode = GameMode.SURVIVAL
        player.inventory.setItem(8, GameEvent.leaveItem)
        player.health = player.maxHealth
        player.foodLevel = 20

        for (effect in player.activePotionEffects) {
            player.removePotionEffect(effect.type)
        }

        player.updateInventory()
    }

    fun start() {
        if (!(event.canStart(this))) {
            end()
            return
        }

        arena.takeSnapshot()
        logic.start()

        Bukkit.getPluginManager().callEvent(GameStateChangeEvent(this, GameState.RUNNING))
    }

    fun end() {
        Bukkit.getScheduler().runTaskLater(PotPvPSI.instance, {
//            for (player in players) {
//                for (other in players) {
//                    if (player != other && !VisibilityUtils.shouldSeePlayer(player, other)) {
//                        player.hidePlayer(other, VisibilityUtils.shouldHidePlayerFromTablist(player, other))
//                    }
//                }
//            }
            for (player in this.players) {
                if (player != null && player.isOnline) {
                    PotPvPSI.instance.lobbyHandler.returnToLobby(player);
                }
            }
            for (player in this.spectators) {
                if (player != null && player.isOnline) {
                    PotPvPSI.instance.lobbyHandler.returnToLobby(player);
                }
            }
        }, 20L)
        arena.restore()
        Bukkit.getPluginManager().callEvent(GameStateChangeEvent(this, GameState.ENDED))
    }

    fun getSecondSpawnLocations(): Array<Location> {
        if (getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            val direction = arena.team2Spawn.direction
            return arrayOf(
                    arena.team2Spawn.clone().add(direction.clone().setX(-direction.z).setZ(direction.x)),
                    arena.team2Spawn.clone().add(direction.clone().setX(direction.z).setZ(-direction.x))
            )
        } else {
            return arrayOf(arena.team2Spawn)
        }
    }

    fun sendMessage(vararg message: String) {
        for (player in players) {
            player.sendMessage(message)
        }
    }

    fun getFirstSpawnLocations(): Array<Location> {
        if (getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            val direction = arena.team1Spawn.direction
            return arrayOf(
                    arena.team1Spawn.clone().add(direction.clone().setX(-direction.z).setZ(direction.x)),
                    arena.team1Spawn.clone().add(direction.clone().setX(direction.z).setZ(-direction.x))
            )
        } else {
            return arrayOf(arena.team1Spawn)
        }
    }

    fun <T> getParameter(clazz: Class<T>): GameParameterOption? {
        for (parameter in parameters) {
            if (parameter.javaClass == clazz || clazz.isAssignableFrom(parameter.javaClass)) {
                return clazz.cast(parameter) as GameParameterOption
            }
        }
        return null
    }

    fun getMaxPlayers(): Int {
        return -1
    }

}