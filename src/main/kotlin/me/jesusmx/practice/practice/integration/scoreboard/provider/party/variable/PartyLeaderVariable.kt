package me.jesusmx.practice.practice.integration.scoreboard.provider.party.variable

import me.jesusmx.practice.practice.integration.scoreboard.other.Variable
import net.frozenorb.potpvp.PotPvPSI
import org.bukkit.entity.Player

class PartyLeaderVariable : Variable<Boolean>() {

    override fun format(player: Player, t: Boolean): String {
        return PotPvPSI.instance.uuidCache.name(PotPvPSI.instance.partyHandler.getParty(player.uniqueId).leader)
    }
}