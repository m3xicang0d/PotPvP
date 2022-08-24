package net.frozenorb.potpvp.game.morpheus.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import org.bukkit.entity.Player;

public class ForceEndCommand {

    @Command(names = {"forceend"}, permission = "op")
    public static void host(Player sender) {
        Game game = GameQueue.INSTANCE.getCurrentGame(sender);

        if (game == null) {
            sender.sendMessage("You're not in a game");
            return;
        }

        game.end();
    }

}
