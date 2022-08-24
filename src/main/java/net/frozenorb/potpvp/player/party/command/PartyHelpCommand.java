package net.frozenorb.potpvp.player.party.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.entity.Player;

import java.util.List;

public final class PartyHelpCommand {

    public static final List<String> HELP_MESSAGE = CC.translate(PotPvPSI.getInstance().getConfig().getStringList("PARTY.COMMAND"));

    @Command(names = {"party", "p", "f", "party help", "p help", "f help"}, permission = "")
    public static void party(Player sender) {
        HELP_MESSAGE.forEach(sender::sendMessage);
    }

}