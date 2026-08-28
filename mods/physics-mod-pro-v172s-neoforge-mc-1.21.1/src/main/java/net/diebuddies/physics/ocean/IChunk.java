/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.IWorld;
import net.diebuddies.physics.ocean.storage.StorageContainer;
import org.joml.Vector3i;

public abstract class IChunk<T extends IWorld> {
    public static final int ALL_NEIGHBOURS_LOADED = 8;
    public static final byte MIN_VALUE = -127;
    public static final byte MAX_VALUE = 127;
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_SIZE_HALF = 8;
    public static final int CHUNK_SIZE_BITS = 15;
    public static final int CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(15);
    public static final int CHUNK_VOLUME = 4096;
    public static final byte AIR = 0;
    public static final byte SOLID = -1;
    public static final byte WATERLOGGED = -2;
    protected T world;
    public final StorageContainer dataStorage;
    public final int x;
    public final int y;
    public final int z;
    public final int xVoxel;
    public final int yVoxel;
    public final int zVoxel;
    public final int hashCode;
    protected int loadedNeighbourCount;

    public IChunk(int x, int y, int z, StorageContainer dataStorage) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xVoxel = x * 16;
        this.yVoxel = y * 16;
        this.zVoxel = z * 16;
        this.dataStorage = dataStorage;
        int prime = 31;
        int chashCode = 1;
        chashCode = 31 * chashCode + x;
        chashCode = 31 * chashCode + y;
        this.hashCode = chashCode = 31 * chashCode + z;
    }

    public int getLoadedNeighbourCount() {
        return this.loadedNeighbourCount;
    }

    public void setLoadedNeighbourCount(int loadedNeighbourCount) {
        this.loadedNeighbourCount = loadedNeighbourCount;
    }

    public void setWorld(T world) {
        this.world = world;
    }

    public T getWorld() {
        return this.world;
    }

    public byte getData(int x, int y, int z) {
        if (this.outOfBounds(x, y, z)) {
            return ((IWorld)this.world).getData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z);
        }
        return this.dataStorage.getData(x, y, z);
    }

    public byte getDataFast(int x, int y, int z) {
        return this.dataStorage.getData(x, y, z);
    }

    public void setData(int x, int y, int z, byte data) {
        this.dataStorage.setData(x, y, z, data);
    }

    public boolean outOfBounds(int x, int y, int z) {
        return x >= 16 || y >= 16 || z >= 16 || x < 0 || y < 0 || z < 0;
    }

    public boolean isSolid(Vector3i pos) {
        return this.isSolid(pos.x, pos.y, pos.z);
    }

    public boolean isSolid(int x, int y, int z) {
        return this.getData(x, y, z) <= -1;
    }

    public IChunk getNeighbourChunk(int xOffset, int yOffset, int zOffset) {
        return ((IWorld)this.world).getChunk(this.x + xOffset, this.y + yOffset, this.z + zOffset);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        IChunk other = (IChunk)obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return this.z == other.z;
    }
}

