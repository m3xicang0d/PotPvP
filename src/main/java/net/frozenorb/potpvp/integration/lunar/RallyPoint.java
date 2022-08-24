package net.frozenorb.potpvp.integration.lunar;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor @Getter
public class RallyPoint {

    public Player player;
    public Location location;
    public LCWaypoint lcWaypoint;
    public final int duration = 3;  //In minutes
    public final long creationMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(duration);

    public boolean expired() {
        return System.currentTimeMillis() >= creationMillis;
    }
}
