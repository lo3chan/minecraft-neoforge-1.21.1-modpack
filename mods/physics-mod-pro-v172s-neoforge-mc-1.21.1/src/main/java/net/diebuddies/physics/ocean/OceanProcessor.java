/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 *  net.minecraft.client.renderer.BiomeColors
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Plane
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.joml.Vector2f
 *  org.joml.Vector4f
 */
package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.physics.ocean.Dynamic2DArray;
import net.diebuddies.physics.ocean.IWorld;
import net.diebuddies.physics.ocean.Index;
import net.diebuddies.physics.ocean.OceanChunk;
import net.diebuddies.physics.ocean.OceanLayer;
import net.diebuddies.physics.ocean.OceanSurface;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.WorldUtil;
import net.diebuddies.physics.ocean.storage.EqualStorageType;
import net.diebuddies.physics.ocean.storage.FullStorageType2DInt;
import net.diebuddies.physics.ocean.storage.StorageContainer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class OceanProcessor
extends IWorld<OceanChunk>
implements Runnable {
    private ConcurrentLinkedQueue<Runnable> events;
    public final Dynamic2DArray waves = new Dynamic2DArray(50, 50);
    public final Dynamic2DArray depth = new Dynamic2DArray(50, 50);
    public final Long2ObjectMap<FullStorageType2DInt> loadedBiomeChunks = new Long2ObjectOpenHashMap(400);
    private Thread thread;
    private volatile boolean shutdown;
    private OceanWorld oceanWorld;
    private Short2ObjectMap<OceanLayer> oceanLayers;
    private Vector4f waterUVOffsets;
    private Vector2f waterMidCoord;
    protected LongSet processChunkColumns;
    public List<Runnable> proxyEvents;
    public ObjectSet<OceanLayer> layerUpdates;
    private float[] weights = new float[2];

    public OceanProcessor(OceanWorld oceanWorld, int minChunkY, int maxChunkY, Vector4f waterUVOffsets) {
        super(minChunkY, maxChunkY);
        this.oceanWorld = oceanWorld;
        this.layerUpdates = new ObjectOpenHashSet();
        this.processChunkColumns = new LongOpenHashSet();
        this.oceanLayers = new Short2ObjectOpenHashMap();
        this.events = new ConcurrentLinkedQueue();
        this.proxyEvents = new ObjectArrayList();
        this.waterUVOffsets = waterUVOffsets;
        this.waterMidCoord = new Vector2f(waterUVOffsets.x + waterUVOffsets.y, waterUVOffsets.z + waterUVOffsets.w).mul(0.5f);
        this.thread = new Thread((Runnable)this, "Ocean Processor Thread");
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
        while (!this.shutdown) {
            Runnable event = null;
            while ((event = this.events.poll()) != null) {
                event.run();
            }
            LongIterator it = this.processChunkColumns.iterator();
            while (it.hasNext()) {
                long index = it.nextLong();
                this.processChunkColumn(index);
            }
            this.processChunkColumns.clear();
            if (!this.layerUpdates.isEmpty()) {
                ObjectArrayList generatedSurfaces = new ObjectArrayList();
                ObjectIterator layerIterator = this.layerUpdates.iterator();
                while (layerIterator.hasNext()) {
                    OceanLayer oceanLayer = (OceanLayer)layerIterator.next();
                    OceanSurface oceanSurface = oceanLayer.generateMesh();
                    if (oceanSurface == null) continue;
                    generatedSurfaces.add(oceanSurface);
                    layerIterator.remove();
                }
                this.oceanWorld.queueEvent(() -> this.lambda$run$0((List)generatedSurfaces));
            }
            if (!this.proxyEvents.isEmpty()) {
                ObjectArrayList multipleEventsCopy = new ObjectArrayList(this.proxyEvents);
                this.oceanWorld.queueEvent(() -> OceanProcessor.lambda$run$1((List)multipleEventsCopy));
                this.proxyEvents.clear();
            }
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processChunkColumn(long index) {
        int z;
        int x = Index.getXFromOceanLayer(index);
        if (!this.areSurroundingsLoaded(x, 0, z = Index.getZFromOceanLayer(index))) {
            return;
        }
        int voxelX = x * 16;
        int voxelZ = z * 16;
        for (int y = this.minChunkY; y <= this.maxChunkY; ++y) {
            int fx;
            OceanChunk chunk = (OceanChunk)this.getChunk(x, y, z);
            int voxelY = y * 16;
            if (chunk == null) continue;
            StorageContainer dataStorage = chunk.dataStorage;
            if (dataStorage.getStorageType() instanceof EqualStorageType) {
                byte data = dataStorage.getData(0, 0, 0);
                if (data <= 0) continue;
                int fy = voxelY + 16 - 1;
                for (int xo = 0; xo < 16; ++xo) {
                    for (int zo = 0; zo < 16; ++zo) {
                        fx = voxelX + xo;
                        int fz = voxelZ + zo;
                        if (!this.isOceanSurface(data, fx, fy, fz)) continue;
                        this.setToSurface(data, fx, fy, fz);
                    }
                }
                continue;
            }
            for (int xo = 0; xo < 16; ++xo) {
                for (int yo = 0; yo < 16; ++yo) {
                    for (int zo = 0; zo < 16; ++zo) {
                        int fz;
                        int fy;
                        byte data = dataStorage.getData(xo, yo, zo);
                        if (!this.isOceanSurface(data, fx = voxelX + xo, fy = voxelY + yo, fz = voxelZ + zo)) continue;
                        this.setToSurface(data, fx, fy, fz);
                    }
                }
            }
        }
    }

    public void setToSurface(byte state, int x, int y, int z) {
        OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)y);
        if (oceanLayer == null) {
            oceanLayer = new OceanLayer(this, (short)y);
            this.oceanLayers.put((short)y, (Object)oceanLayer);
        }
        if (oceanLayer != null) {
            short depth = this.getWaterDepthAndHeight(x, y, z);
            oceanLayer.setWaterAndDepthAndHeight(x, z, depth);
        }
    }

    public void blockChanged(int x, int y, int z, byte previousState, byte state) {
        if (!this.areSurroundingsLoaded(WorldUtil.calculateChunkPosX(x), 0, WorldUtil.calculateChunkPosZ(z))) {
            return;
        }
        OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)y);
        if (oceanLayer == null && this.isOceanSurface(state, x, y, z)) {
            oceanLayer = new OceanLayer(this, (short)y);
            this.oceanLayers.put((short)y, (Object)oceanLayer);
        }
        if (oceanLayer != null) {
            short belowPos;
            OceanLayer belowOceanLayer;
            this.updateSurface(oceanLayer, state, x, y, z);
            this.updateSurface(oceanLayer, x + 1, y, z);
            this.updateSurface(oceanLayer, x - 1, y, z);
            this.updateSurface(oceanLayer, x, y, z + 1);
            this.updateSurface(oceanLayer, x, y, z - 1);
            if (previousState > 0 || state > 0) {
                boolean hasSurfaceNeighbour = false;
                for (int xo = -1; xo <= 1 && !hasSurfaceNeighbour; ++xo) {
                    for (int zo = -1; zo <= 1 && !hasSurfaceNeighbour; ++zo) {
                        if (xo == 0 && zo == 0) continue;
                        hasSurfaceNeighbour = oceanLayer.isWater(x + xo, z + zo);
                    }
                }
                if (hasSurfaceNeighbour) {
                    oceanLayer.causeLayerUpdate(x, z);
                }
            }
            if ((belowOceanLayer = (OceanLayer)this.oceanLayers.get(belowPos = (short)(y - 1))) == null) {
                if (this.isOceanSurface(x, belowPos, z)) {
                    belowOceanLayer = new OceanLayer(this, belowPos);
                    this.oceanLayers.put(belowPos, (Object)belowOceanLayer);
                }
            } else {
                this.updateSurface(belowOceanLayer, x, belowPos, z);
            }
        }
        for (Short2ObjectMap.Entry entry : this.oceanLayers.short2ObjectEntrySet()) {
            oceanLayer = (OceanLayer)entry.getValue();
            short layerY = entry.getShortKey();
            oceanLayer.updateDepthAndHeight(x, layerY, z);
        }
    }

    private void updateSurface(OceanLayer oceanLayer, byte state, int x, int y, int z) {
        if (this.isOceanSurface(state, x, y, z)) {
            short depth = this.getWaterDepthAndHeight(x, y, z);
            oceanLayer.setWaterAndDepthAndHeight(x, z, depth);
        } else {
            oceanLayer.unsetWater(x, z);
        }
    }

    private void updateSurface(OceanLayer oceanLayer, int x, int y, int z) {
        this.updateSurface(oceanLayer, this.getData(x, y, z), x, y, z);
    }

    private boolean isOceanSurface(byte state, int x, int y, int z) {
        return state > 0 && !this.isWater(x, y + 1, z) && !this.hasWaterFlow(x, y, z);
    }

    private boolean isOceanSurface(int x, int y, int z) {
        return this.isOceanSurface(this.getData(x, y, z), x, y, z);
    }

    public short getWaterDepthAndHeight(int x, int y, int z) {
        byte offset;
        byte height = OceanLayer.RANGE;
        byte depth = OceanLayer.RANGE;
        for (offset = 1; offset <= OceanLayer.RANGE; offset = (byte)(offset + 1)) {
            if (this.isAir(x, y + offset, z)) continue;
            height = offset;
            break;
        }
        for (offset = 1; offset <= OceanLayer.RANGE; offset = (byte)(offset + 1)) {
            if (this.isWater(x, y - offset, z)) continue;
            depth = offset;
            break;
        }
        return (short)(depth | height << 8);
    }

    @Override
    public void removeChunkColumn(int x, int z) {
        super.removeChunkColumn(x, z);
        long biomeIndex = Index.chunk(x, 0, z);
        this.loadedBiomeChunks.remove(biomeIndex);
        ObjectIterator it = this.oceanLayers.short2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            Short2ObjectMap.Entry entry = (Short2ObjectMap.Entry)it.next();
            OceanLayer oceanLayer = (OceanLayer)entry.getValue();
            boolean empty = oceanLayer.remove(x, z);
            if (!empty) continue;
            it.remove();
        }
    }

    @Override
    public void removeAll() {
        super.removeAll();
        this.loadedBiomeChunks.clear();
        for (OceanLayer layer : this.oceanLayers.values()) {
            layer.clear();
        }
        this.oceanLayers.clear();
        this.proxyEvents.add(() -> this.oceanWorld.clearOceanLayers());
    }

    public boolean isWater(int x, int y, int z) {
        byte data = this.getData(x, y, z);
        return data != -1 && data != 0;
    }

    public boolean isAir(int x, int y, int z) {
        return this.getData(x, y, z) == 0;
    }

    public boolean hasWaterFlow(int x, int y, int z) {
        int vx = 0;
        int vz = 0;
        byte currentState = this.getData(x, y, z);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int nz;
            int ny;
            int nx = x + direction.getStepX();
            byte neighbourState = this.getData(nx, ny = y + direction.getStepY(), nz = z + direction.getStepZ());
            if (!this.affectsFlow(neighbourState)) continue;
            int neighbourHeight = this.getOwnHeight(neighbourState);
            int magnitude = 0;
            if (neighbourHeight == 0) {
                byte belowNeighbourState = this.getData(nx, ny - 1, nz);
                boolean affectsFlow = this.affectsFlow(belowNeighbourState);
                neighbourHeight = this.getOwnHeight(belowNeighbourState);
                if (neighbourState > -1 && affectsFlow && neighbourHeight > 0) {
                    magnitude = this.getOwnHeight(currentState) - (neighbourHeight - 8);
                }
            } else if (neighbourHeight > 0) {
                magnitude = this.getOwnHeight(currentState) - neighbourHeight;
            }
            if (magnitude == 0) continue;
            vx += direction.getStepX() * magnitude;
            vz += direction.getStepZ() * magnitude;
        }
        return vx != 0 || vz != 0;
    }

    private boolean affectsFlow(byte neighbourState) {
        return neighbourState > 0;
    }

    private int getOwnHeight(byte neighbourState) {
        return Math.max(0, neighbourState);
    }

    public void updateLight(int worldX, int worldY, int worldZ, byte lightData) {
        OceanLayer oceanLayer;
        Object chunk;
        if ((chunk = this.getChunkWorldPos(worldX, --worldY, worldZ)) != null && (oceanLayer = (OceanLayer)this.oceanLayers.get((short)worldY)) != null) {
            oceanLayer.setLight(worldX, worldZ, lightData);
        }
    }

    public void updateLayerLight(int chunkX, short layerY, int chunkZ, byte[] lightData) {
        OceanLayer oceanLayer;
        Object chunk = this.getChunkWorldPos(chunkX * 16, layerY, chunkZ * 16);
        if (chunk != null && (oceanLayer = (OceanLayer)this.oceanLayers.get(layerY)) != null) {
            oceanLayer.setLayerLight(chunkX, chunkZ, lightData);
        }
    }

    public void updateBiome(int chunkX, int chunkZ, int[] colorData) {
        Object chunk = this.getChunkWorldPos(chunkX * 16, 0, chunkZ * 16);
        if (chunk != null) {
            long biomeIndex = Index.chunk(chunkX, 0, chunkZ);
            this.loadedBiomeChunks.put(biomeIndex, (Object)new FullStorageType2DInt(colorData));
        }
    }

    public float getHeight(int worldX, int worldY, int worldZ) {
        byte data = this.getData(worldX, worldY, worldZ);
        byte dataAbove = this.getData(worldX, worldY + 1, worldZ);
        if (data > 0) {
            if (dataAbove > 0) {
                return 1.0f;
            }
            return (float)data / 9.0f;
        }
        if (data == 0) {
            return 0.0f;
        }
        return -1.0f;
    }

    public float calculateAverageHeight(int worldX, int worldY, int worldZ, float height, float side1, float side2) {
        if (side2 >= 1.0f || side1 >= 1.0f) {
            return 1.0f;
        }
        this.weights[0] = 0.0f;
        this.weights[1] = 0.0f;
        if (side2 > 0.0f || side1 > 0.0f) {
            float currentHeight = this.getHeight(worldX, worldY, worldZ);
            if (currentHeight >= 1.0f) {
                return 1.0f;
            }
            this.addWeightedHeight(this.weights, currentHeight);
        }
        this.addWeightedHeight(this.weights, height);
        this.addWeightedHeight(this.weights, side2);
        this.addWeightedHeight(this.weights, side1);
        return this.weights[0] / this.weights[1];
    }

    private void addWeightedHeight(float[] weights, float height) {
        if (height >= 0.8f) {
            weights[0] = weights[0] + height * 10.0f;
            weights[1] = weights[1] + 10.0f;
        } else if (height >= 0.0f) {
            weights[0] = weights[0] + height;
            weights[1] = weights[1] + 1.0f;
        }
    }

    public void loadOceanBiomes(int chunkX, int chunkZ) {
        int[] colorData = new int[256];
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        BlockPos.MutableBlockPos tmp = new BlockPos.MutableBlockPos();
        for (int xo = 0; xo < 16; ++xo) {
            for (int zo = 0; zo < 16; ++zo) {
                tmp.set(worldX + xo, 0, worldZ + zo);
                int color = BiomeColors.getAverageWaterColor((BlockAndTintGetter)this.oceanWorld.getLevel(), (BlockPos)tmp);
                int flippedColor = 0xFF000000 | color & 0xFF00;
                flippedColor |= (color & 0xFF) << 16;
                colorData[zo * 16 + xo] = flippedColor |= (color & 0xFF0000) >> 16;
            }
        }
        this.updateBiome(chunkX, chunkZ, colorData);
    }

    public int getBiomeColor(int x, int z) {
        FullStorageType2DInt c = this.getBiomeChunkWorldPos(x, z);
        if (c != null) {
            return c.getData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosZ(z));
        }
        return 0;
    }

    public FullStorageType2DInt getBiomeChunkWorldPos(int x, int z) {
        int chunkX = WorldUtil.calculateChunkPosX(x);
        int chunkZ = WorldUtil.calculateChunkPosZ(z);
        return (FullStorageType2DInt)this.loadedBiomeChunks.get(Index.chunk(chunkX, 0, chunkZ));
    }

    public OceanWorld getOceanWorld() {
        return this.oceanWorld;
    }

    public Vector4f getWaterUVOffsets() {
        return this.waterUVOffsets;
    }

    public Vector2f getWaterMidCoord() {
        return this.waterMidCoord;
    }

    public Short2ObjectMap<OceanLayer> getOceanLayers() {
        return this.oceanLayers;
    }

    public void queueEvent(Runnable runnable) {
        this.events.add(runnable);
    }

    public void shutdown() {
        this.shutdown = true;
    }

    private static /* synthetic */ void lambda$run$1(List multipleEventsCopy) {
        for (Runnable task : multipleEventsCopy) {
            task.run();
        }
    }

    private /* synthetic */ void lambda$run$0(List generatedSurfaces) {
        this.oceanWorld.replaceOceanMeshes(generatedSurfaces);
    }
}

