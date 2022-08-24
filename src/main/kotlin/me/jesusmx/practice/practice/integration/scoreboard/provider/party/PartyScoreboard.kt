package me.jesusmx.practice.practice.integration.scoreboard.provider.party

import me.jesusmx.practice.practice.integration.scoreboard.other.Scoreboard
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.FightingVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.InQueueVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.lobby.variable.OnlineVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.party.variable.PartyLeaderVariable
import me.jesusmx.practice.practice.integration.scoreboard.provider.party.variable.PartyMembersVariable
import net.frozenorb.potpvp.PotPvPSI
import net.frozenorb.potpvp.util.CC
import org.bukkit.entity.Player
import java.util.stream.Collectors

class PartyScoreboard : Scoreboard() {

    private val onlineVariable = OnlineVariable()
    private val fightingVariable = FightingVariable()
    private val inQueueVariable = InQueueVariable()
    private val partyMembersVariable = PartyMembersVariable()
    private val partyLeaderVariable = PartyLeaderVariable()

    override fun accept(player : Player, scores : MutableList<String>) {
        scores.addAll(CC.translate(PotPvPSI.instance.scoreboardConfig.getStringList("IN-LOBBY-PARTY").stream()
                .map { it.replace("%online%", onlineVariable.format(player, true)) }
                .map { it.replace("%fighting%", fightingVariable.format(player, true)) }
                .map { it.replace("%in-queue%", inQueueVariable.format(player, true)) }
                .map { it.replace("%party-members%", partyMembersVariable.format(player, true)) }
                .map { it.replace("%party-leader%", partyLeaderVariable.format(player, true)) }
                .collect(Collectors.toList())))
    }
}