package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.player.party.PartyUtils;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

public final class PartyTeamSplitCommand {

    @Command(names = {"party teamsplit", "p teamsplit", "t teamsplit", "team teamsplit", "f teamsplit"}, permission = "")
    public static void partyTeamSplit(Player sender) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
        } else {
            PartyUtils.startTeamSplit(party, sender);
        }
    }

}