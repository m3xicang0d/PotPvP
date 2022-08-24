package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyAccessRestriction;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

public final class PartyLockCommand {

    @Command(names = {"party lock", "p lock", "t lock", "team lock", "f lock"}, permission = "")
    public static void partyLock(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
        } else if (party.getAccessRestriction() == PartyAccessRestriction.INVITE_ONLY) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.STATUS.ALREADY-LOCKED")));
        } else {
            party.setAccessRestriction(PartyAccessRestriction.INVITE_ONLY);
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.STATUS.LOCKED")));
        }
    }

}
