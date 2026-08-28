/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 *  net.minecraft.util.Mth
 *  org.joml.Matrix4d
 *  org.joml.Vector2f
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.ocean.Dynamic2DArray;
import net.diebuddies.physics.ocean.Index;
import net.diebuddies.physics.ocean.OceanMesh;
import net.diebuddies.physics.ocean.OceanProcessor;
import net.diebuddies.physics.ocean.OceanSurface;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.physics.ocean.ProxyOceanStorage;
import net.diebuddies.physics.ocean.storage.FullStorageType2DBit;
import net.diebuddies.physics.ocean.storage.FullStorageType2DByte;
import net.diebuddies.physics.ocean.storage.FullStorageType2DShort;
import net.minecraft.util.Mth;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class OceanLayer {
    private static int counter;
    private int hashCode;
    private static final int CHUNK_GROUPING = 6;
    public static byte RANGE;
    public static byte RANGE_SOLID;
    public static float SCALE_COLOR_RANGE;
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_SIZE_BITS = 15;
    public static final int CHUNK_VOLUME = 256;
    public static final int CHUNK_SIZE_USED_BITS;
    public static final byte SOLID = 0;
    private OceanProcessor processor;
    private final ProxyOceanLayer proxyLayer;
    private final short layerPosY;
    private Long2ObjectMap<OceanStorage> oceanStorage;
    private int lastMinX = Integer.MAX_VALUE;
    private int lastMinZ = Integer.MAX_VALUE;
    private LongSet surfaceUpdatesNeeded;

    public static void updateRange(byte range) {
        RANGE = range;
        RANGE_SOLID = (byte)(RANGE + 1);
        SCALE_COLOR_RANGE = 256.0f / (float)RANGE;
    }

    public OceanLayer(OceanProcessor processor, short layerPosY) {
        this.hashCode = counter++;
        this.processor = processor;
        this.layerPosY = layerPosY;
        this.oceanStorage = new Long2ObjectOpenHashMap();
        this.proxyLayer = new ProxyOceanLayer(layerPosY);
        this.surfaceUpdatesNeeded = new LongOpenHashSet();
        processor.proxyEvents.add(() -> processor.getOceanWorld().getOceanLayers().put(layerPosY, (Object)this.proxyLayer));
    }

    @Nullable
    public OceanSurface generateMesh() {
        byte[] texture;
        if (this.oceanStorage.isEmpty()) {
            this.lastMinX = Integer.MAX_VALUE;
            this.lastMinZ = Integer.MAX_VALUE;
            this.surfaceUpdatesNeeded.clear();
            return new OceanSurface(this.proxyLayer, true);
        }
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (Long2ObjectMap.Entry entry : this.oceanStorage.long2ObjectEntrySet()) {
            long layerIndex = entry.getLongKey();
            OceanStorage storage = (OceanStorage)entry.getValue();
            if (storage.lights == null) {
                return null;
            }
            FullStorageType2DBit blocks = storage.blocks;
            int chunkX = Index.getXFromOceanLayer(layerIndex) * 16;
            int chunkZ = Index.getZFromOceanLayer(layerIndex) * 16;
            for (int xo = 0; xo < 16; ++xo) {
                for (int zo = 0; zo < 16; ++zo) {
                    int data = blocks.getData(xo, zo);
                    if (data == 0) continue;
                    int fx = chunkX + xo;
                    int fz = chunkZ + zo;
                    if (fx < minX) {
                        minX = fx;
                    }
                    if (fx > maxX) {
                        maxX = fx;
                    }
                    if (fz < minZ) {
                        minZ = fz;
                    }
                    if (fz <= maxZ) continue;
                    maxZ = fz;
                }
            }
        }
        if (minX == Integer.MAX_VALUE) {
            this.lastMinX = Integer.MAX_VALUE;
            this.lastMinZ = Integer.MAX_VALUE;
            this.surfaceUpdatesNeeded.clear();
            return new OceanSurface(this.proxyLayer, true);
        }
        int width = maxX - minX + 1;
        int height = maxZ - minZ + 1;
        Dynamic2DArray waves = this.processor.waves;
        Dynamic2DArray depth = this.processor.depth;
        waves.request(width, height);
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                int data = this.getOcean(x, z);
                if (data == 0) {
                    waves.set(x - minX, z - minZ, RANGE_SOLID);
                    continue;
                }
                waves.set(x - minX, z - minZ, (byte)0);
            }
        }
        depth.request(width, height);
        boolean allZeroDepth = true;
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                byte data = this.getMaxDepth(x, z);
                if (data > 1) {
                    allZeroDepth = false;
                }
                depth.set(x - minX, z - minZ, (byte)java.lang.Math.max(0, RANGE - java.lang.Math.max(0, data - 1)));
            }
        }
        int maxInfluence = 0;
        boolean createSingularLayer = true;
        if (allZeroDepth) {
            texture = new byte[1];
            waterCount = 0;
            for (x = 0; x < width; ++x) {
                for (z = 0; z < height; ++z) {
                    val = 255 - Math.clamp((int)((float)java.lang.Math.max(waves.get(x, z) + 1, depth.get(x, z) + 1) * SCALE_COLOR_RANGE), 0, 255);
                    if (val == RANGE_SOLID) continue;
                    ++waterCount;
                }
            }
            if (waterCount > 1024) {
                createSingularLayer = false;
            }
        } else {
            this.blur(waves, RANGE_SOLID);
            this.blur(depth, RANGE);
            texture = new byte[width * height];
            waterCount = 0;
            for (x = 0; x < width; ++x) {
                for (z = 0; z < height; ++z) {
                    val = 255 - Math.clamp((int)((float)java.lang.Math.max(waves.get(x, z) + 1, depth.get(x, z) + 1) * SCALE_COLOR_RANGE), 0, 255);
                    texture[z * width + x] = (byte)val;
                    maxInfluence = java.lang.Math.max(maxInfluence, val);
                    if (val == RANGE_SOLID) continue;
                    ++waterCount;
                }
            }
            if (waterCount > 1024) {
                createSingularLayer = false;
            }
        }
        if (this.lastMinX == Integer.MAX_VALUE) {
            this.lastMinX = minX;
            this.lastMinZ = minZ;
            int offsetX = minX;
            int offsetZ = minZ;
            this.processor.proxyEvents.add(() -> this.proxyLayer.setWaveOffset(offsetX, offsetZ));
        }
        int waveOffsetX = this.lastMinX - minX;
        int waveOffsetZ = this.lastMinZ - minZ;
        if (java.lang.Math.abs(waveOffsetX) > 36000 || java.lang.Math.abs(waveOffsetZ) > 36000) {
            this.lastMinX = minX;
            this.lastMinZ = minZ;
            waveOffsetX = this.lastMinX - minX;
            waveOffsetZ = this.lastMinZ - minZ;
        }
        OceanSurface surface = new OceanSurface(texture, width, height, waveOffsetX, waveOffsetZ, this.proxyLayer);
        if (createSingularLayer) {
            RawMesh mesh = this.generateMesh(waves, minX, minZ, 0, 0, width, height);
            Matrix4d transformation = new Matrix4d().translation((double)minX, (double)this.layerPosY, (double)minZ);
            OceanMesh oceanMesh = new OceanMesh(mesh, transformation, texture, (float)maxInfluence / 255.0f, width, height, waveOffsetX, waveOffsetZ, 0, 0);
            surface.singleMesh = true;
            return surface.addOceanMesh(0, this.layerPosY, 0, oceanMesh);
        }
        LongIterator it = this.surfaceUpdatesNeeded.iterator();
        while (it.hasNext()) {
            long index = it.nextLong();
            int chunkX = Index.getXFromOceanLayer(index);
            int chunkZ = Index.getZFromOceanLayer(index);
            int chunkSize = 96;
            int startX = chunkX * chunkSize - minX;
            int startZ = chunkZ * chunkSize - minZ;
            int endX = startX + chunkSize;
            int endZ = startZ + chunkSize;
            Matrix4d transformation = new Matrix4d().translation((double)minX, (double)this.layerPosY, (double)minZ);
            RawMesh mesh = this.generateMesh(waves, minX, minZ, startX, startZ, endX, endZ);
            int textureSize = chunkSize + 1;
            byte[] subTexture = null;
            if (mesh != null) {
                if (allZeroDepth) {
                    subTexture = new byte[1];
                } else {
                    subTexture = new byte[textureSize * textureSize];
                    for (int x = 0; x < textureSize; ++x) {
                        for (int z = 0; z < textureSize; ++z) {
                            int textureIndex = (startZ + z) * width + startX + x;
                            if (textureIndex < 0 || textureIndex >= texture.length) continue;
                            subTexture[z * textureSize + x] = texture[textureIndex];
                        }
                    }
                }
            }
            OceanMesh oceanMesh = new OceanMesh(mesh, transformation, subTexture, (float)maxInfluence / 255.0f, textureSize, textureSize, waveOffsetX, waveOffsetZ, startX, startZ);
            surface.addOceanMesh(chunkX, this.layerPosY, chunkZ, oceanMesh);
        }
        this.surfaceUpdatesNeeded.clear();
        return surface;
    }

    private RawMesh generateMesh(Dynamic2DArray waves, int offsetX, int offsetZ, int startX, int startZ, int endX, int endZ) {
        LongArrayList points = new LongArrayList();
        float size = 1.0f;
        startX = java.lang.Math.max(0, startX);
        startZ = java.lang.Math.max(0, startZ);
        endX = java.lang.Math.min(waves.getWidth(), endX);
        endZ = java.lang.Math.min(waves.getHeight(), endZ);
        for (int x = startX; x < endX; ++x) {
            for (int z = startZ; z < endZ; ++z) {
                if (x < 0 || z < 0 || x >= waves.getWidth() || z >= waves.getHeight() || waves.get(x, z) == RANGE_SOLID) continue;
                points.add((long)x << 32 | (long)z & 0xFFFFFFFFL);
            }
        }
        if (points.isEmpty()) {
            return null;
        }
        int positionSize = points.size() * 3 * 4 * 4;
        int colorSize = points.size() * 4 * 4;
        int uvSize = points.size() * 2 * 4 * 4;
        int lightSize = points.size() * 4 * 4;
        int vertexSize = positionSize + colorSize + uvSize + lightSize;
        RawMesh mesh = new RawMesh();
        ByteBuffer data = MemoryUtil.memAlloc((int)vertexSize);
        long pointer = MemoryUtil.memAddress((ByteBuffer)data);
        ByteBuffer indexData = MemoryUtil.memAlloc((int)(points.size() * 6 * 2));
        long indexPointer = MemoryUtil.memAddress((ByteBuffer)indexData);
        Vector4f waterUVs = this.processor.getWaterUVOffsets();
        Vector2f midCoord = this.processor.getWaterMidCoord();
        int pointsSize = points.size();
        int indexCount = 0;
        for (int i = 0; i < pointsSize; ++i) {
            long localIndex = points.getLong(i);
            int localX = (int)(localIndex >> 32);
            int localZ = (int)localIndex;
            int worldX = offsetX + localX;
            int worldZ = offsetZ + localZ;
            float height = this.processor.getHeight(worldX, this.layerPosY, worldZ);
            float north = this.processor.getHeight(worldX, this.layerPosY, worldZ - 1);
            float south = this.processor.getHeight(worldX, this.layerPosY, worldZ + 1);
            float east = this.processor.getHeight(worldX + 1, this.layerPosY, worldZ);
            float west = this.processor.getHeight(worldX - 1, this.layerPosY, worldZ);
            float h1 = this.processor.calculateAverageHeight(worldX + 1, this.layerPosY, worldZ - 1, height, north, east) - 0.001f;
            float h2 = this.processor.calculateAverageHeight(worldX - 1, this.layerPosY, worldZ - 1, height, north, west) - 0.001f;
            float h3 = this.processor.calculateAverageHeight(worldX + 1, this.layerPosY, worldZ + 1, height, south, east) - 0.001f;
            float h4 = this.processor.calculateAverageHeight(worldX - 1, this.layerPosY, worldZ + 1, height, south, west) - 0.001f;
            MemoryUtil.memPutFloat((long)pointer, (float)((float)localX + size));
            MemoryUtil.memPutFloat((long)(pointer + 4L), (float)h3);
            MemoryUtil.memPutFloat((long)(pointer + 8L), (float)((float)localZ + size));
            MemoryUtil.memPutFloat((long)(pointer + 12L), (float)waterUVs.y);
            MemoryUtil.memPutFloat((long)(pointer + 16L), (float)waterUVs.w);
            MemoryUtil.memPutInt((long)(pointer + 20L), (int)this.calculateLight(worldX, worldZ));
            MemoryUtil.memPutInt((long)(pointer + 24L), (int)this.calculateBiomeColor(worldX, worldZ));
            MemoryUtil.memPutFloat((long)(pointer += 28L), (float)((float)localX + size));
            MemoryUtil.memPutFloat((long)(pointer + 4L), (float)h1);
            MemoryUtil.memPutFloat((long)(pointer + 8L), (float)localZ);
            MemoryUtil.memPutFloat((long)(pointer + 12L), (float)waterUVs.y);
            MemoryUtil.memPutFloat((long)(pointer + 16L), (float)waterUVs.z);
            MemoryUtil.memPutInt((long)(pointer + 20L), (int)this.calculateLight(worldX, worldZ - 1));
            MemoryUtil.memPutInt((long)(pointer + 24L), (int)this.calculateBiomeColor(worldX, worldZ - 1));
            MemoryUtil.memPutFloat((long)(pointer += 28L), (float)localX);
            MemoryUtil.memPutFloat((long)(pointer + 4L), (float)h2);
            MemoryUtil.memPutFloat((long)(pointer + 8L), (float)localZ);
            MemoryUtil.memPutFloat((long)(pointer + 12L), (float)waterUVs.x);
            MemoryUtil.memPutFloat((long)(pointer + 16L), (float)waterUVs.z);
            MemoryUtil.memPutInt((long)(pointer + 20L), (int)this.calculateLight(worldX - 1, worldZ - 1));
            MemoryUtil.memPutInt((long)(pointer + 24L), (int)this.calculateBiomeColor(worldX - 1, worldZ - 1));
            MemoryUtil.memPutFloat((long)(pointer += 28L), (float)localX);
            MemoryUtil.memPutFloat((long)(pointer + 4L), (float)h4);
            MemoryUtil.memPutFloat((long)(pointer + 8L), (float)((float)localZ + size));
            MemoryUtil.memPutFloat((long)(pointer + 12L), (float)waterUVs.x);
            MemoryUtil.memPutFloat((long)(pointer + 16L), (float)waterUVs.w);
            MemoryUtil.memPutInt((long)(pointer + 20L), (int)this.calculateLight(worldX - 1, worldZ));
            MemoryUtil.memPutInt((long)(pointer + 24L), (int)this.calculateBiomeColor(worldX - 1, worldZ));
            pointer += 28L;
            MemoryUtil.memPutShort((long)indexPointer, (short)((short)indexCount));
            MemoryUtil.memPutShort((long)(indexPointer + 2L), (short)((short)(indexCount + 1)));
            MemoryUtil.memPutShort((long)(indexPointer + 4L), (short)((short)(indexCount + 2)));
            MemoryUtil.memPutShort((long)(indexPointer + 6L), (short)((short)(indexCount + 2)));
            MemoryUtil.memPutShort((long)(indexPointer + 8L), (short)((short)(indexCount + 3)));
            MemoryUtil.memPutShort((long)(indexPointer + 10L), (short)((short)indexCount));
            indexCount += 4;
            indexPointer += 12L;
        }
        mesh.data = data;
        mesh.indexData = indexData;
        return mesh;
    }

    private int calculateLight(int worldX, int worldZ) {
        int count = 0;
        int sky = 0;
        int block = 0;
        for (int x = 0; x <= 1; ++x) {
            int fx = worldX + x;
            for (int z = 0; z <= 1; ++z) {
                int fz = worldZ + z;
                OceanStorage storage = (OceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(fx, fz));
                if (storage == null || storage.blocks.getData(fx & 0xF, fz & 0xF) == 0) continue;
                byte lightData = storage.lights.getData(fx & 0xF, fz & 0xF);
                sky += lightData >> 4 & 0xF;
                block += lightData & 0xF;
                ++count;
            }
        }
        return (sky /= count) << 20 | (block /= count) << 4;
    }

    private int calculateBiomeColor(int worldX, int worldZ) {
        int count = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int x = 0; x <= 1; ++x) {
            int fx = worldX + x;
            for (int z = 0; z <= 1; ++z) {
                int fz = worldZ + z;
                int color = this.processor.getBiomeColor(fx, fz);
                if (color == 0) continue;
                r += color & 0xFF;
                g += (color & 0xFF00) >> 8;
                b += (color & 0xFF0000) >> 16;
                ++count;
            }
        }
        return (r /= count) | (g /= count) << 8 | (b /= count) << 16 | 0xFF000000;
    }

    public int getOcean(int x, int z) {
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
        if (storage == null) {
            return 0;
        }
        return storage.blocks.getData(x & 0xF, z & 0xF);
    }

    public byte getMaxDepth(int x, int z) {
        byte height;
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
        if (storage == null) {
            return 1;
        }
        short data = storage.depths.getData(x & 0xF, z & 0xF);
        byte depth = (byte)(data & 0xFFFF);
        return depth < (height = (byte)(data >> 8 & 0xFFFF)) ? depth : height;
    }

    private void blur(Dynamic2DArray arr, byte borderValue) {
        int widthMinusOne = arr.getWidth() - 1;
        int heightMinusOne = arr.getHeight() - 1;
        int borderValueMinusOne = borderValue - 1;
        for (int y = 0; y < arr.getHeight(); ++y) {
            int x;
            arr.set(widthMinusOne, y, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(widthMinusOne, y), borderValueMinusOne)));
            arr.set(0, y, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(0, y), borderValueMinusOne)));
            for (x = 0; x < widthMinusOne; ++x) {
                arr.set(x + 1, y, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(x + 1, y), arr.get(x, y) - 1)));
            }
            for (x = widthMinusOne; x >= 1; --x) {
                arr.set(x - 1, y, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(x - 1, y), arr.get(x, y) - 1)));
            }
        }
        for (int x = 0; x < arr.getWidth(); ++x) {
            int y;
            arr.set(x, heightMinusOne, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(x, heightMinusOne), borderValueMinusOne)));
            arr.set(x, 0, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(x, 0), borderValueMinusOne)));
            for (y = 0; y < heightMinusOne; ++y) {
                arr.set(x, y + 1, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(x, y + 1), arr.get(x, y) - 1)));
            }
            for (y = heightMinusOne; y >= 1; --y) {
                arr.set(x, y - 1, (byte)java.lang.Math.max(0, java.lang.Math.max(arr.get(x, y - 1), arr.get(x, y) - 1)));
            }
        }
    }

    public boolean remove(int chunkX, int chunkZ) {
        long index = Index.oceanLayerChunk(chunkX, chunkZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.remove(index);
        if (storage != null) {
            this.processor.layerUpdates.add((Object)this);
            this.causeOceanSurfaceUpdate(chunkX * 16, chunkZ * 16);
            this.causeOceanSurfaceUpdate(chunkX * 16 + 15, chunkZ * 16);
            this.causeOceanSurfaceUpdate(chunkX * 16 + 15, chunkZ * 16 + 15);
            this.causeOceanSurfaceUpdate(chunkX * 16, chunkZ * 16 + 15);
            this.processor.proxyEvents.add(() -> {
                this.proxyLayer.getOceanStorage().remove(index);
                if (this.proxyLayer.getOceanStorage().isEmpty()) {
                    this.processor.getOceanWorld().removeOceanLayer(this.layerPosY);
                }
            });
        }
        return this.oceanStorage.isEmpty();
    }

    public void clear() {
        this.oceanStorage.clear();
        this.processor.layerUpdates.add((Object)this);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public void setWaterAndDepthAndHeight(int worldX, int worldZ, short depthAndHeight) {
        long index = OceanLayer.oceanLayerChunkFromWorldPos(worldX, worldZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.computeIfAbsent(index, key -> new OceanStorage(key));
        int fx = worldX & 0xF;
        int fz = worldZ & 0xF;
        boolean changed = false;
        changed |= storage.blocks.setAndCompareData(fx, fz);
        if (changed |= storage.depths.setAndCompareData(fx, fz, depthAndHeight)) {
            this.processor.layerUpdates.add((Object)this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
            this.processor.proxyEvents.add(() -> {
                ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
                proxyStorage.blocks.setData(fx, fz);
                proxyStorage.depths.setData(fx, fz, depthAndHeight);
            });
        }
    }

    public void causeLayerUpdate(int worldX, int worldZ) {
        this.processor.layerUpdates.add((Object)this);
        this.causeOceanSurfaceUpdate(worldX, worldZ);
    }

    private void causeOceanSurfaceUpdate(int worldX, int worldZ) {
        int grouping = 6;
        int startX = Mth.floor((double)((double)OceanLayer.calculateChunkPosX(worldX - RANGE_SOLID) / (double)grouping));
        int endX = Mth.floor((double)((double)OceanLayer.calculateChunkPosX(worldX + RANGE_SOLID) / (double)grouping));
        int startZ = Mth.floor((double)((double)OceanLayer.calculateChunkPosZ(worldZ - RANGE_SOLID) / (double)grouping));
        int endZ = Mth.floor((double)((double)OceanLayer.calculateChunkPosZ(worldZ + RANGE_SOLID) / (double)grouping));
        for (int x = startX; x <= endX; ++x) {
            for (int z = startZ; z <= endZ; ++z) {
                this.surfaceUpdatesNeeded.add(Index.oceanLayerChunk(x, z));
            }
        }
    }

    public void unsetWater(int worldX, int worldZ) {
        int fz;
        int fx;
        boolean changed;
        long index = OceanLayer.oceanLayerChunkFromWorldPos(worldX, worldZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(index);
        if (storage != null && (changed = storage.blocks.unsetAndCompareData(fx = worldX & 0xF, fz = worldZ & 0xF))) {
            this.processor.layerUpdates.add((Object)this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
            this.processor.proxyEvents.add(() -> {
                ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
                proxyStorage.blocks.unsetData(fx, fz);
            });
        }
    }

    public boolean isWater(int worldX, int worldZ) {
        long index = OceanLayer.oceanLayerChunkFromWorldPos(worldX, worldZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(index);
        if (storage != null) {
            int fx = worldX & 0xF;
            int fz = worldZ & 0xF;
            return storage.blocks.getData(fx, fz) > 0;
        }
        return false;
    }

    public void updateDepthAndHeight(int worldX, short layerY, int worldZ) {
        short depth;
        boolean changed;
        int fz;
        int fx;
        long index = OceanLayer.oceanLayerChunkFromWorldPos(worldX, worldZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(index);
        if (storage != null && storage.blocks.getData(fx = worldX & 0xF, fz = worldZ & 0xF) != 0 && (changed = storage.depths.setAndCompareData(fx, fz, depth = this.processor.getWaterDepthAndHeight(worldX, layerY, worldZ)))) {
            this.processor.layerUpdates.add((Object)this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
            this.processor.proxyEvents.add(() -> {
                ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
                proxyStorage.depths.setData(fx, fz, depth);
            });
        }
    }

    public void setLight(int worldX, int worldZ, byte light) {
        boolean changed;
        long index = OceanLayer.oceanLayerChunkFromWorldPos(worldX, worldZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(index);
        if (storage != null && storage.lights != null && (changed = storage.lights.setAndCompareData(worldX & 0xF, worldZ & 0xF, light))) {
            this.processor.layerUpdates.add((Object)this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
        }
    }

    public void setLayerLight(int chunkX, int chunkZ, byte[] light) {
        long index = Index.oceanLayerChunk(chunkX, chunkZ);
        OceanStorage storage = (OceanStorage)this.oceanStorage.get(index);
        if (storage != null) {
            storage.lights = new FullStorageType2DByte(light);
        }
    }

    public static int calculateChunkPosX(int worldX) {
        return worldX >> CHUNK_SIZE_USED_BITS;
    }

    public static int calculateChunkPosZ(int worldZ) {
        return worldZ >> CHUNK_SIZE_USED_BITS;
    }

    public static long oceanLayerChunkFromWorldPos(int worldX, int worldZ) {
        return Index.oceanLayerChunk(OceanLayer.calculateChunkPosX(worldX), OceanLayer.calculateChunkPosZ(worldZ));
    }

    public short getLayerPosY() {
        return this.layerPosY;
    }

    static {
        RANGE = (byte)32;
        RANGE_SOLID = (byte)(RANGE + 1);
        SCALE_COLOR_RANGE = 256.0f / (float)RANGE;
        CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(15);
    }

    private class OceanStorage {
        public final int x;
        public final int z;
        public final int size;
        public final FullStorageType2DBit blocks;
        public final FullStorageType2DShort depths;
        @Nullable
        public FullStorageType2DByte lights;

        public OceanStorage(long index) {
            this.size = 256;
            this.x = Index.getXFromOceanLayer(index);
            this.z = Index.getZFromOceanLayer(index);
            this.blocks = new FullStorageType2DBit(this.size);
            this.depths = new FullStorageType2DShort(this.size);
            OceanLayer.this.processor.loadOceanBiomes(this.x, this.z);
            OceanLayer.this.processor.getOceanWorld().queueEvent(() -> OceanLayer.this.processor.getOceanWorld().loadOceanLayerLights(this.x, OceanLayer.this.layerPosY, this.z));
            OceanLayer.this.processor.proxyEvents.add(() -> {
                OceanLayer.this.proxyLayer.getOceanStorage().put(index, (Object)new ProxyOceanStorage(index));
                OceanLayer.this.processor.getOceanWorld().getOceanLayers().put(OceanLayer.this.layerPosY, (Object)OceanLayer.this.proxyLayer);
            });
        }
    }
}

