package net.frozenorb.potpvp.kt.morpheus.game.util.team

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEventLogic
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.lms.LastManStandingGameEvent
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.skywars.SkywarsGameEvent
import net.frozenorb.potpvp.util.VisibilityUtils
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

abstract class GameTeamEventLogic(private val game: Game) : GameEventLogic {
    var invites: MutableMap<UUID, UUID> = HashMap()
    var participants: MutableSet<GameTeam> = HashSet()

    override fun start() {
        if (game.getParameter(GameTeamSizeParameter.Duos.javaClass) != null) {
            generateTeams()
        } else {
            for (player in game.players) {
                VisibilityUtils.updateVisibility(player)
                participants.add(GameTeam(player))
            }
        }

        invites.clear()

    }

    public fun generateTeams() {
        outer@ for (player in game.players.toMutableList()) {
            if (!contains(player)) {
                for (other in game.players) {
                    if (player != other && !contains(other)) {
                        player.sendMessage(ChatColor.YELLOW.toString() + "You were automatically put into a team with " + other.displayName + ChatColor.YELLOW + "")
                        other.sendMessage(ChatColor.YELLOW.toString() + "You were automatically put into a team with " + player.displayName + ChatColor.YELLOW + "")

                        val team = GameTeam(player, other)
                        participants.add(team)

                        continue@outer
                    }
                }

                if (game.event is LastManStandingGameEvent || game.event is SkywarsGameEvent) {
                    val team = GameTeam(player)
                    participants.add(team)
                } else {
                    player.sendMessage(ChatColor.RED.toString() + "We couldn't find a player for you to team up with, so you were sent to the lobby.")
                    game.addSpectator(player)
                }
            }
        }
    }

    fun get(player: Player): GameTeam? {
        for (participant in participants) {
            if (participant.players.contains(player)) {
                return participant
            }
        }
        return null
    }

    fun contains(player: Player): Boolean {
        for (participant in participants) {
            if (participant.players.contains(player)) {
                return true
            }
        }
        return false
    }
}