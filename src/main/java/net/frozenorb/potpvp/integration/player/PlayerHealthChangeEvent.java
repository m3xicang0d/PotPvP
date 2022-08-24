package net.frozenorb.potpvp.integration.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerHealthChangeEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final double previousHealth;
    private final double newHealth;

    public PlayerHealthChangeEvent(Player player, double previousHealth, double newHealth) {
        super(player);
        this.previousHealth = previousHealth;
        this.newHealth = newHealth;
    }

    public double getPreviousHealth() {
        return this.previousHealth;
    }

    public double getNewHealth() {
        return this.newHealth;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}