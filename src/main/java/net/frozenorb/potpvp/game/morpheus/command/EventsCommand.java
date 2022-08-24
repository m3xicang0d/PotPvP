package net.frozenorb.potpvp.game.morpheus.command;

import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class EventsCommand {
    @Command(names = {"events", "games", "queue"}, permission = "")
    public static void showEvents(Player sender) {
        LinkedList<TextComponent> messages = new LinkedList<>();

        if (GameQueue.INSTANCE.getCurrentGame() != null) {
            TextComponent runningGameMessage = new TextComponent("Running game:");
            runningGameMessage.setColor(ChatColor.GREEN);

            messages.add(runningGameMessage);
            messages.add(gameToTextComponent(GameQueue.INSTANCE.getCurrentGame(), true));
        }

        if (GameQueue.INSTANCE.size() > 0) {
            TextComponent queuedGamesMessage = new TextComponent("Queued games:");
            queuedGamesMessage.setColor(ChatColor.YELLOW);
            messages.add(queuedGamesMessage);
            for (Game game : GameQueue.INSTANCE.getGames()) {
                int index = GameQueue.INSTANCE.getGames().indexOf(game) + 1;
                TextComponent line = new TextComponent(ChatColor.YELLOW.toString() + index + ". ");
                line.addExtra(gameToTextComponent(game, false));
                messages.add(line);
            }
        }

        if (messages.size() > 0) {
            messages.forEach(message -> sender.spigot().sendMessage(message));
        } else {
            sender.sendMessage(ChatColor.YELLOW + "The event queue is empty");
        }
    }

    public static TextComponent gameToTextComponent(Game game, Boolean running) {
        String eventName = game.getEvent().getName();
        String hostName = game.getHost().getDisplayName();

        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;

        TextComponent result = new TextComponent(eventName);
        result.setColor(running ? ChatColor.GREEN : ChatColor.YELLOW);
        result.setBold(running);

        result.setHoverEvent(new HoverEvent(showText, new BaseComponent[]{
            new TextComponent(ChatColor.LIGHT_PURPLE + "Hosted by " + hostName)
        }));

        return result;
    }

}
