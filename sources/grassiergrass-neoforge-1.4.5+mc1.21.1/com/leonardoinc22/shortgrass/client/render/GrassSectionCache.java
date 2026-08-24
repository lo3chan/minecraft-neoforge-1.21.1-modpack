package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.iris.GrassIrisBrightness;
import com.leonardoinc22.shortgrass.config.GrassConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
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
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
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
   private static final ConcurrentLinkedQueue<Long> PENDING_DIRTY_SECTIONS = new ConcurrentLinkedQueue<>();
   private static final ConcurrentLinkedQueue<Long> PENDING_LIGHT_DIRTY_SECTIONS = new ConcurrentLinkedQueue<>();
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
   private static final ConcurrentLinkedQueue<GrassSectionCache.PendingResult> RESULTS = new ConcurrentLinkedQueue<>();
   private static final LongOpenHashSet IN_FLIGHT = new LongOpenHashSet();
   private static int generation;
   private static double lastRefreshCamX = 0.0 / 0.0;
   private static double lastRefreshCamZ = 0.0 / 0.0;
   private static int lastLodRenderRadius = -1;
   private static int lastEvictSx = -2147483648;
   private static int lastEvictSy = -2147483648;
   private static int lastEvictSz = -2147483648;
   private static int lastEvictRadius = -1;
   private static int lastEvictVertical = -1;

   private GrassSectionCache() {
   }

   static int verticalSectionRadius(int radiusSections) {
      return Math.min(radiusSections, 4);
   }

   static Long2ObjectOpenHashMap<GrassSectionMesh> meshes() {
      return CACHE;
   }

   static boolean inRange(long key, int camSx, int camSy, int camSz, int radiusSections, int verticalSections) {
      if (Math.abs(SectionPos.y(key) - camSy) > verticalSections) {
         return false;
      } else {
         int dx = SectionPos.x(key) - camSx;
         int dz = SectionPos.z(key) - camSz;
         return dx * dx + dz * dz <= radiusSections * radiusSections;
      }
   }

   static void evictOutOfRange(int camSx, int camSy, int camSz, int radiusSections, int verticalSections) {
      if (camSx != lastEvictSx || camSy != lastEvictSy || camSz != lastEvictSz || radiusSections != lastEvictRadius || verticalSections != lastEvictVertical) {
         lastEvictSx = camSx;
         lastEvictSy = camSy;
         lastEvictSz = camSz;
         lastEvictRadius = radiusSections;
         lastEvictVertical = verticalSections;
         ObjectIterator<Entry<GrassSectionMesh>> iterator = Long2ObjectMaps.fastIterator(CACHE);

         while (iterator.hasNext()) {
            Entry<GrassSectionMesh> entry = (Entry<GrassSectionMesh>)iterator.next();
            if (!inRange(entry.getLongKey(), camSx, camSy, camSz, radiusSections, verticalSections)) {
               ((GrassSectionMesh)entry.getValue()).close();
               DIRTY_SECTIONS.remove(entry.getLongKey());
               LIGHT_DIRTY_SECTIONS.remove(entry.getLongKey());
               RELOD_PENDING.remove(entry.getLongKey());
               iterator.remove();
            }
         }
      }
   }

   static void invalidateBlock(ClientLevel level, ClientLevel cachedLevel, BlockPos pos) {
      if (level == cachedLevel) {
         markSectionDirty(SectionPos.asLong(pos));
         markSectionDirty(SectionPos.asLong(pos.getX(), pos.getY() - 1, pos.getZ()));
      }
   }

   static void invalidateLightSection(ClientLevel level, ClientLevel cachedLevel, SectionPos sectionPos) {
      if (level == cachedLevel) {
         markSectionLightDirty(SectionPos.asLong(sectionPos.x(), sectionPos.y(), sectionPos.z()));
         markSectionLightDirty(SectionPos.asLong(sectionPos.x(), sectionPos.y() - 1, sectionPos.z()));
      }
   }

   static void invalidateRenderSection(ClientLevel level, ClientLevel cachedLevel, int sectionX, int sectionY, int sectionZ) {
      if (level == cachedLevel) {
         markSectionLightDirty(SectionPos.asLong(sectionX, sectionY, sectionZ));
         markSectionLightDirty(SectionPos.asLong(sectionX, sectionY - 1, sectionZ));
      }
   }

   static void invalidateChunk(ClientLevel level, ClientLevel cachedLevel, int chunkX, int chunkZ) {
      if (level == cachedLevel) {
         for (int index = 0; index < level.getSectionsCount(); index++) {
            markSectionDirty(SectionPos.asLong(chunkX, level.getSectionYFromSectionIndex(index), chunkZ));
         }
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
         LIGHT_DIRTY_SECTIONS.remove(key);
      }

      while ((key = PENDING_LIGHT_DIRTY_SECTIONS.poll()) != null) {
         if (!DIRTY_SECTIONS.contains(key)) {
            LIGHT_DIRTY_SECTIONS.add(key);
         }
      }
   }

   static void buildBudgeted(
      ClientLevel level,
      int camSx,
      int camSy,
      int camSz,
      int radiusSections,
      int verticalSections,
      Vec3 cameraPos,
      long now,
      boolean irisMode,
      boolean computeMode,
      TextureAtlasSprite bladeSprite,
      TextureAtlasSprite snowBladeSprite,
      Frustum frustum
   ) {
      drainResults();
      TO_BUILD.clear();
      TO_REFRESH.clear();
      TO_RELOD.clear();
      TO_LIGHT_REFRESH.clear();
      boolean lodCheck = shouldRecheckLod(cameraPos);
      int renderRadius = GrassConfig.renderRadius;

      for (int sx = camSx - radiusSections; sx <= camSx + radiusSections; sx++) {
         for (int sz = camSz - radiusSections; sz <= camSz + radiusSections; sz++) {
            for (int sy = camSy - verticalSections; sy <= camSy + verticalSections; sy++) {
               long key = SectionPos.asLong(sx, sy, sz);
               if (inRange(key, camSx, camSy, camSz, radiusSections, verticalSections)) {
                  GrassSectionMesh mesh = (GrassSectionMesh)CACHE.get(key);
                  if (!IN_FLIGHT.contains(key)) {
                     if (mesh == null) {
                        TO_BUILD.add(key);
                     } else if (DIRTY_SECTIONS.contains(key)) {
                        TO_REFRESH.add(key);
                     } else {
                        if (lodCheck) {
                           if (GrassGeometry.lodTier(horizontalSectionDistanceSqr(key, cameraPos), renderRadius) != mesh.lodTier) {
                              RELOD_PENDING.add(key);
                           } else {
                              RELOD_PENDING.remove(key);
                           }
                        }

                        if (!RELOD_PENDING.contains(key)
                           && mesh.anim != GrassSectionMesh.Anim.BAKED
                           && LIGHT_DIRTY_SECTIONS.contains(key)
                           && now - mesh.builtAtTick >= 0L
                           && now - mesh.lastLightRefreshTick >= 4L
                           && (frustum == null || frustum.isVisible(mesh.bounds))) {
                           TO_LIGHT_REFRESH.add(key);
                        }
                     }
                  }
               }
            }
         }
      }

      LongComparator byDistance = (a, b) -> Double.compare(sectionDistanceSqr(a, cameraPos), sectionDistanceSqr(b, cameraPos));
      TO_LIGHT_REFRESH.sort(byDistance);
      int lightBudget = 4;

      for (int li = 0; li < TO_LIGHT_REFRESH.size() && lightBudget > 0; lightBudget--) {
         long key = TO_LIGHT_REFRESH.getLong(li);
         GrassSectionMesh existing = (GrassSectionMesh)CACHE.get(key);
         if (existing == null) {
            LIGHT_DIRTY_SECTIONS.remove(key);
            lightBudget++;
         } else {
            if (!refreshSectionLighting(level, key, existing)) {
               DIRTY_SECTIONS.add(key);
            } else {
               existing.lastLightRefreshTick = now;
            }

            LIGHT_DIRTY_SECTIONS.remove(key);
         }

         li++;
      }

      LongIterator it = RELOD_PENDING.iterator();

      while (it.hasNext()) {
         long key = it.nextLong();
         if (!IN_FLIGHT.contains(key) && !DIRTY_SECTIONS.contains(key) && CACHE.containsKey(key)) {
            TO_RELOD.add(key);
         }
      }

      TO_BUILD.addAll(TO_REFRESH);
      TO_BUILD.addAll(TO_RELOD);
      TO_BUILD.sort(byDistance);
      RenderRegionCache regionCache = new RenderRegionCache();
      int budget = 4;
      long submitDeadline = System.nanoTime() + 1000000L;

      for (int bi = 0; bi < TO_BUILD.size() && budget > 0 && IN_FLIGHT.size() < 3; bi++) {
         long key = TO_BUILD.getLong(bi);
         if ((
               CACHE.containsKey(key)
                  || frustum == null
                  || frustum.isVisible(
                     GrassDrawDispatcher.sectionBounds(
                        SectionPos.sectionToBlockCoord(SectionPos.x(key)),
                        SectionPos.sectionToBlockCoord(SectionPos.y(key)),
                        SectionPos.sectionToBlockCoord(SectionPos.z(key))
                     )
                  )
            )
            && submitBuild(level, regionCache, key, now, irisMode, computeMode, bladeSprite, snowBladeSprite, cameraPos)) {
            budget--;
            if (System.nanoTime() >= submitDeadline) {
               bi++;
               break;
            }
         }
      }
   }

   private static boolean submitBuild(
      ClientLevel level,
      RenderRegionCache regionCache,
      long key,
      long now,
      boolean irisMode,
      boolean computeMode,
      TextureAtlasSprite bladeSprite,
      TextureAtlasSprite snowBladeSprite,
      Vec3 cameraPos
   ) {
      GrassSectionMesh mesh = GrassSectionBuilder.prepareMesh(key, now, computeMode, cameraPos);
      mesh.irisMode = irisMode;
      if (!GrassSectionBuilder.sectionMayHaveContent(level, key)) {
         GrassSectionMesh old = (GrassSectionMesh)CACHE.put(key, mesh);
         if (old != null) {
            old.close();
         }

         DIRTY_SECTIONS.remove(key);
         LIGHT_DIRTY_SECTIONS.remove(key);
         RELOD_PENDING.remove(key);
         return true;
      } else {
         RenderChunkRegion region = regionCache.createRegion(level, SectionPos.of(key));
         if (region == null) {
            return false;
         } else {
            DIRTY_SECTIONS.remove(key);
            LIGHT_DIRTY_SECTIONS.remove(key);
            RELOD_PENDING.remove(key);
            IN_FLIGHT.add(key);
            int gen = generation;
            int tier = mesh.lodTier;
            BUILD_WORKER.execute(() -> {
               GrassSectionBuildBuffers buffers = new GrassSectionBuildBuffers(irisMode, computeMode, bladeSprite, snowBladeSprite);

               try {
                  GrassSectionBuilder.emit(buffers, region, level, key, tier);
                  buffers.buildCpu(mesh);
               } catch (Throwable var13x) {
                  buffers.close();
                  RESULTS.add(new GrassSectionCache.PendingResult(key, null, null, gen));
                  return;
               }

               RESULTS.add(new GrassSectionCache.PendingResult(key, mesh, buffers, gen));
            });
            return true;
         }
      }
   }

   private static void drainResults() {
      int budget = 3;
      long uploadDeadline = System.nanoTime() + 2000000L;

      GrassSectionCache.PendingResult result;
      while (budget > 0 && (result = RESULTS.poll()) != null) {
         IN_FLIGHT.remove(result.key());
         if (result.generation() == generation && result.mesh() != null) {
            try {
               result.buffers().uploadInto(result.mesh());
            } finally {
               result.buffers().close();
            }

            GrassSectionMesh old = (GrassSectionMesh)CACHE.put(result.key(), result.mesh());
            if (old != null) {
               old.close();
            }

            budget--;
            if (System.nanoTime() >= uploadDeadline) {
               break;
            }
         } else {
            if (result.buffers() != null) {
               result.buffers().close();
            }

            if (result.mesh() != null) {
               result.mesh().close();
            }
         }
      }
   }

   private static boolean shouldRecheckLod(Vec3 cameraPos) {
      int renderRadius = GrassConfig.renderRadius;
      double dx = cameraPos.x - lastRefreshCamX;
      double dz = cameraPos.z - lastRefreshCamZ;
      boolean moved = Double.isNaN(lastRefreshCamX) || dx * dx + dz * dz >= 16.0;
      if (!moved && renderRadius == lastLodRenderRadius) {
         return false;
      } else {
         lastRefreshCamX = cameraPos.x;
         lastRefreshCamZ = cameraPos.z;
         lastLodRenderRadius = renderRadius;
         return true;
      }
   }

   private static double horizontalSectionDistanceSqr(long key, Vec3 cameraPos) {
      double dx = SectionPos.sectionToBlockCoord(SectionPos.x(key)) + 8 - cameraPos.x;
      double dz = SectionPos.sectionToBlockCoord(SectionPos.z(key)) + 8 - cameraPos.z;
      return dx * dx + dz * dz;
   }

   private static boolean refreshSectionLighting(ClientLevel level, long key, GrassSectionMesh mesh) {
      boolean computeMesh = mesh.hasComputeBuffers();
      if (canPatchDrawnLight(mesh.buffer, mesh.bladeBufferAlt, mesh.vertexBytes, mesh.vertexStride, mesh.lightOffset, mesh.lightRuns, computeMesh)
         && canPatchDrawnLight(
            mesh.plantBuffer, mesh.plantBufferAlt, mesh.plantVertexBytes, mesh.plantVertexStride, mesh.plantLightOffset, mesh.plantLightRuns, computeMesh
         )) {
         int originX = SectionPos.sectionToBlockCoord(SectionPos.x(key));
         int originY = SectionPos.sectionToBlockCoord(SectionPos.y(key));
         int originZ = SectionPos.sectionToBlockCoord(SectionPos.z(key));
         boolean bladeChanged = mesh.buffer != null
            && patchLightRuns(level, originX, originY, originZ, mesh.vertexBytes, mesh.vertexStride, mesh.lightOffset, mesh.lightRuns, mesh.irisMode, false);
         boolean plantChanged = mesh.plantBuffer != null
            && patchLightRuns(
               level, originX, originY, originZ, mesh.plantVertexBytes, mesh.plantVertexStride, mesh.plantLightOffset, mesh.plantLightRuns, mesh.irisMode, true
            );
         if (bladeChanged) {
            uploadVertexBytes(mesh.buffer, mesh.vertexBytes);
            if (computeMesh && mesh.bladeBufferAlt != null) {
               uploadVertexBytes(mesh.bladeBufferAlt, mesh.vertexBytes);
            }
         }

         if (plantChanged) {
            uploadVertexBytes(mesh.plantBuffer, mesh.plantVertexBytes);
            if (computeMesh && mesh.plantBufferAlt != null) {
               uploadVertexBytes(mesh.plantBufferAlt, mesh.plantVertexBytes);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean canPatchDrawnLight(
      VertexBuffer buffer,
      VertexBuffer alternateBuffer,
      ByteBuffer vertexBytes,
      int stride,
      int lightOffset,
      GrassSectionMesh.LightRun[] runs,
      boolean computeMesh
   ) {
      boolean hasDrawnGeometry = buffer != null || computeMesh && alternateBuffer != null;
      if (!hasDrawnGeometry) {
         return true;
      } else if (buffer != null && VboHandleAccess.vboId(buffer) != 0) {
         return !computeMesh || alternateBuffer != null && VboHandleAccess.vboId(alternateBuffer) != 0
            ? hasLightPatchData(vertexBytes, stride, lightOffset, runs)
            : false;
      } else {
         return false;
      }
   }

   private static boolean hasLightPatchData(ByteBuffer vertexBytes, int stride, int lightOffset, GrassSectionMesh.LightRun[] runs) {
      if (vertexBytes != null && runs != null && runs.length != 0 && stride > 0 && lightOffset >= 0 && lightOffset + 4 <= stride) {
         int limit = vertexBytes.limit();

         for (GrassSectionMesh.LightRun run : runs) {
            if (run.startVertex() < 0 || run.vertexCount() <= 0) {
               return false;
            }

            long lastLightByte = ((long)run.startVertex() + run.vertexCount() - 1L) * stride + lightOffset + 4L;
            if (lastLightByte > limit) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean patchLightRuns(
      ClientLevel level,
      int originX,
      int originY,
      int originZ,
      ByteBuffer vertexBytes,
      int stride,
      int lightOffset,
      GrassSectionMesh.LightRun[] runs,
      boolean irisMode,
      boolean plants
   ) {
      if (vertexBytes != null && runs.length != 0) {
         boolean changed = false;
         MutableBlockPos pos = new MutableBlockPos();

         for (GrassSectionMesh.LightRun run : runs) {
            int sample = run.sample();
            int light = LevelRenderer.getLightColor(
               level, pos.set(originX + lightSampleX(sample), originY + lightSampleY(sample), originZ + lightSampleZ(sample))
            );
            if (irisMode) {
               light = GrassIrisBrightness.adjustLight(light, plants);
            }

            int endVertex = run.startVertex() + run.vertexCount();

            for (int vertex = run.startVertex(); vertex < endVertex; vertex++) {
               int offset = vertex * stride + lightOffset;
               if (vertexBytes.getInt(offset) != light) {
                  vertexBytes.putInt(offset, light);
                  changed = true;
               }
            }
         }

         return changed;
      } else {
         return false;
      }
   }

   private static void uploadVertexBytes(VertexBuffer buffer, ByteBuffer vertexBytes) {
      RenderSystem.assertOnRenderThread();
      ByteBuffer uploadBytes = vertexBytes.duplicate().order(ByteOrder.nativeOrder());
      uploadBytes.position(0);
      uploadBytes.limit(vertexBytes.limit());
      GlStateManager._glBindBuffer(34962, VboHandleAccess.vboId(buffer));
      RenderSystem.glBufferData(34962, uploadBytes, 35048);
   }

   private static int packLightSample(int localX, int localY, int localZ) {
      return Mth.clamp(localY, 0, 31) << 8 | Mth.clamp(localZ, 0, 15) << 4 | Mth.clamp(localX, 0, 15);
   }

   private static int lightSampleX(int sample) {
      return sample & 15;
   }

   private static int lightSampleY(int sample) {
      return sample >> 8 & 31;
   }

   private static int lightSampleZ(int sample) {
      return sample >> 4 & 15;
   }

   private static double sectionDistanceSqr(long key, Vec3 cameraPos) {
      double dx = SectionPos.sectionToBlockCoord(SectionPos.x(key)) + 8 - cameraPos.x;
      double dy = SectionPos.sectionToBlockCoord(SectionPos.y(key)) + 8 - cameraPos.y;
      double dz = SectionPos.sectionToBlockCoord(SectionPos.z(key)) + 8 - cameraPos.z;
      return dx * dx + dy * dy + dz * dz;
   }

   static void disposeAll() {
      generation++;
      IN_FLIGHT.clear();

      GrassSectionCache.PendingResult result;
      while ((result = RESULTS.poll()) != null) {
         if (result.buffers() != null) {
            result.buffers().close();
         }

         if (result.mesh() != null) {
            result.mesh().close();
         }
      }

      ObjectIterator var1 = CACHE.values().iterator();

      while (var1.hasNext()) {
         GrassSectionMesh mesh = (GrassSectionMesh)var1.next();
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
      lastEvictSx = -2147483648;
      lastEvictSy = -2147483648;
      lastEvictSz = -2147483648;
      lastEvictRadius = -1;
      lastEvictVertical = -1;
   }

   static void resetRefreshTracking() {
      lastRefreshCamX = 0.0 / 0.0;
      lastRefreshCamZ = 0.0 / 0.0;
      lastLodRenderRadius = -1;
   }

   private record PendingResult(long key, GrassSectionMesh mesh, GrassSectionBuildBuffers buffers, int generation) {
   }
}
