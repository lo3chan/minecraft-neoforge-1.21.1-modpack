/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 */
package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.List;
import net.diebuddies.physics.ocean.IChunk;
import net.diebuddies.physics.ocean.Index;
import net.diebuddies.physics.ocean.WorldUtil;

public abstract class IWorld<T extends IChunk> {
    public final Long2ObjectMap<T> loadedChunks = new Long2ObjectOpenHashMap(25600);
    public final int minVoxelY;
    public final int maxVoxelY;
    public final int minChunkY;
    public final int maxChunkY;

    public IWorld(int minChunkY, int maxChunkY) {
        this.minChunkY = minChunkY;
        this.maxChunkY = maxChunkY;
        this.minVoxelY = minChunkY * 16;
        this.maxVoxelY = maxChunkY * 16;
    }

    public T getChunk(int x, int y, int z) {
        if (y < this.minChunkY || y > this.maxChunkY) {
            return null;
        }
        IChunk c = (IChunk)this.loadedChunks.get(Index.chunk(x, y, z));
        return (T)c;
    }

    private T removeChunk(long index) {
        IChunk c = (IChunk)this.loadedChunks.remove(index);
        if (c != null) {
            c.setLoadedNeighbourCount(0);
            this.editNeighbourCount(c.x, c.y, c.z, -1);
            c.setWorld(null);
        }
        return (T)c;
    }

    public void removeChunkColumn(int x, int z) {
        for (int y = this.minChunkY; y <= this.maxChunkY; ++y) {
            this.removeChunk(Index.chunk(x, y, z));
        }
    }

    public void removeAll() {
        LongOpenHashSet copy = new LongOpenHashSet((LongCollection)this.loadedChunks.keySet());
        LongIterator it = copy.longIterator();
        while (it.hasNext()) {
            long index = it.nextLong();
            this.removeChunk(index);
        }
    }

    public void addChunkColumn(List<T> chunkColumn) {
        for (IChunk chunk : chunkColumn) {
            this.addChunk(chunk);
        }
    }

    private void addChunk(T chunk) {
        long index = Index.chunk(((IChunk)((Object)chunk)).x, ((IChunk)((Object)chunk)).y, ((IChunk)((Object)chunk)).z);
        if (this.loadedChunks.get(index) != null) {
            T t = this.removeChunk(index);
        }
        this.loadedChunks.put(index, chunk);
        ((IChunk)((Object)chunk)).setWorld((IWorld)this);
        ((IChunk)((Object)chunk)).setLoadedNeighbourCount(this.editNeighbourCount(((IChunk)((Object)chunk)).x, ((IChunk)((Object)chunk)).y, ((IChunk)((Object)chunk)).z, 1));
    }

    public boolean isChunkLoaded(int x, int y, int z) {
        if (y > this.maxChunkY) {
            return true;
        }
        if (y < this.minChunkY) {
            return true;
        }
        IChunk c = (IChunk)this.loadedChunks.get(Index.chunk(x, y, z));
        return c != null;
    }

    public byte getData(int x, int y, int z) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            return ((IChunk)c).getData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z));
        }
        return 0;
    }

    public T getChunkWorldPos(int x, int y, int z) {
        if (y < this.minVoxelY || y > this.maxVoxelY) {
            return null;
        }
        int chunkX = WorldUtil.calculateChunkPosX(x);
        int chunkY = WorldUtil.calculateChunkPosY(this, y);
        int chunkZ = WorldUtil.calculateChunkPosZ(z);
        return this.getChunk(chunkX, chunkY, chunkZ);
    }

    public T getChunkWorldPos(double x, double y, double z) {
        if (y < (double)this.minVoxelY || y > (double)this.maxVoxelY) {
            return null;
        }
        int chunkX = WorldUtil.calculateChunkPosX(x);
        int chunkY = WorldUtil.calculateChunkPosY(this, y);
        int chunkZ = WorldUtil.calculateChunkPosZ(z);
        return this.getChunk(chunkX, chunkY, chunkZ);
    }

    public void setData(int x, int y, int z, byte data) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            ((IChunk)c).setData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), data);
        }
    }

    public int editNeighbourCount(int chunkX, int chunkY, int chunkZ, int edit) {
        int count = 0;
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                IChunk neighbour;
                if (x == 0 && z == 0 || (neighbour = (IChunk)this.loadedChunks.get(Index.chunk(chunkX + x, chunkY, chunkZ + z))) == null) continue;
                neighbour.setLoadedNeighbourCount(neighbour.getLoadedNeighbourCount() + edit);
                ++count;
            }
        }
        return count;
    }

    public boolean areSurroundingsLoaded(int x, int y, int z) {
        T c = this.getChunk(x, y, z);
        return c != null && ((IChunk)c).getLoadedNeighbourCount() == 8;
    }
}

