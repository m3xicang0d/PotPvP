package net.frozenorb.potpvp.integration.spigot.chunk.impl;

import eu.vortexdev.api.chunk.ChunkSnapshot;
import net.frozenorb.potpvp.integration.spigot.chunk.ChunkSnap;
import org.bukkit.Chunk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VortexSpigotChunk extends ChunkSnap<ChunkSnapshot> {

    @Override
    public ChunkSnapshot takeSnapshot(Chunk chunk) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = chunk.getClass().getDeclaredMethod("takeSnapshot");
        return (ChunkSnapshot) method.invoke(method, null);
    }

    @Override
    public void restoreSnapshot(Chunk chunk, ChunkSnapshot chunkSnapshot) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = chunk.getClass().getDeclaredMethod("restoreSnapshot", ChunkSnapshot.class);
        method.invoke(chunk, chunkSnapshot);
    }
}