package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyAccessRestriction;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.Clickable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyOpenCommand {

    @Command(names = {"party open", "p open", "t open", "team open", "f open", "party unlock", "p unlock", "t unlock", "team unlock", "f unlock"}, permission = "party.command.announce")
    public static void partyOpen(Player sender) {
        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);
        if (party == null) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
        } else if (party.getAccessRestriction() == PartyAccessRestriction.PUBLIC) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.STATUS.ALREADY-OPEN")));
            sender.sendMessage(ChatColor.RED + "Your party is already open.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.STATUS.OPEN")));
            Clickable clickable = new Clickable(
                    PotPvPSI.getInstance().messagesConfig.getString("PARTY.ANNOUNCE.MESSAGE"),
                    PotPvPSI.getInstance().messagesConfig.getString("PARTY.ANNOUNCE.HOVER"),
                    "/p announce");

            clickable.sendToPlayer(sender);
        }
    }
}

//    CODE CHANGED ATT: JESUSMX
//
//
//    public static void partyOpen(Player sender) {
//        Party party = PotPvPSI.getInstance().getPartyHandler().getParty(sender);
//
//        if (party == null) {
//            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-IN-PARTY")));
//        } else if (!party.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
//        } else if (party.getAccessRestriction() == PartyAccessRestriction.PUBLIC) {
//            sender.sendMessage(ChatColor.RED + "Your party is already open.");
//        } else {
//            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
//            Clickable clickable = new Clickable(
//                "&eYour party is now &aopen&e, type &c/p announce &eor &cclick here to &eannounce it on the chat",
//                "&cClick here to &eannounce",
//                "/p announce");
//
//            clickable.sendToPlayer(sender);
//        }
//    }