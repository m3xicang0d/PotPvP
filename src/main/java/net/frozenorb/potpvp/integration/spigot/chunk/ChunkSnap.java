package net.frozenorb.potpvp.integration.spigot.chunk;

import org.bukkit.Chunk;

import java.lang.reflect.InvocationTargetException;

public abstract class ChunkSnap<T> {

    public abstract T takeSnapshot(Chunk chunk) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    public abstract void restoreSnapshot(Chunk chunk, T chunkSnapshot) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}