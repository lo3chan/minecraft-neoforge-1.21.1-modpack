/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.joml.Math
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.snow.IWorld;
import net.diebuddies.physics.snow.Index;
import net.diebuddies.physics.snow.storage.EqualStorageType;
import net.diebuddies.physics.snow.storage.StorageContainer;
import org.joml.Vector3f;
import org.joml.Vector3i;

public abstract class IChunk<T extends IWorld> {
    public static final int ALL_NEIGHBOURS_LOADED = 8;
    public static final byte MIN_VALUE = -127;
    public static final byte MAX_VALUE = 127;
    public static final int LIGHT_SIZE = 16;
    public static final int MAX_LIGHT = 255;
    public static int CHUNK_SIZE;
    public static int CHUNK_SIZE_HALF;
    public static int CHUNK_SIZE_BITS;
    public static int CHUNK_SIZE_USED_BITS;
    public static int CHUNK_VOLUME;
    public static int CHUNK_MULTIPLE;
    public static int CHUNK_MULTIPLE_BITS;
    public static float CHUNK_MULTIPLE_INV;
    protected T world;
    public final StorageContainer dataStorage;
    public final StorageContainer lightStorage;
    public StorageContainer modulationStorage;
    public final int x;
    public final int y;
    public final int z;
    public final int xVoxel;
    public final int yVoxel;
    public final int zVoxel;
    public final int hashCode;
    protected int loadedNeighbourCount;
    public Int2ObjectMap<IntSet> activeNodes = new Int2ObjectOpenHashMap();

    public static void updateChunkSize() {
        if (ConfigClient.snowQuality == 0) {
            IChunk.setChunkSize(32);
        } else {
            IChunk.setChunkSize(64);
        }
    }

    private static void setChunkSize(int chunkSize) {
        CHUNK_SIZE = chunkSize;
        CHUNK_SIZE_HALF = CHUNK_SIZE / 2;
        CHUNK_SIZE_BITS = CHUNK_SIZE - 1;
        CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(CHUNK_SIZE_BITS);
        CHUNK_VOLUME = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
        CHUNK_MULTIPLE = CHUNK_SIZE / 16;
        CHUNK_MULTIPLE_BITS = 32 - Integer.numberOfLeadingZeros(CHUNK_MULTIPLE) - 1;
        CHUNK_MULTIPLE_INV = 1.0f / (float)CHUNK_MULTIPLE;
        Index.updateMasks();
    }

    public IChunk(int x, int y, int z, StorageContainer dataStorage, StorageContainer lightStorage) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xVoxel = x * CHUNK_SIZE;
        this.yVoxel = y * CHUNK_SIZE;
        this.zVoxel = z * CHUNK_SIZE;
        this.dataStorage = dataStorage;
        this.lightStorage = lightStorage;
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

    public float getData(int x, int y, int z) {
        if (this.outOfBounds(x, y, z)) {
            return (float)((IWorld)this.world).getData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z) / 127.0f;
        }
        return (float)this.dataStorage.getData(x, y, z) / 127.0f;
    }

    public byte getDataByte(int x, int y, int z) {
        if (this.outOfBounds(x, y, z)) {
            return ((IWorld)this.world).getData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z);
        }
        return this.dataStorage.getData(x, y, z);
    }

    public byte getDataByteFast(int x, int y, int z) {
        return this.dataStorage.getData(x, y, z);
    }

    public byte getLightDataByte(int x, int y, int z) {
        if (this.outOfBounds(x, y, z)) {
            return ((IWorld)this.world).getLightData(this.xVoxel + x, this.yVoxel + y, this.zVoxel + z);
        }
        return this.lightStorage.getData(x, y, z);
    }

    public byte getLightDataByteFast(int x, int y, int z) {
        return this.lightStorage.getData(x, y, z);
    }

    public void setData(int x, int y, int z, byte data) {
        this.dataStorage.setData(x, y, z, data);
    }

    public void setLightData(int x, int y, int z, byte data) {
        this.lightStorage.setData(x, y, z, data);
    }

    public boolean outOfBounds(int x, int y, int z) {
        return x >= CHUNK_SIZE || y >= CHUNK_SIZE || z >= CHUNK_SIZE || x < 0 || y < 0 || z < 0;
    }

    public float getDensity(float x, float y, float z) {
        int ix = (int)Math.round(Math.floor(x));
        int iy = (int)Math.round(Math.floor(y));
        int iz = (int)Math.round(Math.floor(z));
        float p1 = this.getData(ix, iy, iz);
        float p2 = this.getData(ix + 1, iy, iz);
        float p3 = this.getData(ix, iy + 1, iz);
        float p4 = this.getData(ix + 1, iy + 1, iz);
        float p5 = this.getData(ix, iy, iz + 1);
        float p6 = this.getData(ix + 1, iy, iz + 1);
        float p7 = this.getData(ix, iy + 1, iz + 1);
        float p8 = this.getData(ix + 1, iy + 1, iz + 1);
        float xFinal1 = org.joml.Math.lerp((float)p1, (float)p2, (float)(x - (float)ix));
        float xFinal2 = org.joml.Math.lerp((float)p3, (float)p4, (float)(x - (float)ix));
        float xFinal3 = org.joml.Math.lerp((float)p5, (float)p6, (float)(x - (float)ix));
        float xFinal4 = org.joml.Math.lerp((float)p7, (float)p8, (float)(x - (float)ix));
        float yFinal1 = org.joml.Math.lerp((float)xFinal1, (float)xFinal2, (float)(y - (float)iy));
        float yFinal2 = org.joml.Math.lerp((float)xFinal3, (float)xFinal4, (float)(y - (float)iy));
        return org.joml.Math.lerp((float)yFinal1, (float)yFinal2, (float)(z - (float)iz));
    }

    public Vector3f calculateNormal(float x, float y, float z, float offset, Vector3f normal) {
        normal.x = this.getDensity(x + offset, y, z) - this.getDensity(x - offset, y, z);
        normal.y = this.getDensity(x, y + offset, z) - this.getDensity(x, y - offset, z);
        normal.z = this.getDensity(x, y, z + offset) - this.getDensity(x, y, z - offset);
        if (normal.x == 0.0f && normal.y == 0.0f && normal.z == 0.0f) {
            normal.x = 0.0f;
            normal.y = 1.0f;
            normal.z = 0.0f;
        }
        normal.negate();
        normal.normalize();
        return normal;
    }

    public Vector3f calculateNormal(float x, float y, float z, float offset) {
        return this.calculateNormal(x, y, z, offset, new Vector3f());
    }

    public boolean isStorageBorderSameSign() {
        StorageContainer storage = this.dataStorage;
        if (storage.getStorageType() instanceof EqualStorageType) {
            boolean positiveSign = false;
            byte data = this.getDataByte(0, 0, 0);
            if (data >= 0) {
                positiveSign = true;
            }
            for (int i = 0; i <= 1; ++i) {
                for (int j = 0; j <= 1; ++j) {
                    for (int k = 0; k <= 1; ++k) {
                        if (i == 0 && j == 0 && k == 0) continue;
                        Object chunk = ((IWorld)this.world).getChunk(this.x + i, this.y + j, this.z + k);
                        if (chunk != null) {
                            StorageContainer neighbourStorage = ((IChunk)chunk).dataStorage;
                            if (neighbourStorage.getStorageType() instanceof EqualStorageType) {
                                if (!(positiveSign ? neighbourStorage.getData(0, 0, 0) < 0 : neighbourStorage.getData(0, 0, 0) >= 0)) continue;
                                return false;
                            }
                            return false;
                        }
                        if (!positiveSign) continue;
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean isSolid(Vector3i pos) {
        return this.isSolid(pos.x, pos.y, pos.z);
    }

    public boolean isSolid(int x, int y, int z) {
        return this.getDataByte(x, y, z) >= 0;
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

    static {
        IChunk.updateChunkSize();
    }
}

