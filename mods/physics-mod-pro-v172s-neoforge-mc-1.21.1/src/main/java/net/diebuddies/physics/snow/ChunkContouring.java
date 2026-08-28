/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.snow.ChunkRender;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.WorldContouring;
import net.diebuddies.physics.snow.contouring.OctreeNode;
import net.diebuddies.physics.snow.contouring.Vertex;
import net.diebuddies.physics.snow.storage.StorageContainer;
import net.diebuddies.physics.snow.thread.ChunkLoadMeshEvent;
import net.diebuddies.physics.snow.thread.ChunkUnloadMeshEvent;
import net.diebuddies.physics.snow.thread.MultipleEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class ChunkContouring
extends IChunk<WorldContouring> {
    private static final float LOD_0_DISTANCE = (float)Math.pow(130.0, 2.0);
    private static final float LOD_1_DISTANCE = (float)Math.pow(190.0, 2.0);
    private static final float LOD_2_DISTANCE = (float)Math.pow(300.0, 2.0);
    private boolean voxelLODUpdated = true;
    private boolean voxelsUpdated = true;
    private boolean lightsUpdated = true;
    private ChunkRender chunkRender;
    private boolean priority;
    public Vector3d chunkPosMiddle;

    public ChunkContouring(Vector3d playerPosition, WorldContouring worldContouring, int x, int y, int z, StorageContainer dataStorage, StorageContainer lightStorage) {
        super(x, y, z, dataStorage, lightStorage);
        this.world = worldContouring;
        this.chunkPosMiddle = new Vector3d((double)(this.xVoxel + IChunk.CHUNK_SIZE_HALF), (double)(this.yVoxel + IChunk.CHUNK_SIZE_HALF), (double)(this.zVoxel + IChunk.CHUNK_SIZE_HALF));
        this.chunkRender = new ChunkRender();
        this.setLODVoxelsUpdated(false);
        this.chunkRender.voxelLevelOfDetail = this.calculateLevelOfDetail(playerPosition);
    }

    public void renderUpdate(WorldContouring world, Vector3d playerPosition, MultipleEvent updateMeshes) {
        if (!(this.voxelsUpdated && this.voxelLODUpdated && this.lightsUpdated || this.loadedNeighbourCount != 8)) {
            this.chunkRender.voxelLevelOfDetail = this.calculateLevelOfDetail(playerPosition);
            boolean onlyLightsUpdate = !this.lightsUpdated && this.voxelsUpdated && this.voxelLODUpdated;
            updateMeshes.addEvent(() -> this.createMesh(world, onlyLightsUpdate));
            this.voxelsUpdated = true;
            this.voxelLODUpdated = true;
            this.lightsUpdated = true;
        }
    }

    public void createMesh(WorldContouring world, boolean onlyLightsUpdate) {
        MultipleEvent multipleEvent = new MultipleEvent();
        OctreeNode octree = new OctreeNode(false);
        octree.reset(false);
        octree.size = IChunk.CHUNK_SIZE;
        octree.min = new Vector3i(this.x * IChunk.CHUNK_SIZE, this.y * IChunk.CHUNK_SIZE, this.z * IChunk.CHUNK_SIZE);
        if (this.isStorageBorderSameSign()) {
            this.chunkRender.setEdgeNodes(octree);
            return;
        }
        world.dualContouring.constructOctreeLinear(octree, this, this.getLevelOfDetail());
        List<Vertex> vertices = world.vertices;
        IntList indices = world.indices;
        this.chunkRender.setEdgeNodes(octree);
        world.dualContouring.contourMesh(octree, vertices, indices, false);
        if (indices.size() > 0) {
            ChunkLoadMeshEvent loadMesh = new ChunkLoadMeshEvent(world, this.x, this.y, this.z, vertices, indices, this.getLevelOfDetail(), false);
            multipleEvent.addEvent(loadMesh);
        } else {
            ChunkUnloadMeshEvent unloadMesh = new ChunkUnloadMeshEvent(world, this.x, this.y, this.z, false);
            multipleEvent.addEvent(unloadMesh);
        }
        vertices.clear();
        indices.clear();
        world.dualContouring.resetMemoryPool();
        world.seamUpdates.add(this);
        for (int i = -1; i <= 0; ++i) {
            for (int j = -1; j <= 0; ++j) {
                for (int k = -1; k <= 0; ++k) {
                    ChunkContouring c;
                    if (i == 0 && j == 0 && k == 0 || (c = (ChunkContouring)((WorldContouring)this.world).getChunk(this.x + i, this.y + j, this.z + k)) == null) continue;
                    world.seamUpdates.add(c);
                }
            }
        }
        world.queueEvent(multipleEvent);
    }

    public void updateSeamMesh(MultipleEvent multipleEvent, WorldContouring world) {
        if (this.chunkRender.edgeNodes.isEmpty()) {
            ChunkUnloadMeshEvent unloadMesh = new ChunkUnloadMeshEvent(world, this.x, this.y, this.z, true);
            multipleEvent.addEvent(unloadMesh);
            return;
        }
        Int2ObjectMap<OctreeNode> seamNodes = world.dualContouring.findSeamNodes(world, this);
        Int2ObjectMap<OctreeNode> parents = world.dualContouring.buildOctreeFromBottom(seamNodes, this.x, this.y, this.z, 1);
        OctreeNode seam = null;
        if (parents.size() > 0) {
            seam = (OctreeNode)parents.values().iterator().next();
            List<Vertex> vertices = world.vertices;
            IntList indices = world.indices;
            world.dualContouring.contourMesh(seam, vertices, indices, true);
            if (indices.size() > 0) {
                ChunkLoadMeshEvent loadMesh = new ChunkLoadMeshEvent(world, this.x, this.y, this.z, vertices, indices, this.getLevelOfDetail(), true);
                multipleEvent.addEvent(loadMesh);
            } else {
                seam = null;
                ChunkUnloadMeshEvent unloadMesh = new ChunkUnloadMeshEvent(world, this.x, this.y, this.z, true);
                multipleEvent.addEvent(unloadMesh);
            }
            vertices.clear();
            indices.clear();
        } else {
            ChunkUnloadMeshEvent unloadMesh = new ChunkUnloadMeshEvent(world, this.x, this.y, this.z, true);
            multipleEvent.addEvent(unloadMesh);
        }
        world.dualContouring.resetMemoryPool();
    }

    public void setTriangles(RawMesh mesh, int lod, boolean seam) {
        ((WorldContouring)this.world).removeChunkMesh(new Vector3i(this.x, this.y, this.z), seam);
        if (mesh != null) {
            ((WorldContouring)this.world).addChunkMesh(new Vector3i(this.x, this.y, this.z), mesh, lod, seam);
        }
    }

    public boolean needsUpdate() {
        boolean updateMesh = !this.voxelsUpdated || !this.voxelLODUpdated || !this.lightsUpdated;
        boolean updatedSuccesfully = true;
        if (updateMesh && this.loadedNeighbourCount != 8) {
            updatedSuccesfully = false;
        }
        return updatedSuccesfully;
    }

    public boolean needsUrgentUpdate() {
        return !this.voxelsUpdated;
    }

    public List<OctreeNode> getEdgeNodes() {
        return this.chunkRender.edgeNodes;
    }

    @Override
    public Vector3f calculateNormal(float x, float y, float z, float offset) {
        Vector3f normal = new Vector3f();
        if (offset > 1.0f) {
            int xn = Math.round(x - offset);
            int xp = Math.round(x + offset);
            int yn = Math.round(y - offset);
            int yp = Math.round(y + offset);
            int zn = Math.round(z - offset);
            int zp = Math.round(z + offset);
            float d000 = this.getData(xn, yn, zn);
            float d001 = this.getData(xn, yn, zp);
            float d010 = this.getData(xn, yp, zn);
            float d011 = this.getData(xn, yp, zp);
            float d100 = this.getData(xp, yn, zn);
            float d101 = this.getData(xp, yn, zp);
            float d110 = this.getData(xp, yp, zn);
            float d111 = this.getData(xp, yp, zp);
            normal.x = d000 + d001 + d010 + d011 - (d100 + d101 + d110 + d111);
            normal.y = d000 + d001 + d100 + d101 - (d111 + d110 + d011 + d010);
            normal.z = d000 + d010 + d100 + d110 - (d111 + d101 + d011 + d001);
        } else {
            normal.x = this.getDensity(x - offset, y, z) - this.getDensity(x + offset, y, z);
            normal.y = this.getDensity(x, y - offset, z) - this.getDensity(x, y + offset, z);
            normal.z = this.getDensity(x, y, z - offset) - this.getDensity(x, y, z + offset);
        }
        if (normal.x == 0.0f && normal.y == 0.0f && normal.z == 0.0f) {
            normal.x = this.getDensity(x - offset * 2.0f, y, z) - this.getDensity(x + offset * 2.0f, y, z);
            normal.y = this.getDensity(x, y - offset * 2.0f, z) - this.getDensity(x, y + offset * 2.0f, z);
            normal.z = this.getDensity(x, y, z - offset * 2.0f) - this.getDensity(x, y, z + offset * 2.0f);
            if (normal.x == 0.0f && normal.y == 0.0f && normal.z == 0.0f) {
                normal.x = 0.0f;
                normal.y = 1.0f;
                normal.z = 0.0f;
            }
        }
        normal.normalize();
        return normal;
    }

    public int calculateLevelOfDetail(Vector3d playerPosition) {
        float length = (float)this.chunkPosMiddle.distanceSquared((Vector3dc)playerPosition);
        float modifier = ConfigClient.snowLOD * ConfigClient.snowLOD;
        int levelOfDetail = 3;
        if (length <= LOD_2_DISTANCE * modifier) {
            levelOfDetail = 2;
        }
        if (length <= LOD_1_DISTANCE * modifier) {
            levelOfDetail = 1;
        }
        if (length <= LOD_0_DISTANCE * modifier) {
            levelOfDetail = 0;
        }
        return levelOfDetail;
    }

    public void checkLOD(Vector3d playerPosition) {
        if (this.calculateLevelOfDetail(playerPosition) != this.getLevelOfDetail()) {
            this.setLODVoxelsUpdated(false);
            this.chunkRender.voxelLevelOfDetail = this.calculateLevelOfDetail(playerPosition);
        }
    }

    public void setLODVoxelsUpdated(boolean voxelLODUpdated) {
        if (!voxelLODUpdated && this.voxelLODUpdated && this.loadedNeighbourCount == 8) {
            ((WorldContouring)this.world).needsVisualUpdate.add(this);
        }
        this.voxelLODUpdated = voxelLODUpdated;
    }

    public void setVoxelsUpdated(boolean voxelsUpdated) {
        if (!voxelsUpdated && this.voxelsUpdated && this.loadedNeighbourCount == 8) {
            ((WorldContouring)this.world).needsVisualUpdate.add(this);
        }
        this.voxelsUpdated = voxelsUpdated;
    }

    public int getLevelOfDetail() {
        return this.chunkRender.voxelLevelOfDetail;
    }

    @Override
    public void setLoadedNeighbourCount(int loadedNeighbourCount) {
        super.setLoadedNeighbourCount(loadedNeighbourCount);
        if (loadedNeighbourCount == 8) {
            ((WorldContouring)this.world).needsVisualUpdate.add(this);
        } else {
            ((WorldContouring)this.world).needsVisualUpdate.remove(this);
        }
    }

    @Override
    public void setData(int x, int y, int z, byte data) {
        if (!this.dataStorage.setAndCompareData(x, y, z, data)) {
            return;
        }
        this.setVoxelsUpdated(false);
        if (!this.activeNodes.isEmpty()) {
            for (Int2ObjectMap.Entry entry : this.activeNodes.int2ObjectEntrySet()) {
                int lod = entry.getIntKey();
                int off = 1 << lod;
                int offMask = off - 1;
                IntSet active = (IntSet)entry.getValue();
                if (active == null || lod != 0 && ((x & offMask) != 0 || (y & offMask) != 0 || (z & offMask) != 0)) continue;
                active.add(x << 16 | y << 8 | z);
                active.add((byte)(x - off) << 16 | y << 8 | z);
                active.add(x << 16 | (byte)(y - off) << 8 | z);
                active.add(x << 16 | y << 8 | (byte)(z - off));
                active.add((byte)(x - off) << 16 | (byte)(y - off) << 8 | z);
                active.add((byte)(x - off) << 16 | y << 8 | (byte)(z - off));
                active.add(x << 16 | (byte)(y - off) << 8 | (byte)(z - off));
                active.add((byte)(x - off) << 16 | (byte)(y - off) << 8 | (byte)(z - off));
            }
        }
        ChunkContouring neighbour = null;
        if (x == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, 0, 0)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
        if (y == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(0, -1, 0)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
        if (z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(0, 0, -1)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
        if (x == 0 && y == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, -1, 0)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
        if (x == 0 && z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, 0, -1)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
        if (y == 0 && z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(0, -1, -1)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
        if (x == 0 && y == 0 && z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, -1, -1)) != null) {
            neighbour.setVoxelsUpdated(false);
        }
    }

    @Override
    public void setLightData(int x, int y, int z, byte data) {
        if (!this.lightStorage.setAndCompareData(x, y, z, data)) {
            return;
        }
        this.setLightsUpdated(false);
        ChunkContouring neighbour = null;
        if (x == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, 0, 0)) != null) {
            neighbour.setLightsUpdated(false);
        }
        if (y == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(0, -1, 0)) != null) {
            neighbour.setLightsUpdated(false);
        }
        if (z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(0, 0, -1)) != null) {
            neighbour.setLightsUpdated(false);
        }
        if (x == 0 && y == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, -1, 0)) != null) {
            neighbour.setLightsUpdated(false);
        }
        if (x == 0 && z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, 0, -1)) != null) {
            neighbour.setLightsUpdated(false);
        }
        if (y == 0 && z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(0, -1, -1)) != null) {
            neighbour.setLightsUpdated(false);
        }
        if (x == 0 && y == 0 && z == 0 && (neighbour = (ChunkContouring)this.getNeighbourChunk(-1, -1, -1)) != null) {
            neighbour.setLightsUpdated(false);
        }
    }

    public void setLightDataFast(int x, int y, int z, byte data) {
        this.lightStorage.setData(x, y, z, data);
    }

    public boolean areVoxelsUpdated() {
        return this.voxelsUpdated;
    }

    public boolean isVoxelLODUpdated() {
        return this.voxelLODUpdated;
    }

    public void setLightsUpdated(boolean lightsUpdated) {
        if (!lightsUpdated && this.lightsUpdated && this.loadedNeighbourCount == 8) {
            ((WorldContouring)this.world).needsVisualUpdate.add(this);
        }
        this.lightsUpdated = lightsUpdated;
    }

    public boolean areLightsUpdated() {
        return this.lightsUpdated;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public boolean hasPriority() {
        return this.priority;
    }
}

