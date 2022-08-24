package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

public final class PartyDisbandCommand {

    @Command(names = {"party disband", "p disband", "t disband", "team disband", "f disband"}, permission = "")
    public static void partyDisband(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
            return;
        }

        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
            return;
        }

        party.disband();
    }

}