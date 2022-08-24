package net.frozenorb.potpvp.game.queue;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

public abstract class MatchQueueEntry {

    /**
     * {@link MatchQueue} this MatchQueueEntry is entered in
     */
    @Getter public final MatchQueue queue;

    /**
     * Time this MatchQueueEntry joined its {@link MatchQueue}
     */
    @Getter public final Instant timeJoined;

    MatchQueueEntry(MatchQueue queue) {
        this.queue = queue;
        this.timeJoined = Instant.now();
    }

    public abstract Set<UUID> getMembers();

    /**
     * Gets how long, in seconds, this MatchQueueEntry has been waiting in a queue
     *
     * @return the duration, in seconds, this entry has been waiting in a queue
     */
    public int getWaitSeconds() {
        return (int) ChronoUnit.SECONDS.between(timeJoined, Instant.now());
    }

}