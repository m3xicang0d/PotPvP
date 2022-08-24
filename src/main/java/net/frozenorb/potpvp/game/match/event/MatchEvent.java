package net.frozenorb.potpvp.game.match.event;

import net.frozenorb.potpvp.game.match.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchEvent extends Event {

    public final Match match;

    public MatchEvent(Match match) {
        this.match = match;
    }

    public static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Match getMatch() {
        return match;
    }
}