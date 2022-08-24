package net.frozenorb.potpvp.player.party.event;

import lombok.Getter;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.player.party.command.PartyDisbandCommand;
import org.bukkit.event.HandlerList;

/**
 * Called when a {@link Party} is disbanded.
 *
 * @see PartyDisbandCommand
 * @see Party#disband()
 */
public final class PartyDisbandEvent extends PartyEvent {

    @Getter public static HandlerList handlerList = new HandlerList();

    public PartyDisbandEvent(Party party) {
        super(party);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}