package net.frozenorb.potpvp.game.morpheus.command;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.queue.QueueHandler;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.frozenorb.potpvp.kt.morpheus.game.GameState;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.player.party.PartyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayCommand {

    @Command(names = {"play"}, permission = "")
    public static void host(Player sender) {
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();
        if (!lobbyHandler.isInLobby(sender) || lobbyHandler.isInSpectatorMode(sender)) return;

        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        if (queueHandler.isQueued(sender.getUniqueId())) return;

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You must leave your party to join the event!");
            return;
        }
        for (Game game : GameQueue.INSTANCE.getCurrentGames()) {
            if (game.getState() == GameState.STARTING) {
                if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                    sender.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                    return;
                }
                game.add(sender);
            } else {
                game.addSpectator(sender);
            }
        }
    }
}
