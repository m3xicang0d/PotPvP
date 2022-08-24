package net.frozenorb.potpvp.game.pvpclasses.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.potpvp.game.pvpclasses.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class BardRestoreEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    @Getter public Player player;
    @Getter public PvPClass.SavedPotion potions;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}