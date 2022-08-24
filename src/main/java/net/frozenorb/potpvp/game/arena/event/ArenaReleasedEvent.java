package net.frozenorb.potpvp.game.arena.event;

import lombok.Getter;
import net.frozenorb.potpvp.game.arena.Arena;
import net.frozenorb.potpvp.game.match.Match;
import org.bukkit.event.HandlerList;

/**
 * Called when an {@link Arena} is done being used by a
 * {@link Match}
 */
public final class ArenaReleasedEvent extends ArenaEvent {

    @Getter public static HandlerList handlerList = new HandlerList();

    public ArenaReleasedEvent(Arena arena) {
        super(arena);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}