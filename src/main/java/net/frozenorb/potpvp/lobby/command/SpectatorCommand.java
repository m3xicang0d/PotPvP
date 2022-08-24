package net.frozenorb.potpvp.lobby.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.util.validation.PotPvPValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class SpectatorCommand {

    @Command(names = "spectator")
    public static void toggleSpectatorMode(Player sender) {
        if (PotPvPValidation.isInGame(sender)) return;

        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
        if (lobbyHandler.isInLobby(sender)) {
            Boolean isInSpectatorMode = lobbyHandler.isInSpectatorMode(sender);
            lobbyHandler.setSpectatorMode(sender, !isInSpectatorMode);
            sender.sendMessage(isInSpectatorMode ? ChatColor.RED + "Spectator mode disabled." : ChatColor.GREEN + "Spectator mode enabled.");
        }
    }
}
