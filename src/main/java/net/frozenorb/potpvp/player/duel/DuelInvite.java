package net.frozenorb.potpvp.player.duel;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.game.kittype.KitType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class DuelInvite<T> {
    private final T sender;
    private final T target;
    private final KitType kitType;
    private final Instant timeSent;

    public DuelInvite(T sender, T target, KitType kitType) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.timeSent = Instant.now();
    }

    public boolean isExpired() {
        long sentAgo = ChronoUnit.SECONDS.between(this.timeSent, Instant.now());
        return sentAgo > 30L;
    }

    public T getSender() {
        return this.sender;
    }

    public T getTarget() {
        return this.target;
    }

    public KitType getKitType() {
        return this.kitType;
    }

    public Instant getTimeSent() {
        return this.timeSent;
    }
}

