package me.jesusmx.practice.practice.integration.scoreboard.provider.match.sub.fight

import me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable.DurationVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable.OpponentVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.match.variable.PingVariable
import me.jesusmx.practice.practice.game.modes.BoxingMode.Companion.formatHits
import me.jesusmx.practice.practice.integration.scoreboard.other.SubScoreboard
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.game.kittype.HealingMethod
import net.frozenorb.potpvp.game.match.Match
import net.frozenorb.potpvp.game.match.MatchState
import net.frozenorb.potpvp.game.match.MatchTeam
import net.frozenorb.potpvp.game.pvpclasses.pvpclasses.ArcherClass
import net.frozenorb.potpvp.game.pvpclasses.pvpclasses.BardClass
import net.frozenorb.potpvp.kt.potion.cache.PotionCache
import net.frozenorb.potpvp.util.CC
import net.frozenorb.potpvp.kt.scoreboard.ScoreFunction.Companion.TIME_FANCY
import net.frozenorb.potpvp.kt.scoreboard.ScoreFunction.Companion.TIME_SIMPLE
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors
import kotlin.math.roundToLong


@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class FighterScoreboard : SubScoreboard<Match>() {

    private val durationVariable = DurationVariable()
    private val opponentVariable = OpponentVariable()
    private val pingVariable = PingVariable()

    override fun accept(player : Player, scores : MutableList<String>, match : Match) {
        val teams = match.teams
        if (teams.size != 2) {
            return
        }
        val ourTeam = match.getTeam(player.uniqueId)
        val otherTeam = if (teams[0] == ourTeam) teams[1] else teams[0]

        val ourTeamSize = ourTeam.allMembers.size
        val otherTeamSize = otherTeam.allMembers.size

        if (ourTeamSize == 1 && otherTeamSize == 1) {
            this.render1v1MatchLines(scores, otherTeam, player, match)
        } else if (ourTeamSize <= 2 && otherTeamSize <= 2) {
            this.render2v2MatchLines(scores, ourTeam, otherTeam, player, match.kitType.healingMethod)
        } else if (ourTeamSize <= 4 && otherTeamSize <= 4) {
            this.render4v4MatchLines(scores, ourTeam, otherTeam)
        } else if (ourTeam.allMembers.size <= 9) {
            this.renderLargeMatchLines(scores, ourTeam, otherTeam)
        } else {
            this.renderJumboMatchLines(scores, ourTeam, otherTeam)
        }

        val archerMarkScore: String? = this.getArcherMarkScore(player)
        val bardEffectScore: String? = this.getBardEffectScore(player)
        val bardEnergyScore: String? = this.getBardEnergyScore(player)


        // Server Network
        //
        // Archer Tag
        //
        if (archerMarkScore != null) {
            scores.add("&6&lArcher Tagged&7: &f$archerMarkScore")
        }
        if (bardEffectScore != null) {
            scores.add("&a&lBard Effect&7: &f$bardEffectScore")
        }
        if (bardEnergyScore != null) {
            scores.add("&e&lBard Energy&7: &f$bardEnergyScore")
        }
    }

    private fun render1v1MatchLines(scores: MutableList<String>, otherTeam: MatchTeam, player: Player, match: Match) {
        val opponent = Bukkit.getPlayer(otherTeam.firstMember)
        val config = PotPvPSI.instance.scoreboardConfig
        if (match.kitType.id.equals("Boxing", ignoreCase = true)) {
            val ownHits = match.boxingHits.getOrDefault(player.uniqueId, 0)
            val opponentHits = match.boxingHits.getOrDefault(opponent.uniqueId, 0)
            scores.addAll(CC.translate(config.getStringList("IN-MATCH-BOXING").stream()
                    .map { it.replace("%format_hits%", formatHits(ownHits, opponentHits)) }
                    .map { it.replace("%own_hits%", ownHits.toString()) }
                    .map { it.replace("%duration%", durationVariable.format(player, match)) }
                    .map { it.replace("%opponent%", opponentVariable.format(player, match)) }
                    .map { it.replace("%opponent_hits%", opponentHits.toString()) }
                    .collect(Collectors.toList())))
        } else if (match.kitType.id.equals("baseraiding", ignoreCase = true)) {
            for (s in CC.translate(config.getStringList("IN-HCF-TRAPPING"))) {
                if (s.contains("%ability%")) {
                    if (PotPvPSI.instance.abilitySystem.isOnGlobalCooldown(player)) {
                        scores.add(s.replace("%ability%", PotPvPSI.instance.scoreboardConfig.getString("GLOBAL.ABILITY").replace("%cooldown%", PotPvPSI.instance.abilitySystem.getRemaining(player))))
                    } else {
                        continue
                    }
                } else {
                    scores.add(s.replace("%duration%", durationVariable.format(player, match))
                            .replace("%opponent%", opponentVariable.format(player, match)))
                }
            }
        } else {
            scores.addAll(CC.translate(config.getStringList("IN-MATCH-NORMAL").stream()
                    .map { it.replace("%duration%", durationVariable.format(player, match)) }
                    .map { it.replace("%opponent%", opponentVariable.format(player, match)) }
                    .map { it.replace("%opponent-ping%", pingVariable.format(player, Bukkit.getPlayer(otherTeam.firstMember))) }
                    .map { it.replace("%your-ping%", pingVariable.format(player, player)) }
                    .collect(Collectors.toList())))
        }
    }

    fun render2v2MatchLines(
        scores: MutableList<String>,
        ourTeam: MatchTeam,
        otherTeam: MatchTeam,
        player: Player,
        healingMethod: HealingMethod?
    ) {
        var partnerUuid: UUID? = null
        for (teamMember in ourTeam.allMembers) {
            if (teamMember === player.uniqueId) continue
            partnerUuid = teamMember
            break
        }
        if (partnerUuid != null) {
            val healthStr: String
            val namePrefix: String
            val healsStr: String
            if (ourTeam.isAlive(partnerUuid)) {
                val partnerPlayer = Bukkit.getPlayer(partnerUuid)
                val health = partnerPlayer.health.roundToLong().toDouble() / 2.0
                val heals : Int = PotionCache.map.getOrDefault(partnerUuid, 0)
                val healthColor =
                    if (health > 8.0) ChatColor.GREEN else if (health > 6.0) ChatColor.YELLOW else if (health > 4.0) ChatColor.GOLD else if (health > 1.0) ChatColor.RED else ChatColor.DARK_RED
                val healsColor =
                    if (heals > 20) ChatColor.GREEN else if (heals > 12) ChatColor.YELLOW else if (heals > 8) ChatColor.GOLD else if (heals > 3) ChatColor.RED else ChatColor.DARK_RED
                namePrefix = "&a"
                healthStr = healthColor.toString() + health + " *\u2764* " + ChatColor.GRAY
                healsStr =
                    if (healingMethod != null) healsColor.toString() + heals + " " + (if (heals == 1) healingMethod.shortSingular else healingMethod.shortPlural) else ""
            } else {
                namePrefix = "&7&m"
                healthStr = "&4RIP"
                healsStr = ""
            }
            scores.add(namePrefix + PotPvPSI.instance.uuidCache.name(partnerUuid))
            scores.add(healthStr + healsStr)
            scores.add("&b")
        }
        scores.add("&cEnemies")
        scores.addAll(this.renderTeamMemberOverviewLines(otherTeam))
        if (PotPvPSI.instance.matchHandler.getMatchPlaying(player).state == MatchState.IN_PROGRESS) {
            scores.add("&c")
        }
    }

    fun render4v4MatchLines(scores: MutableList<String>, ourTeam: MatchTeam, otherTeam: MatchTeam) {
        scores.add("&a&lTeam &f(" + ourTeam.aliveMembers.size + "/" + ourTeam.allMembers.size + ")")
        scores.addAll(this.renderTeamMemberOverviewLinesWithHearts(ourTeam))
        scores.add("&b")
        scores.add("&c&lEnemies &c(" + otherTeam.aliveMembers.size + "/" + otherTeam.allMembers.size + ")")
        if (PotPvPSI.instance.matchHandler
                .getMatchPlaying(Bukkit.getPlayer(ourTeam.firstAliveMember)).state == MatchState.IN_PROGRESS
        ) {
            scores.add("&c")
        }
    }

    fun renderLargeMatchLines(scores: MutableList<String>, ourTeam: MatchTeam, otherTeam: MatchTeam) {
        scores.add("&a&lTeam &f(" + ourTeam.aliveMembers.size + "/" + ourTeam.allMembers.size + ")")
        scores.addAll(this.renderTeamMemberOverviewLinesWithHearts(ourTeam))
        scores.add("&b")
        scores.add("&c&lEnemies: &f" + otherTeam.aliveMembers.size + "/" + otherTeam.allMembers.size)
    }

    fun renderJumboMatchLines(scores: MutableList<String>, ourTeam: MatchTeam, otherTeam: MatchTeam) {
        scores.add("&a&lTeam: &f" + ourTeam.aliveMembers.size + "/" + ourTeam.allMembers.size)
        scores.add("&c&lEnemies: &f" + otherTeam.aliveMembers.size + "/" + otherTeam.allMembers.size)
    }

    fun renderTeamMemberOverviewLinesWithHearts(team: MatchTeam): List<String> {
        val aliveLines: ArrayList<String> = ArrayList()
        val deadLines: ArrayList<String> = ArrayList()
        for (teamMember in team.allMembers) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(
                    " &f" + PotPvPSI.instance.uuidCache.name(teamMember) + " " + this.getHeartString(
                        team,
                        teamMember
                    )
                )
                continue
            }
            deadLines.add(" &7&m" + PotPvPSI.instance.uuidCache.name(teamMember))
        }
        val result: ArrayList<String> = ArrayList()
        result.addAll(aliveLines)
        result.addAll(deadLines)
        return result
    }

    fun renderTeamMemberOverviewLines(team: MatchTeam): List<String> {
        val aliveLines: ArrayList<String> = ArrayList()
        val deadLines: ArrayList<String> = ArrayList()
        for (teamMember in team.allMembers) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPSI.instance.uuidCache.name(teamMember))
                continue
            }
            deadLines.add(" &7&m" + PotPvPSI.instance.uuidCache.name(teamMember))
        }
        val result: ArrayList<String> = ArrayList()
        result.addAll(aliveLines)
        result.addAll(deadLines)
        return result
    }

    fun getHeartString(ourTeam: MatchTeam, partnerUuid: UUID?): String {
        if (partnerUuid != null) {
            val healthStr: String = if (ourTeam.isAlive(partnerUuid)) {
                val partnerPlayer = Bukkit.getPlayer(partnerUuid)
                val health = partnerPlayer.health.roundToLong().toDouble() / 2.0
                val healthColor =
                    if (health > 8.0) ChatColor.GREEN else if (health > 6.0) ChatColor.YELLOW else if (health > 4.0) ChatColor.GOLD else if (health > 1.0) ChatColor.RED else ChatColor.DARK_RED
                "$healthColor($health \u2665)"
            } else {
                "&4(RIP)"
            }
            return healthStr
        }
        return "&4(RIP)"
    }

    private fun getArcherMarkScore(player: Player): String? {
        if(!ArcherClass.isMarked(player)) return null
        val diff: Long = ArcherClass.markedPlayers[player.name]!! - System.currentTimeMillis()
        return if (diff > 0L) {
            TIME_FANCY.apply(java.lang.Float.valueOf(diff.toFloat() / 1000.0f))
        } else null
    }

    private fun getBardEffectScore(player: Player): String? {
        if(!BardClass.lastEffectUsage.containsKey(player.name)) return null
        val diff: Float = BardClass.lastEffectUsage[player.name]!!.toFloat()
        return if(diff >= System.currentTimeMillis() && ((diff - System.currentTimeMillis()) > 0.0)) {
            TIME_SIMPLE.apply((diff / 1000.0f).toInt())
        } else {
            null
        }
    }

    private fun getBardEnergyScore(player: Player): String? {
        if(!BardClass.energy.containsKey(player.name)) return null
        val energy: Int = BardClass.energy[player.name]!!.toInt()
        return if(energy > 0) {
            energy.toString()
        } else {
            null
        }
    }
}