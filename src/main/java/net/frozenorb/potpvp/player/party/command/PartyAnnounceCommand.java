package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyAccessRestriction;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PartyAnnounceCommand {

    @Command(names = {"party announce", "p announce", "t announce", "team announce", "f announce"}, permission = "potpvp.party.announce", vipFeature = true)
    public static void announce(Player sender) {
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        /*if (party == null) {
            sender.sendMessage(PotPvPLang.NOT_IN_PARTY);
            return;
        }
        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
            return;
        }*/

        if (party.getAccessRestriction() != PartyAccessRestriction.PUBLIC) {
            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
            sender.sendMessage(CC.translate(
                "&eYour party is now &aopen&e."));
        }

        Clickable clickable = new Clickable(sender.getDisplayName() +
            " &ais hosting a public party type &a'&c/p join " + sender.getName() + "&a' &aor &cclick here&a to join",
            "&cClick here&a to join",
            "/p join " + sender.getName());

        Bukkit.getOnlinePlayers().forEach(clickable::sendToPlayer);
    }

}
