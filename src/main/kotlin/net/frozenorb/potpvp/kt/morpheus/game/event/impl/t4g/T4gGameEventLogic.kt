package net.frozenorb.potpvp.kt.morpheus.game.event.impl.t4g

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue
import net.frozenorb.potpvp.kt.morpheus.game.GameState
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.lms.LastManStandingGameEventLogic
import net.frozenorb.potpvp.kt.morpheus.game.util.team.GameTeam
import fr.mrmicky.fastparticle.FastParticle
import fr.mrmicky.fastparticle.ParticleType
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.util.VisibilityUtils
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.lang.Exception
import kotlin.math.roundToInt


class T4gGameEventLogic(game: Game) : LastManStandingGameEventLogic(game) {

    var roundRunnable = object: BukkitRunnable() {
        override fun run() {

            if (game.state == GameState.ENDED) {
                end();
            }

            if (remainingTimeToBlast == -1) {
                remainingTimeToBlast = 45;
            }

            if (remainingTimeToBlast in 1..5) {
                game.sendMessage(ChatColor.YELLOW.toString() + "Blast in " + remainingTimeToBlast + "...")
                game.players.forEach { player -> player.playSound(player.location, Sound.NOTE_PLING, 1F, 1F) }
            }

            if (remainingTimeToBlast == 0) {
                blastEveryone()
            }

            if (remainingTimeToBlast > 0) {
                remainingTimeToBlast--;
            }
        }
    }

    var startEvent = object: BukkitRunnable() {
        var duration = 15;
        override fun run() {

            if (duration == -1) return cancel();

            for (participant in participants) {
                for (player in participant.players) {
                    if (duration in 1..5) {
                        player.playSound(player.location, Sound.NOTE_PLING, 1F, 1F)
                    }
                    if (duration == 0) {
                        player.sendMessage(ChatColor.GREEN.toString() + "Run!")
                        player.playSound(player.location, Sound.NOTE_PLING, 1F, 2F)
                    } else if (duration%5 == 0 || duration < 5) {
                        player.sendMessage(ChatColor.YELLOW.toString() + "Round starts in " + duration + "...")
                    }
                }
            }

            if (duration == 0) {
                startGame();
                cancel();
                return;
            }

            duration--;
        }
    }

    var currentRound = 1;
    var remainingTimeToBlast = -1;

    lateinit var roundTask : BukkitTask;
    lateinit var startTask : BukkitTask;

    override fun start() {
        for (player in game.players) {
            VisibilityUtils.updateVisibility(player)
            participants.add(GameTeam(player))
            player.teleport(game.arena.team1Spawn);
            player.inventory.clear();
        }

        for (participant in participants) {
            participant.starting = true;
        }


        startTask = startEvent.runTaskTimerAsynchronously(PotPvPSI.instance, 0L, 20L)

    }

    override fun end() {
        try {
            startTask?.cancel()
            roundTask?.cancel();
        } catch(err: Exception) {
            println(err.message)
        }

        super.end()
    }

    override fun check() {
        val alive = ArrayList<GameTeam>()

        for (team in participants) {
            if (!team.isFinished()) {
                alive.add(team)
            }
        }

        if (alive.size == 1) {
            val winner = alive[0]
            broadcastWinner(winner)
            end()
        } else if (alive.size == 0) {
            end()
        }
    }

    fun startGame() {

        for (participant in participants) {
            participant.fighting = true;
            participant.starting = false;
        }


        invites.clear()

        tagRandomPlayers(getRemainingPlayers());

        roundTask = roundRunnable.runTaskTimerAsynchronously(PotPvPSI.instance, 0, 20L);
    }


    fun blastEveryone() {
        var lastParticipant: GameTeam = getRemainingParticipants().last();

        for (participant in getRemainingParticipants()) {
            for (player in participant.players) {
                if (player == null || !player.isOnline) {
                    participant.died(player);
                }
                if (isTagged(player)) {
                    lastParticipant = participant;
                }
                blast(player)
            }
        }

        if (getRemainingParticipants().size <= 1) {

            roundTask?.cancel();

            if (getRemainingParticipants().size > 0) {
                broadcastWinner(getRemainingParticipants().first());
            } else {
                currentRound++;
                broadcastWinner(lastParticipant)
            }

            object: BukkitRunnable() {
                override fun run() {
                    end()
                }
            }.runTaskLater(PotPvPSI.instance, 40L)

        } else {
            remainingTimeToBlast = 45;
            currentRound++;

            tagRandomPlayers(getRemainingPlayers());
            getRemainingPlayers().forEach { player -> player.teleport(game.arena.team1Spawn) };
        }
    }

    fun blast(player: Player) {
        if (isTagged(player)) {
            val game = GameQueue.getCurrentGame(player) ?: return;

            player.getNearbyEntities(4.0, 4.0, 4.0).forEach{ entity ->
                if (entity is Player && !game.spectators.contains(entity)) {
                    unTag(entity);
                    entity.health = 0.0;
                }
            }

            FastParticle.spawnParticle(player.location.world, ParticleType.EXPLOSION_LARGE, player.location, 8, 1.0, 1.0, 1.0)
            FastParticle.spawnParticle(player.location.world, ParticleType.EXPLOSION_HUGE, player.location, 4, 1.0, 1.0, 1.0)
            player.location.world.createExplosion(player.location, 0F);

            unTag(player);
            player.health = 0.0;
        }
    }

    override fun broadcastWinner(winner: GameTeam) {
        var winnerName = winner.players[0].displayName
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(arrayOf("",
                    ChatColor.GRAY.toString() + "███████",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GREEN + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GREEN + ChatColor.BOLD + "${game.event.getName()} Event Winner",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GREEN + "█" + ChatColor.GRAY + "█████" + " ",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GREEN + "████" + ChatColor.GRAY + "██" + " " +  winnerName + ChatColor.GREEN + " won the event!",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GREEN + "█" + ChatColor.GRAY + "█████",
                    ChatColor.GRAY.toString() + "█" + ChatColor.GREEN + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GREEN + "Rounds survived: " + ChatColor.RED +  (currentRound - 1),
                    ChatColor.GRAY.toString() + "███████",
                    "")
            )
        }
    }


    companion object {
        fun isTagged(player: Player) : Boolean {
            return player.inventory.helmet != null && player.inventory.helmet.type == Material.TNT;
        }

        fun tagPlayer(taggerPlayer: Player, taggedPlayer: Player) {
            if (isTagged(taggerPlayer) && !isTagged(taggedPlayer)) {
                unTag(taggerPlayer);

                tag(taggedPlayer);
            }
        }

        fun tag(player: Player) {
            player.inventory.helmet = ItemStack(Material.TNT, 1);
            player.inventory.setItem(0, ItemStack(Material.TNT, 1));
            player.updateInventory();
            player.walkSpeed = 0.3F;

            //Nametag PotPvPSI.instance.nameTagHandler.reloadPlayer(player);
            //Nametag PotPvPSI.instance.nameTagHandler.reloadOthersFor(player);
        }

        fun unTag(player: Player) {
            player.inventory.helmet = null;
            player.inventory.setItem(0, null);
            player.updateInventory();
            player.walkSpeed = 0.2F;

            //Nametag PotPvPSI.instance.nameTagHandler.reloadPlayer(player);
            //Nametag PotPvPSI.instance.nameTagHandler.reloadOthersFor(player);
        }

        fun tagRandomPlayers(players: ArrayList<Player>) {
            val shuffledParticipants = players.shuffled();
            val selectedParticipants = (shuffledParticipants.size / 5).toDouble().roundToInt();

            // tag 20% of the shuffled players
            for (i in 0..selectedParticipants) {
                tag(shuffledParticipants[i])
            }
        }

    }

}