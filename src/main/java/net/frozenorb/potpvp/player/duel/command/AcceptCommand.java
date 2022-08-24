/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.potpvp.player.duel.command;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.player.duel.DuelHandler;
import net.frozenorb.potpvp.player.duel.DuelInvite;
import net.frozenorb.potpvp.player.duel.PartyDuelInvite;
import net.frozenorb.potpvp.player.duel.PlayerDuelInvite;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class AcceptCommand {
    @Command(names={"accept"}, permission="")
    public static void accept(Player sender, @Param(name="player") Player target) {

        ConfigFile messages = PotPvPSI.getInstance().getMessagesConfig();

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't accept a duel from yourself!");
            return;
        }
        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);
        if (senderParty != null && targetParty != null) {
            final PartyDuelInvite invite = duelHandler.findInvite(targetParty, senderParty);
            if (invite != null) {
                acceptParty(sender, senderParty, targetParty, invite);
            } else {
                final String leaderName = PotPvPSI.getInstance().getUuidCache().name(targetParty.getLeader());
                sender.sendMessage(CC.translate(messages.getString("DUEL.OTHER-PARTY-NO-RECEIVE").replaceAll("%leader-name%", leaderName)));
            }
        } else if (senderParty == null && targetParty == null) {
            final PlayerDuelInvite invite2 = duelHandler.findInvite(target, sender);
            if (invite2 != null) {
                acceptPlayer(sender, target, invite2);
            } else {
                sender.sendMessage(CC.translate(messages.getString("DUEL.DONT-HAVE-INVITE-DUEL").replaceAll("%player%", target.getName())));
            }
        } else if (senderParty == null) {
            sender.sendMessage(CC.translate(messages.getString("DUEL.DONT-HAVE-INVITE-DUEL").replaceAll("%player%", target.getName())));
        } else {
            sender.sendMessage(CC.translate(messages.getString("DUEL.YOUR-PARTY-DONT-HAVE-DUEL").replaceAll("%player%", target.getName())));
        }
    }

    private static void acceptParty(Player sender, Party senderParty, Party targetParty, DuelInvite invite) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        if (!senderParty.isLeader(sender.getUniqueId())) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.NO-LEADER-PARTY")));
            return;
        }
        if (!PotPvPValidation.canAcceptDuel(senderParty, targetParty, sender)) {
            return;
        }
        Match match = matchHandler.startMatch(ImmutableList.of(new MatchTeam(senderParty.getMembers()), new MatchTeam(targetParty.getMembers())),
                invite.getKitType(),
                false,
                true,
                ((PartyDuelInvite) invite).getSchematic());
        if (match != null) {
            duelHandler.removeInvite(invite);
        } else {
            senderParty.message(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("ERROR.STARTING-MATCH")));
            targetParty.message(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("ERROR.STARTING-MATCH")));
        }
    }

    private static void acceptPlayer(Player sender, Player target, DuelInvite invite) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        if (!PotPvPValidation.canAcceptDuel(sender, target)) {
            return;
        }
        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(sender.getUniqueId()), new MatchTeam(target.getUniqueId())),
                invite.getKitType(),
                false,
                true, // see Match#allowRematches
                ((PlayerDuelInvite) invite).getSchematic()
        );;
        if (match != null) {
            duelHandler.removeInvite(invite);
        } else {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("ERROR.STARTING-MATCH")));
            target.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("ERROR.STARTING-MATCH")));
        }
    }
}

