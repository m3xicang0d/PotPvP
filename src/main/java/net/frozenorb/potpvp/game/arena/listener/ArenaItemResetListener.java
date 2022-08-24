package net.frozenorb.potpvp.game.arena.listener;

import net.frozenorb.potpvp.game.arena.Arena;
import net.frozenorb.potpvp.game.arena.event.ArenaReleasedEvent;
import net.frozenorb.potpvp.kt.util.Cuboid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

/**
 * Remove dropped items when {@link Arena}s are released.
 */
public final class ArenaItemResetListener implements Listener {

    @EventHandler
    public void onArenaReleased(ArenaReleasedEvent event) {
        Set<Chunk> coveredChunks = new HashSet<>();
        Cuboid bounds = event.getArena().getBounds();

        Location minPoint = bounds.getLowerNE();
        Location maxPoint = bounds.getUpperSW();
        World world = minPoint.getWorld();

        // definitely a better way to increment than += 1 but arenas
        // are small enough this doesn't matter
        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                // getChunkAt wants chunk x/z coords, not block coords
                coveredChunks.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }

        // force load all chunks (can't iterate entities in an unload chunk)
        // that are at all covered by this map.
        coveredChunks.forEach(Chunk::load);
        coveredChunks.forEach(chunk -> {
            for (Entity entity : chunk.getEntities()) {
                // if we remove all entities we might call .remove()
                // on a player (breaks a lot of things)
                if (entity instanceof Item && bounds.contains(entity)) {
                    entity.remove();
                }
            }
        });
    }

}