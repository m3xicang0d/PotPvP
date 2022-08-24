package net.frozenorb.potpvp.player.party;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an invitation for a player (the target)
 * to join a {@link Party}
 */
public final class PartyInvite {

    /**
     * The party the target will be joining upon accepting
     * this invitation.
     */
    @Getter public Party party;

    /**
     * Player that will be joining the party,
     * if they accept this invitation.
     */
    @Getter public UUID target;

    /**
     * The time this invite was sent, used to determine if this
     * invitation is still active.
     * Sent and created are synonymous in this context
     */
    @Getter public Instant timeSent;

    PartyInvite(Party party, UUID target) {
        this.party = Preconditions.checkNotNull(party, "party");
        this.target = Preconditions.checkNotNull(target, "target");
        this.timeSent = Instant.now();
    }

}