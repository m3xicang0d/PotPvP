package net.frozenorb.potpvp.player.rematch;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;
import net.frozenorb.potpvp.game.kittype.KitType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@ToString
public final class RematchData {

    @Getter public final UUID sender;
    @Getter public final UUID target;
    @Getter public final KitType kitType;
    @Getter public final Instant expiresAt;
    @Getter public final String arenaName;

    RematchData(UUID sender, UUID target, KitType kitType, int durationSeconds, String arenaName) {
        this.sender = Preconditions.checkNotNull(sender, "sender");
        this.target = Preconditions.checkNotNull(target, "target");
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.expiresAt = Instant.now().plusSeconds(durationSeconds);
        this.arenaName = Preconditions.checkNotNull(arenaName, "arenaName");
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public int getSecondsUntilExpiration() {
        return (int) ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
    }

}