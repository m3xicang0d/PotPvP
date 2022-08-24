package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.player.party.PartyHandler;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.entity.Player;

public final class PartyCreateCommand {

    @Command(names = {"party create", "p create", "t create", "team create", "f create"}, permission = "")
    public static void partyCreate(Player sender) {
        if (PotPvPValidation.isInGame(sender)) return;

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.IN-PARTY")));
            return;
        }

        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(CC.translate(PotPvPSI.getInstance().getMessagesConfig().getString("PARTY.CREATE")));
    }

}