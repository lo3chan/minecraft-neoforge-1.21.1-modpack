/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
 *  net.minecraft.world.phys.Vec3
 *  org.joml.FrustumIntersection
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.math.Math;
import net.diebuddies.math.PerlinNoise;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.ChunkEntity;
import net.diebuddies.physics.snow.ChunkRenderUpdate;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.IWorld;
import net.diebuddies.physics.snow.SnowBatch;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.WorldUtil;
import net.diebuddies.physics.snow.contouring.DualContouring;
import net.diebuddies.physics.snow.contouring.Vertex;
import net.diebuddies.physics.snow.math.SDF;
import net.diebuddies.physics.snow.storage.StorageSimple;
import net.diebuddies.physics.snow.thread.MultipleEvent;
import net.minecraft.world.phys.Vec3;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class WorldContouring
extends IWorld<ChunkContouring>
implements Runnable {
    private static final int CONTOUR_STACK_SIZE = 8;
    private static final int LOD_UPDATE_EVERY_X_BLOCKS = 4;
    private ConcurrentLinkedQueue<Runnable> events;
    private FrustumIntersection frustum;
    private Vector3d playerPosition;
    private Vector3i lastPlayerPosition;
    private Matrix4f viewProjectionMatrix;
    public Set<ChunkContouring> needsVisualUpdate;
    public Set<ChunkContouring> seamUpdates;
    private Set<ChunkRenderUpdate> chunkRenderUpdates;
    private List<Runnable> tasks;
    private StorageSimple modulationLayer;
    private StorageSimple modulationLayerRaw;
    private SnowWorld snowWorld;
    private MultipleEvent updateMeshesEvent = new MultipleEvent();
    public final DualContouring dualContouring = new DualContouring();
    public List<Vertex> vertices = new ObjectArrayList();
    public IntList indices = new IntArrayList();
    private Thread thread;
    private volatile boolean shutdown;

    public WorldContouring(SnowWorld snowWorld, int minChunkY, int maxChunkY) {
        super(minChunkY, maxChunkY);
        this.events = new ConcurrentLinkedQueue();
        this.snowWorld = snowWorld;
        this.needsVisualUpdate = new ObjectOpenHashSet();
        this.seamUpdates = new ObjectOpenHashSet();
        this.chunkRenderUpdates = new ObjectOpenHashSet();
        this.tasks = new ObjectArrayList();
        Vec3 pos = snowWorld.getCameraTranslation();
        this.playerPosition = new Vector3d(pos.x * (double)IChunk.CHUNK_MULTIPLE, pos.y * (double)IChunk.CHUNK_MULTIPLE, pos.z * (double)IChunk.CHUNK_MULTIPLE);
        this.lastPlayerPosition = new Vector3i((int)(pos.x / 4.0), (int)(pos.y / 4.0), (int)(pos.z / 4.0));
        this.viewProjectionMatrix = new Matrix4f((Matrix4fc)snowWorld.getCameraViewProjectionMatrix());
        this.frustum = new FrustumIntersection((Matrix4fc)this.viewProjectionMatrix, true);
        this.thread = new Thread((Runnable)this, "Snow Contouring Thread");
        this.thread.setDaemon(true);
    }

    public void start() {
        this.thread.start();
    }

    public void join() {
        try {
            this.thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.createModulationLayer();
        while (!this.shutdown) {
            Runnable event = null;
            while ((event = this.events.poll()) != null) {
                event.run();
            }
            if (this.seamUpdates.size() > 0) {
                MultipleEvent seamEvents = new MultipleEvent();
                for (ChunkContouring chunk : this.seamUpdates) {
                    chunk.updateSeamMesh(seamEvents, this);
                }
                seamEvents.run();
                this.seamUpdates.clear();
            }
            int ix = (int)(this.playerPosition.x / 4.0);
            int iy = (int)(this.playerPosition.y / 4.0);
            int iz = (int)(this.playerPosition.z / 4.0);
            if (ix != this.lastPlayerPosition.x || iy != this.lastPlayerPosition.y || iz != this.lastPlayerPosition.z) {
                for (ChunkContouring chunk : this.loadedChunks.values()) {
                    chunk.checkLOD(this.playerPosition);
                }
                this.lastPlayerPosition.set(ix, iy, iz);
            }
            int chunkX = WorldUtil.calculateChunkPosX((int)this.playerPosition.x);
            int chunkZ = WorldUtil.calculateChunkPosX((int)this.playerPosition.z);
            this.updateChangedChunks(chunkX, chunkZ);
            for (Runnable runnable : this.tasks) {
                runnable.run();
            }
            this.tasks.clear();
            if (!this.updateMeshesEvent.isEmpty()) {
                this.snowWorld.queueEvent(this.updateMeshesEvent);
                this.updateMeshesEvent = new MultipleEvent();
            }
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createModulationLayer() {
        this.modulationLayer = new StorageSimple(0, IChunk.CHUNK_VOLUME);
        this.modulationLayerRaw = new StorageSimple(0, IChunk.CHUNK_VOLUME);
        int repeatableAfter = IChunk.CHUNK_SIZE;
        int tilesLarge = IChunk.CHUNK_SIZE / 16;
        double dividerLarge = (double)repeatableAfter / (double)tilesLarge;
        PerlinNoise perlinLarge = new PerlinNoise(new Random(0L), tilesLarge);
        int tilesSmall = IChunk.CHUNK_SIZE / 2;
        double dividerSmall = (double)repeatableAfter / (double)tilesSmall;
        PerlinNoise perlinSmall = new PerlinNoise(new Random(1L), tilesSmall);
        for (int x = 0; x < IChunk.CHUNK_SIZE; ++x) {
            for (int y = 0; y < IChunk.CHUNK_SIZE; ++y) {
                for (int z = 0; z < IChunk.CHUNK_SIZE; ++z) {
                    double noiseLarge = perlinLarge.noise((double)x / dividerLarge, (double)y / dividerLarge, (double)z / dividerLarge) * 0.5 + 0.5;
                    double noiseSmall = perlinSmall.noise((double)x / dividerSmall, (double)y / dividerSmall, (double)z / dividerSmall);
                    double finalNoise = noiseLarge * 3.5 + noiseSmall * 3.5;
                    byte rawData = Math.clamp((int)(finalNoise * 0.5 * 127.0), (byte)-107, (byte)107);
                    byte data = Math.clamp(-127 + rawData, (byte)-127, (byte)127);
                    this.modulationLayer.setData(x, y, z, data);
                    this.modulationLayerRaw.setData(x, y, z, rawData);
                }
            }
        }
    }

    @Override
    public void addChunk(ChunkContouring chunk) {
        chunk.modulationStorage = this.modulationLayer;
        super.addChunk(chunk);
    }

    private void updateChangedChunks(int chunkX, int chunkZ) {
        int placeSize = 8 - this.chunkRenderUpdates.size();
        float radius = new Vector3f((float)IChunk.CHUNK_SIZE / 2.0f + 3.0f).length() * IChunk.CHUNK_MULTIPLE_INV;
        ObjectRBTreeSet updates = new ObjectRBTreeSet((Comparator)new Comparator<ChunkRenderUpdate>(this){

            @Override
            public int compare(ChunkRenderUpdate o1, ChunkRenderUpdate o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });
        ObjectArrayList updateAnyways = new ObjectArrayList();
        double longestDistance = Double.MAX_VALUE;
        Vector3d position = new Vector3d((Vector3dc)this.playerPosition);
        Iterator<ChunkContouring> it = this.needsVisualUpdate.iterator();
        while (it.hasNext()) {
            ChunkContouring chunk = it.next();
            if (chunk.getWorld() == null || !chunk.needsUpdate()) {
                it.remove();
                continue;
            }
            if (chunk.needsUrgentUpdate()) {
                updateAnyways.add(new ChunkRenderUpdate(0.0, chunk));
                continue;
            }
            if (placeSize <= 0) continue;
            double distance = this.calculateChunkDistance(chunk, position, radius);
            int size = updates.size();
            if (!(distance < longestDistance) && size >= placeSize) continue;
            if (size > placeSize) {
                updates.remove(updates.last());
            }
            updates.add((Object)new ChunkRenderUpdate(distance, chunk));
            longestDistance = ((ChunkRenderUpdate)updates.last()).distance;
        }
        for (ChunkRenderUpdate cru : updates) {
            this.checkCRU(cru);
        }
        for (ChunkRenderUpdate cru : updateAnyways) {
            this.checkCRU(cru);
        }
        Iterator<ChunkRenderUpdate> chunkIt = this.chunkRenderUpdates.iterator();
        while (chunkIt.hasNext()) {
            ChunkRenderUpdate chunkRenderUpdate = chunkIt.next();
            if (chunkRenderUpdate.chunk.getWorld() == null) {
                chunkIt.remove();
                continue;
            }
            if (chunkRenderUpdate.chunk.hasPriority()) continue;
            if (chunkRenderUpdate.dataChanged) {
                this.tasks.add(0, chunkRenderUpdate.event);
                chunkIt.remove();
                continue;
            }
            if (this.tasks.size() > 8) continue;
            this.tasks.add(chunkRenderUpdate.event);
            chunkIt.remove();
        }
        chunkIt = this.chunkRenderUpdates.iterator();
        while (chunkIt.hasNext()) {
            ChunkRenderUpdate chunkRenderUpdate = chunkIt.next();
            if (chunkRenderUpdate.chunk.getWorld() == null) {
                chunkIt.remove();
                continue;
            }
            if (!chunkRenderUpdate.chunk.hasPriority()) continue;
            this.tasks.add(0, chunkRenderUpdate.event);
            chunkRenderUpdate.chunk.setPriority(false);
            chunkIt.remove();
        }
    }

    private void checkCRU(ChunkRenderUpdate cru) {
        boolean dataChanged = !cru.chunk.areVoxelsUpdated();
        MultipleEvent updateMeshes = new MultipleEvent();
        cru.chunk.renderUpdate(this, this.playerPosition, updateMeshes);
        if (!updateMeshes.isEmpty()) {
            cru.event = updateMeshes;
            cru.dataChanged = dataChanged;
            this.chunkRenderUpdates.add(cru);
        }
        this.needsVisualUpdate.remove(cru.chunk);
    }

    private double calculateChunkDistance(ChunkContouring chunk, Vector3d position, float radius) {
        double distance = position.distanceSquared((Vector3dc)chunk.chunkPosMiddle);
        if (this.frustum.testSphere((float)((double)(chunk.xVoxel + IChunk.CHUNK_SIZE_HALF) - position.x) * IChunk.CHUNK_MULTIPLE_INV, (float)((double)(chunk.yVoxel + IChunk.CHUNK_SIZE_HALF) - position.y) * IChunk.CHUNK_MULTIPLE_INV, (float)((double)(chunk.zVoxel + IChunk.CHUNK_SIZE_HALF) - position.z) * IChunk.CHUNK_MULTIPLE_INV, radius)) {
            distance *= 0.1;
        }
        return distance;
    }

    public void changeDensity(SDF sdf, byte strength, byte action) {
        int x = (int)sdf.getX();
        int y = (int)sdf.getY();
        int z = (int)sdf.getZ();
        int adjRadius = (int)java.lang.Math.ceil(sdf.getBounds()) + 2;
        for (int i = x - adjRadius; i < x + adjRadius; ++i) {
            for (int j = y - adjRadius; j < y + adjRadius; ++j) {
                for (int k = z - adjRadius; k < z + adjRadius; ++k) {
                    int voxelZ;
                    int voxelY;
                    ChunkContouring c = (ChunkContouring)this.getChunkWorldPos(i, j, k);
                    if (c == null) continue;
                    double distance = Math.clamp(sdf.getDistance(i, j, k), -1.0, 1.0);
                    byte calcData = (byte)((double)strength * distance);
                    int voxelX = WorldUtil.calculateVoxelPosX(i);
                    byte oldData = c.getDataByteFast(voxelX, voxelY = WorldUtil.calculateVoxelPosY(j), voxelZ = WorldUtil.calculateVoxelPosZ(k));
                    if (oldData <= calcData) continue;
                    if (sdf.hasPriority()) {
                        c.setPriority(true);
                    }
                    c.setData(voxelX, voxelY, voxelZ, calcData);
                }
            }
        }
    }

    public void uploadInformation() {
        this.frustum.set((Matrix4fc)this.viewProjectionMatrix.set((Matrix4fc)this.snowWorld.getCameraViewProjectionMatrix()), true);
        Vec3 pos = this.snowWorld.getCameraTranslation();
        this.playerPosition.set(pos.x * (double)IChunk.CHUNK_MULTIPLE, pos.y * (double)IChunk.CHUNK_MULTIPLE, pos.z * (double)IChunk.CHUNK_MULTIPLE);
    }

    @Override
    public ChunkContouring removeChunk(long index) {
        ChunkContouring chunk = (ChunkContouring)this.loadedChunks.get(index);
        if (chunk != null) {
            this.removeChunkMesh(new Vector3i(chunk.x, chunk.y, chunk.z), true);
            this.removeChunkMesh(new Vector3i(chunk.x, chunk.y, chunk.z), false);
        }
        return (ChunkContouring)super.removeChunk(index);
    }

    public void removeChunkMesh(Vector3i chunkPosition, boolean seam) {
        this.updateMeshesEvent.addEvent(() -> this.snowWorld.removeChunkEntity(chunkPosition.x, chunkPosition.y + (seam ? this.heightChunks : 0), chunkPosition.z));
    }

    public void addChunkMesh(Vector3i chunkPosition, RawMesh mesh, int lod, boolean seam) {
        Vector3i newPosition = new Vector3i((Vector3ic)chunkPosition);
        this.updateMeshesEvent.addEvent(() -> {
            ChunkEntity entity = new ChunkEntity();
            entity.position = newPosition;
            entity.batchPosition = new Vector3i(SnowBatch.shiftPos(newPosition.x), SnowBatch.shiftPos(newPosition.y), SnowBatch.shiftPos(newPosition.z));
            entity.vertexSegment = this.snowWorld.getSnowVertexData().uploadData(mesh.data);
            if (mesh.indexData != null && this.snowWorld.getSnowIndexData() != null) {
                entity.indexSegment = this.snowWorld.getSnowIndexData().uploadData(mesh.indexData);
            }
            entity.aabb = mesh.boundingBox;
            entity.center = new Vector3d((Vector3dc)entity.aabb.start).add((Vector3dc)entity.aabb.end).mul(0.5);
            entity.calculateTransformations();
            this.snowWorld.addChunkEntity(entity, chunkPosition.x, chunkPosition.y + (seam ? this.heightChunks : 0), chunkPosition.z);
            mesh.destroy();
        });
    }

    public StorageSimple getModulationLayer() {
        return this.modulationLayer;
    }

    public StorageSimple getModulationLayerRaw() {
        return this.modulationLayerRaw;
    }

    public void queueEvent(Runnable runnable) {
        this.events.add(runnable);
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public SnowWorld getSnowWorld() {
        return this.snowWorld;
    }

    public Vector3d getPlayerPosition() {
        return this.playerPosition;
    }
}

