package net.frozenorb.potpvp.util.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HalfHourEvent extends Event {

    public HalfHourEvent(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int hour;
    public int minute;
    public static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

}
