package net.frozenorb.potpvp.player.party.event;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.frozenorb.potpvp.player.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player is kicked from their {@link Party}.
 */
public final class PartyMemberKickEvent extends PartyEvent {

    @Getter public static HandlerList handlerList = new HandlerList();

    @Getter public final Player member;

    public PartyMemberKickEvent(Player member, Party party) {
        super(party);

        this.member = Preconditions.checkNotNull(member, "member");
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}