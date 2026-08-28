/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongComparator
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.chunk.RenderChunkRegion
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassDrawDispatcher;
import com.leonardoinc22.shortgrass.client.render.GrassGeometry;
import com.leonardoinc22.shortgrass.client.render.GrassSectionBuildBuffers;
import com.leonardoinc22.shortgrass.client.render.GrassSectionBuilder;
import com.leonardoinc22.shortgrass.client.render.GrassSectionMesh;
import com.leonardoinc22.shortgrass.client.render.VboHandleAccess;
import com.leonardoinc22.shortgrass.client.render.iris.GrassIrisBrightness;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

final class GrassSectionCache {
    private static final int MAX_VERTICAL_SECTIONS = 4;
    private static final int SUBMIT_BUDGET_PER_FRAME = 4;
    private static final long SUBMIT_TIME_BUDGET_NANOS = 1000000L;
    private static final long UPLOAD_TIME_BUDGET_NANOS = 2000000L;
    private static final int REFRESH_TICKS = 20;
    private static final int EMPTY_REFRESH_TICKS = 100;
    private static final int LIGHT_REFRESH_TICKS = 0;
    private static final int LIGHT_REFRESH_BUDGET_PER_FRAME = 4;
    private static final int LIGHT_REFRESH_MIN_INTERVAL_TICKS = 4;
    private static final double REFRESH_MOVE_THRESHOLD_SQR = 16.0;
    private static final Long2ObjectOpenHashMap<GrassSectionMesh> CACHE = new Long2ObjectOpenHashMap();
    private static final LongArrayList TO_BUILD = new LongArrayList();
    private static final LongArrayList TO_REFRESH = new LongArrayList();
    private static final LongArrayList TO_RELOD = new LongArrayList();
    private static final LongArrayList TO_LIGHT_REFRESH = new LongArrayList();
    private static final ConcurrentLinkedQueue<Long> PENDING_DIRTY_SECTIONS = new ConcurrentLinkedQueue();
    private static final ConcurrentLinkedQueue<Long> PENDING_LIGHT_DIRTY_SECTIONS = new ConcurrentLinkedQueue();
    private static final LongOpenHashSet DIRTY_SECTIONS = new LongOpenHashSet();
    private static final LongOpenHashSet LIGHT_DIRTY_SECTIONS = new LongOpenHashSet();
    private static final LongOpenHashSet RELOD_PENDING = new LongOpenHashSet();
    private static final int UPLOAD_BUDGET_PER_FRAME = 3;
    private static final int MAX_IN_FLIGHT = 3;
    private static final ExecutorService BUILD_WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "shortgrass-section-builder");
        t.setDaemon(true);
        t.setPriority(1);
        return t;
    });
    private static final ConcurrentLinkedQueue<PendingResult> RESULTS = new ConcurrentLinkedQueue();
    private static final LongOpenHashSet IN_FLIGHT = new LongOpenHashSet();
    private static int generation;
    private static double lastRefreshCamX;
    private static double lastRefreshCamZ;
    private static int lastLodRenderRadius;
    private static int lastEvictSx;
    private static int lastEvictSy;
    private static int lastEvictSz;
    private static int lastEvictRadius;
    private static int lastEvictVertical;

    private GrassSectionCache() {
    }

    static int verticalSectionRadius(int radiusSections) {
        return Math.min(radiusSections, 4);
    }

    static Long2ObjectOpenHashMap<GrassSectionMesh> meshes() {
        return CACHE;
    }

    static boolean inRange(long key, int camSx, int camSy, int camSz, int radiusSections, int verticalSections) {
        int dz;
        if (Math.abs(SectionPos.y((long)key) - camSy) > verticalSections) {
            return false;
        }
        int dx = SectionPos.x((long)key) - camSx;
        return dx * dx + (dz = SectionPos.z((long)key) - camSz) * dz <= radiusSections * radiusSections;
    }

    static void evictOutOfRange(int camSx, int camSy, int camSz, int radiusSections, int verticalSections) {
        if (camSx == lastEvictSx && camSy == lastEvictSy && camSz == lastEvictSz && radiusSections == lastEvictRadius && verticalSections == lastEvictVertical) {
            return;
        }
        lastEvictSx = camSx;
        lastEvictSy = camSy;
        lastEvictSz = camSz;
        lastEvictRadius = radiusSections;
        lastEvictVertical = verticalSections;
        ObjectIterator iterator = Long2ObjectMaps.fastIterator(CACHE);
        while (iterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iterator.next();
            if (GrassSectionCache.inRange(entry.getLongKey(), camSx, camSy, camSz, radiusSections, verticalSections)) continue;
            ((GrassSectionMesh)entry.getValue()).close();
            DIRTY_SECTIONS.remove(entry.getLongKey());
            LIGHT_DIRTY_SECTIONS.remove(entry.getLongKey());
            RELOD_PENDING.remove(entry.getLongKey());
            iterator.remove();
        }
    }

    static void invalidateBlock(ClientLevel level, ClientLevel cachedLevel, BlockPos pos) {
        if (level != cachedLevel) {
            return;
        }
        GrassSectionCache.markSectionDirty(SectionPos.asLong((BlockPos)pos));
        GrassSectionCache.markSectionDirty(SectionPos.asLong((int)pos.getX(), (int)(pos.getY() - 1), (int)pos.getZ()));
    }

    static void invalidateLightSection(ClientLevel level, ClientLevel cachedLevel, SectionPos sectionPos) {
        if (level != cachedLevel) {
            return;
        }
        GrassSectionCache.markSectionLightDirty(SectionPos.asLong((int)sectionPos.x(), (int)sectionPos.y(), (int)sectionPos.z()));
        GrassSectionCache.markSectionLightDirty(SectionPos.asLong((int)sectionPos.x(), (int)(sectionPos.y() - 1), (int)sectionPos.z()));
    }

    static void invalidateRenderSection(ClientLevel level, ClientLevel cachedLevel, int sectionX, int sectionY, int sectionZ) {
        if (level != cachedLevel) {
            return;
        }
        GrassSectionCache.markSectionLightDirty(SectionPos.asLong((int)sectionX, (int)sectionY, (int)sectionZ));
        GrassSectionCache.markSectionLightDirty(SectionPos.asLong((int)sectionX, (int)(sectionY - 1), (int)sectionZ));
    }

    static void invalidateChunk(ClientLevel level, ClientLevel cachedLevel, int chunkX, int chunkZ) {
        if (level != cachedLevel) {
            return;
        }
        for (int index = 0; index < level.getSectionsCount(); ++index) {
            GrassSectionCache.markSectionDirty(SectionPos.asLong((int)chunkX, (int)level.getSectionYFromSectionIndex(index), (int)chunkZ));
        }
    }

    private static void markSectionDirty(long key) {
        PENDING_DIRTY_SECTIONS.add(key);
    }

    private static void markSectionLightDirty(long key) {
        PENDING_LIGHT_DIRTY_SECTIONS.add(key);
    }

    static void drainPendingDirtySections() {
        Long key;
        while ((key = PENDING_DIRTY_SECTIONS.poll()) != null) {
            DIRTY_SECTIONS.add(key);
            LIGHT_DIRTY_SECTIONS.remove((Object)key);
        }
        while ((key = PENDING_LIGHT_DIRTY_SECTIONS.poll()) != null) {
            if (DIRTY_SECTIONS.contains((Object)key)) continue;
            LIGHT_DIRTY_SECTIONS.add(key);
        }
    }

    static void buildBudgeted(ClientLevel level, int camSx, int camSy, int camSz, int radiusSections, int verticalSections, Vec3 cameraPos, long now, boolean irisMode, boolean computeMode, TextureAtlasSprite bladeSprite, TextureAtlasSprite snowBladeSprite, Frustum frustum) {
        long key;
        GrassSectionCache.drainResults();
        TO_BUILD.clear();
        TO_REFRESH.clear();
        TO_RELOD.clear();
        TO_LIGHT_REFRESH.clear();
        boolean lodCheck = GrassSectionCache.shouldRecheckLod(cameraPos);
        int renderRadius = GrassConfig.renderRadius;
        for (int sx = camSx - radiusSections; sx <= camSx + radiusSections; ++sx) {
            for (int sz = camSz - radiusSections; sz <= camSz + radiusSections; ++sz) {
                for (int sy = camSy - verticalSections; sy <= camSy + verticalSections; ++sy) {
                    key = SectionPos.asLong((int)sx, (int)sy, (int)sz);
                    if (!GrassSectionCache.inRange(key, camSx, camSy, camSz, radiusSections, verticalSections)) continue;
                    GrassSectionMesh mesh = (GrassSectionMesh)CACHE.get(key);
                    if (IN_FLIGHT.contains(key)) continue;
                    if (mesh == null) {
                        TO_BUILD.add(key);
                        continue;
                    }
                    if (DIRTY_SECTIONS.contains(key)) {
                        TO_REFRESH.add(key);
                        continue;
                    }
                    if (lodCheck) {
                        if (GrassGeometry.lodTier(GrassSectionCache.horizontalSectionDistanceSqr(key, cameraPos), renderRadius) != mesh.lodTier) {
                            RELOD_PENDING.add(key);
                        } else {
                            RELOD_PENDING.remove(key);
                        }
                    }
                    if (RELOD_PENDING.contains(key) || mesh.anim == GrassSectionMesh.Anim.BAKED || !LIGHT_DIRTY_SECTIONS.contains(key) || now - mesh.builtAtTick < 0L || now - mesh.lastLightRefreshTick < 4L || frustum != null && !frustum.isVisible(mesh.bounds)) continue;
                    TO_LIGHT_REFRESH.add(key);
                }
            }
        }
        LongComparator byDistance = (a, b) -> Double.compare(GrassSectionCache.sectionDistanceSqr(a, cameraPos), GrassSectionCache.sectionDistanceSqr(b, cameraPos));
        TO_LIGHT_REFRESH.sort(byDistance);
        int lightBudget = 4;
        for (int li = 0; li < TO_LIGHT_REFRESH.size() && lightBudget > 0; ++li, --lightBudget) {
            key = TO_LIGHT_REFRESH.getLong(li);
            GrassSectionMesh existing = (GrassSectionMesh)CACHE.get(key);
            if (existing == null) {
                LIGHT_DIRTY_SECTIONS.remove(key);
                ++lightBudget;
                continue;
            }
            if (!GrassSectionCache.refreshSectionLighting(level, key, existing)) {
                DIRTY_SECTIONS.add(key);
            } else {
                existing.lastLightRefreshTick = now;
            }
            LIGHT_DIRTY_SECTIONS.remove(key);
        }
        LongIterator it = RELOD_PENDING.iterator();
        while (it.hasNext()) {
            long key2 = it.nextLong();
            if (IN_FLIGHT.contains(key2) || DIRTY_SECTIONS.contains(key2) || !CACHE.containsKey(key2)) continue;
            TO_RELOD.add(key2);
        }
        TO_BUILD.addAll((LongList)TO_REFRESH);
        TO_BUILD.addAll((LongList)TO_RELOD);
        TO_BUILD.sort(byDistance);
        RenderRegionCache regionCache = new RenderRegionCache();
        int budget = 4;
        long submitDeadline = System.nanoTime() + 1000000L;
        for (int bi = 0; bi < TO_BUILD.size() && budget > 0 && IN_FLIGHT.size() < 3; ++bi) {
            long key3 = TO_BUILD.getLong(bi);
            if (!CACHE.containsKey(key3) && frustum != null && !frustum.isVisible(GrassDrawDispatcher.sectionBounds(SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key3)), SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key3)), SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key3)))) || !GrassSectionCache.submitBuild(level, regionCache, key3, now, irisMode, computeMode, bladeSprite, snowBladeSprite, cameraPos)) continue;
            --budget;
            if (System.nanoTime() < submitDeadline) continue;
            ++bi;
            break;
        }
    }

    private static boolean submitBuild(ClientLevel level, RenderRegionCache regionCache, long key, long now, boolean irisMode, boolean computeMode, TextureAtlasSprite bladeSprite, TextureAtlasSprite snowBladeSprite, Vec3 cameraPos) {
        GrassSectionMesh mesh = GrassSectionBuilder.prepareMesh(key, now, computeMode, cameraPos);
        mesh.irisMode = irisMode;
        if (!GrassSectionBuilder.sectionMayHaveContent(level, key)) {
            GrassSectionMesh old = (GrassSectionMesh)CACHE.put(key, (Object)mesh);
            if (old != null) {
                old.close();
            }
            DIRTY_SECTIONS.remove(key);
            LIGHT_DIRTY_SECTIONS.remove(key);
            RELOD_PENDING.remove(key);
            return true;
        }
        RenderChunkRegion region = regionCache.createRegion((Level)level, SectionPos.of((long)key));
        if (region == null) {
            return false;
        }
        DIRTY_SECTIONS.remove(key);
        LIGHT_DIRTY_SECTIONS.remove(key);
        RELOD_PENDING.remove(key);
        IN_FLIGHT.add(key);
        int gen = generation;
        int tier = mesh.lodTier;
        BUILD_WORKER.execute(() -> {
            GrassSectionBuildBuffers buffers = new GrassSectionBuildBuffers(irisMode, computeMode, bladeSprite, snowBladeSprite);
            try {
                GrassSectionBuilder.emit(buffers, (BlockAndTintGetter)region, level, key, tier);
                buffers.buildCpu(mesh);
            }
            catch (Throwable t) {
                buffers.close();
                RESULTS.add(new PendingResult(key, null, null, gen));
                return;
            }
            RESULTS.add(new PendingResult(key, mesh, buffers, gen));
        });
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drainResults() {
        PendingResult result;
        int budget = 3;
        long uploadDeadline = System.nanoTime() + 2000000L;
        while (budget > 0 && (result = RESULTS.poll()) != null) {
            IN_FLIGHT.remove(result.key());
            if (result.generation() != generation || result.mesh() == null) {
                if (result.buffers() != null) {
                    result.buffers().close();
                }
                if (result.mesh() == null) continue;
                result.mesh().close();
                continue;
            }
            try {
                result.buffers().uploadInto(result.mesh());
            }
            finally {
                result.buffers().close();
            }
            GrassSectionMesh old = (GrassSectionMesh)CACHE.put(result.key(), (Object)result.mesh());
            if (old != null) {
                old.close();
            }
            --budget;
            if (System.nanoTime() < uploadDeadline) continue;
            break;
        }
    }

    private static boolean shouldRecheckLod(Vec3 cameraPos) {
        boolean moved;
        int renderRadius = GrassConfig.renderRadius;
        double dx = cameraPos.x - lastRefreshCamX;
        double dz = cameraPos.z - lastRefreshCamZ;
        boolean bl = moved = Double.isNaN(lastRefreshCamX) || dx * dx + dz * dz >= 16.0;
        if (!moved && renderRadius == lastLodRenderRadius) {
            return false;
        }
        lastRefreshCamX = cameraPos.x;
        lastRefreshCamZ = cameraPos.z;
        lastLodRenderRadius = renderRadius;
        return true;
    }

    private static double horizontalSectionDistanceSqr(long key, Vec3 cameraPos) {
        double dx = (double)(SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key)) + 8) - cameraPos.x;
        double dz = (double)(SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key)) + 8) - cameraPos.z;
        return dx * dx + dz * dz;
    }

    private static boolean refreshSectionLighting(ClientLevel level, long key, GrassSectionMesh mesh) {
        boolean plantChanged;
        boolean computeMesh = mesh.hasComputeBuffers();
        if (!GrassSectionCache.canPatchDrawnLight(mesh.buffer, mesh.bladeBufferAlt, mesh.vertexBytes, mesh.vertexStride, mesh.lightOffset, mesh.lightRuns, computeMesh) || !GrassSectionCache.canPatchDrawnLight(mesh.plantBuffer, mesh.plantBufferAlt, mesh.plantVertexBytes, mesh.plantVertexStride, mesh.plantLightOffset, mesh.plantLightRuns, computeMesh)) {
            return false;
        }
        int originX = SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key));
        int originY = SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key));
        int originZ = SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key));
        boolean bladeChanged = mesh.buffer != null && GrassSectionCache.patchLightRuns(level, originX, originY, originZ, mesh.vertexBytes, mesh.vertexStride, mesh.lightOffset, mesh.lightRuns, mesh.irisMode, false);
        boolean bl = plantChanged = mesh.plantBuffer != null && GrassSectionCache.patchLightRuns(level, originX, originY, originZ, mesh.plantVertexBytes, mesh.plantVertexStride, mesh.plantLightOffset, mesh.plantLightRuns, mesh.irisMode, true);
        if (bladeChanged) {
            GrassSectionCache.uploadVertexBytes(mesh.buffer, mesh.vertexBytes);
            if (computeMesh && mesh.bladeBufferAlt != null) {
                GrassSectionCache.uploadVertexBytes(mesh.bladeBufferAlt, mesh.vertexBytes);
            }
        }
        if (plantChanged) {
            GrassSectionCache.uploadVertexBytes(mesh.plantBuffer, mesh.plantVertexBytes);
            if (computeMesh && mesh.plantBufferAlt != null) {
                GrassSectionCache.uploadVertexBytes(mesh.plantBufferAlt, mesh.plantVertexBytes);
            }
        }
        return true;
    }

    private static boolean canPatchDrawnLight(VertexBuffer buffer, VertexBuffer alternateBuffer, ByteBuffer vertexBytes, int stride, int lightOffset, GrassSectionMesh.LightRun[] runs, boolean computeMesh) {
        boolean hasDrawnGeometry;
        boolean bl = hasDrawnGeometry = buffer != null || computeMesh && alternateBuffer != null;
        if (!hasDrawnGeometry) {
            return true;
        }
        if (buffer == null || VboHandleAccess.vboId(buffer) == 0) {
            return false;
        }
        if (computeMesh && (alternateBuffer == null || VboHandleAccess.vboId(alternateBuffer) == 0)) {
            return false;
        }
        return GrassSectionCache.hasLightPatchData(vertexBytes, stride, lightOffset, runs);
    }

    private static boolean hasLightPatchData(ByteBuffer vertexBytes, int stride, int lightOffset, GrassSectionMesh.LightRun[] runs) {
        if (vertexBytes == null || runs == null || runs.length == 0 || stride <= 0 || lightOffset < 0 || lightOffset + 4 > stride) {
            return false;
        }
        int limit = vertexBytes.limit();
        for (GrassSectionMesh.LightRun run : runs) {
            if (run.startVertex() < 0 || run.vertexCount() <= 0) {
                return false;
            }
            long lastLightByte = ((long)run.startVertex() + (long)run.vertexCount() - 1L) * (long)stride + (long)lightOffset + 4L;
            if (lastLightByte <= (long)limit) continue;
            return false;
        }
        return true;
    }

    private static boolean patchLightRuns(ClientLevel level, int originX, int originY, int originZ, ByteBuffer vertexBytes, int stride, int lightOffset, GrassSectionMesh.LightRun[] runs, boolean irisMode, boolean plants) {
        if (vertexBytes == null || runs.length == 0) {
            return false;
        }
        boolean changed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (GrassSectionMesh.LightRun run : runs) {
            int sample = run.sample();
            int light = LevelRenderer.getLightColor((BlockAndTintGetter)level, (BlockPos)pos.set(originX + GrassSectionCache.lightSampleX(sample), originY + GrassSectionCache.lightSampleY(sample), originZ + GrassSectionCache.lightSampleZ(sample)));
            if (irisMode) {
                light = GrassIrisBrightness.adjustLight(light, plants);
            }
            int endVertex = run.startVertex() + run.vertexCount();
            for (int vertex = run.startVertex(); vertex < endVertex; ++vertex) {
                int offset = vertex * stride + lightOffset;
                if (vertexBytes.getInt(offset) == light) continue;
                vertexBytes.putInt(offset, light);
                changed = true;
            }
        }
        return changed;
    }

    private static void uploadVertexBytes(VertexBuffer buffer, ByteBuffer vertexBytes) {
        RenderSystem.assertOnRenderThread();
        ByteBuffer uploadBytes = vertexBytes.duplicate().order(ByteOrder.nativeOrder());
        uploadBytes.position(0);
        uploadBytes.limit(vertexBytes.limit());
        GlStateManager._glBindBuffer((int)34962, (int)VboHandleAccess.vboId(buffer));
        RenderSystem.glBufferData((int)34962, (ByteBuffer)uploadBytes, (int)35048);
    }

    private static int packLightSample(int localX, int localY, int localZ) {
        return Mth.clamp((int)localY, (int)0, (int)31) << 8 | Mth.clamp((int)localZ, (int)0, (int)15) << 4 | Mth.clamp((int)localX, (int)0, (int)15);
    }

    private static int lightSampleX(int sample) {
        return sample & 0xF;
    }

    private static int lightSampleY(int sample) {
        return sample >> 8 & 0x1F;
    }

    private static int lightSampleZ(int sample) {
        return sample >> 4 & 0xF;
    }

    private static double sectionDistanceSqr(long key, Vec3 cameraPos) {
        double dx = (double)(SectionPos.sectionToBlockCoord((int)SectionPos.x((long)key)) + 8) - cameraPos.x;
        double dy = (double)(SectionPos.sectionToBlockCoord((int)SectionPos.y((long)key)) + 8) - cameraPos.y;
        double dz = (double)(SectionPos.sectionToBlockCoord((int)SectionPos.z((long)key)) + 8) - cameraPos.z;
        return dx * dx + dy * dy + dz * dz;
    }

    static void disposeAll() {
        PendingResult result;
        ++generation;
        IN_FLIGHT.clear();
        while ((result = RESULTS.poll()) != null) {
            if (result.buffers() != null) {
                result.buffers().close();
            }
            if (result.mesh() == null) continue;
            result.mesh().close();
        }
        for (GrassSectionMesh mesh : CACHE.values()) {
            mesh.close();
        }
        CACHE.clear();
        TO_BUILD.clear();
        TO_REFRESH.clear();
        TO_RELOD.clear();
        TO_LIGHT_REFRESH.clear();
        DIRTY_SECTIONS.clear();
        LIGHT_DIRTY_SECTIONS.clear();
        RELOD_PENDING.clear();
        PENDING_DIRTY_SECTIONS.clear();
        PENDING_LIGHT_DIRTY_SECTIONS.clear();
        lastEvictSx = Integer.MIN_VALUE;
        lastEvictSy = Integer.MIN_VALUE;
        lastEvictSz = Integer.MIN_VALUE;
        lastEvictRadius = -1;
        lastEvictVertical = -1;
    }

    static void resetRefreshTracking() {
        lastRefreshCamX = Double.NaN;
        lastRefreshCamZ = Double.NaN;
        lastLodRenderRadius = -1;
    }

    static {
        lastRefreshCamX = Double.NaN;
        lastRefreshCamZ = Double.NaN;
        lastLodRenderRadius = -1;
        lastEvictSx = Integer.MIN_VALUE;
        lastEvictSy = Integer.MIN_VALUE;
        lastEvictSz = Integer.MIN_VALUE;
        lastEvictRadius = -1;
        lastEvictVertical = -1;
    }

    private record PendingResult(long key, GrassSectionMesh mesh, GrassSectionBuildBuffers buffers, int generation) {
    }
}

