/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.Index;
import net.diebuddies.physics.snow.WorldUtil;
import net.diebuddies.physics.snow.math.Ray;
import net.diebuddies.physics.snow.math.RayHit;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

public abstract class IWorld<T extends IChunk> {
    public static final byte VOXEL_ADD = 0;
    public static final byte VOXEL_SUBTRACT = 1;
    public static final byte VOXEL_SET = 2;
    public final Long2ObjectMap<T> loadedChunks = new Long2ObjectOpenHashMap(25600);
    public final int minVoxelY;
    public final int maxVoxelY;
    public final int minChunkY;
    public final int maxChunkY;
    public final int heightChunks;

    public IWorld(int minChunkY, int maxChunkY) {
        this.minChunkY = minChunkY;
        this.maxChunkY = maxChunkY;
        this.minVoxelY = minChunkY * IChunk.CHUNK_SIZE;
        this.maxVoxelY = maxChunkY * IChunk.CHUNK_SIZE;
        this.heightChunks = maxChunkY - minChunkY + 1;
    }

    public T getChunk(int x, int y, int z) {
        if (y < this.minChunkY || y > this.maxChunkY) {
            return null;
        }
        IChunk c = (IChunk)this.loadedChunks.get(Index.chunk(x, y, z));
        return (T)c;
    }

    public T removeChunk(long index) {
        IChunk c = (IChunk)this.loadedChunks.remove(index);
        if (c != null) {
            c.setLoadedNeighbourCount(0);
            this.editNeighbourCount(c.x, c.y, c.z, -1);
            c.setWorld(null);
        }
        return (T)c;
    }

    public void addChunk(T chunk) {
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

    public float getDensity(double x, double y, double z) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            return ((IChunk)c).getDensity(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z));
        }
        return -1.0f;
    }

    public byte getData(int x, int y, int z) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            return ((IChunk)c).getDataByte(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z));
        }
        return -127;
    }

    public byte getLightData(int x, int y, int z) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            return ((IChunk)c).getLightDataByte(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z));
        }
        return -16;
    }

    public Vector3f calculateNormal(double x, double y, double z) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            return ((IChunk)c).calculateNormal(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), 1.0f);
        }
        return new Vector3f(0.0f, -1.0f, 0.0f);
    }

    public boolean isSolid(int x, int y, int z) {
        return this.getData(x, y, z) >= 0;
    }

    public boolean isSolid(double x, double y, double z) {
        return this.getDensity(x, y, z) >= 0.0f;
    }

    public boolean isSolid(Vector3i pos) {
        return this.isSolid(pos.x, pos.y, pos.z);
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

    public void setLightData(int x, int y, int z, byte data) {
        T c = this.getChunkWorldPos(x, y, z);
        if (c != null) {
            ((IChunk)c).setLightData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), data);
        }
    }

    public RayHit castFastLevelRay(Ray ray, double maxLength, double stepSize, double binarySearchStepSize, int maxSteps) {
        ray.getDirection().normalize();
        Vector3d currentPosition = new Vector3d((Vector3dc)ray.getStart());
        boolean hit = false;
        double length = 0.0;
        for (int steps = 0; !hit && steps < maxSteps; ++steps) {
            boolean bl = hit = (double)this.getDensity(currentPosition.x, currentPosition.y, currentPosition.z) >= 0.0;
            if (hit) {
                currentPosition.sub(ray.getDirection().x * stepSize, ray.getDirection().y * stepSize, ray.getDirection().z * stepSize);
                int i = 0;
                while ((double)i <= stepSize / binarySearchStepSize) {
                    boolean bl2 = hit = (double)this.getDensity(currentPosition.x, currentPosition.y, currentPosition.z) >= 0.0;
                    if (hit) {
                        return new RayHit(new Vector3d((Vector3fc)this.calculateNormal(currentPosition.x, currentPosition.y, currentPosition.z)), currentPosition);
                    }
                    currentPosition.add(ray.getDirection().x * binarySearchStepSize, ray.getDirection().y * binarySearchStepSize, ray.getDirection().z * binarySearchStepSize);
                    ++i;
                }
                return new RayHit(new Vector3d((Vector3fc)this.calculateNormal(currentPosition.x, currentPosition.y, currentPosition.z)), currentPosition);
            }
            length += stepSize;
            currentPosition.add(ray.getDirection().x * stepSize, ray.getDirection().y * stepSize, ray.getDirection().z * stepSize);
            if (!(length >= maxLength)) continue;
            return null;
        }
        return null;
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

