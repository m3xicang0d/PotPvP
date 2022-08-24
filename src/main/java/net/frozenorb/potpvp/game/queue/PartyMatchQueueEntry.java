package net.frozenorb.potpvp.game.queue;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.frozenorb.potpvp.player.party.Party;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link Party} waiting
 * in a {@link MatchQueue}
 */
public final class PartyMatchQueueEntry extends MatchQueueEntry {

    @Getter public final Party party;

    PartyMatchQueueEntry(MatchQueue queue, Party party) {
        super(queue);

        this.party = Preconditions.checkNotNull(party, "party");
    }

    @Override
    public Set<UUID> getMembers() {
        return party.getMembers();
    }

}