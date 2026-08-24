package com.seibel.distanthorizons.core.generation;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhChunkPos;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.render.renderer.AbstractDebugWireframeRenderer;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IMutableBlockPosWrapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

public class DhLightingEngine {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final DhLightingEngine INSTANCE = new DhLightingEngine();
   private static final AbstractDebugWireframeRenderer DEBUG_RENDERER = SingletonInjector.INSTANCE.get(AbstractDebugWireframeRenderer.class);
   private static final ThreadLocal<DhBlockPosMutable> PRIMARY_BLOCK_POS_REF = ThreadLocal.withInitial(() -> new DhBlockPosMutable());
   private static final ThreadLocal<DhBlockPosMutable> SECONDARY_BLOCK_POS_REF = ThreadLocal.withInitial(() -> new DhBlockPosMutable());
   private static final boolean RENDER_BLOCK_LIGHT_WIREFRAME = false;
   private static final boolean RENDER_SKY_LIGHT_WIREFRAME = false;
   private static final byte[] ADJACENT_DIRECTION_OFFSETS = new byte[]{-1, 0, 1, 0, 0, -1, 0, 1};

   private DhLightingEngine() {
   }

   public void lightChunk(@NotNull IChunkWrapper centerChunk, @NotNull ArrayList<IChunkWrapper> nearbyChunkList, int maxSkyLight) {
      this.lightChunk(centerChunk, nearbyChunkList, maxSkyLight, true, true);
   }

   public void bakeChunkBlockLighting(@NotNull IChunkWrapper centerChunk, @NotNull ArrayList<IChunkWrapper> nearbyChunkList, int maxSkyLight) {
      this.lightChunk(centerChunk, nearbyChunkList, maxSkyLight, true, false);
   }

   private int lightChunk(
      @NotNull IChunkWrapper centerChunk, @NotNull ArrayList<IChunkWrapper> nearbyChunkList, int maxSkyLight, boolean updateBlockLight, boolean updateSkyLight
   ) {
      DhChunkPos centerChunkPos = centerChunk.getChunkPos();
      AdjacentChunkHolder adjacentChunkHolder = new AdjacentChunkHolder(centerChunk);
      int posIterations = 0;
      DhLightingEngine.StableLightPosStack blockLightWorldPosQueue = null;
      DhLightingEngine.StableLightPosStack skyLightWorldPosQueue = null;

      try {
         blockLightWorldPosQueue = DhLightingEngine.StableLightPosStack.borrowStableLightPosArray();
         skyLightWorldPosQueue = DhLightingEngine.StableLightPosStack.borrowStableLightPosArray();
         HashSet<DhChunkPos> requestedAdjacentPositions = new HashSet<>(9);

         for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
               DhChunkPos adjacentPos = new DhChunkPos(centerChunkPos.getX() + xOffset, centerChunkPos.getZ() + zOffset);
               requestedAdjacentPositions.add(adjacentPos);
            }
         }

         for (int chunkIndex = 0; chunkIndex < nearbyChunkList.size(); chunkIndex++) {
            IChunkWrapper neighborChunk = nearbyChunkList.get(chunkIndex);
            if (neighborChunk != null && requestedAdjacentPositions.contains(neighborChunk.getChunkPos())) {
               requestedAdjacentPositions.remove(neighborChunk.getChunkPos());
               adjacentChunkHolder.add(neighborChunk);
               DhBlockPosMutable relLightBlockPos = PRIMARY_BLOCK_POS_REF.get();
               if (updateBlockLight) {
                  ArrayList<DhBlockPos> blockLightPosList = neighborChunk.getWorldBlockLightPosList();

                  for (int blockLightIndex = 0; blockLightIndex < blockLightPosList.size(); blockLightIndex++) {
                     DhBlockPos blockLightPos = blockLightPosList.get(blockLightIndex);
                     blockLightPos.mutateToChunkRelativePos(relLightBlockPos);
                     IBlockStateWrapper blockState = neighborChunk.getBlockState(relLightBlockPos);
                     int lightValue = blockState.getLightEmission();
                     blockLightWorldPosQueue.push(blockLightPos.getX(), blockLightPos.getY(), blockLightPos.getZ(), lightValue);
                     neighborChunk.setDhBlockLight(relLightBlockPos.getX(), relLightBlockPos.getY(), relLightBlockPos.getZ(), lightValue);
                  }
               }

               if (updateSkyLight && maxSkyLight > 0) {
                  IMutableBlockPosWrapper mcBlockPos = neighborChunk.getMutableBlockPosWrapper();
                  IBlockStateWrapper previousBlockState = null;
                  int maxY = neighborChunk.getMaxNonEmptyHeight();
                  int minY = neighborChunk.getInclusiveMinBuildHeight();

                  for (int relX = 0; relX < 16; relX++) {
                     for (int relZ = 0; relZ < 16; relZ++) {
                        for (int y = maxY; y >= minY; y--) {
                           IBlockStateWrapper block = previousBlockState = neighborChunk.getBlockState(relX, y, relZ, mcBlockPos, previousBlockState);
                           if (block != null && block.getOpacity() != 0) {
                              break;
                           }

                           DhBlockPos skyLightPos = new DhBlockPos(neighborChunk.getMinBlockX() + relX, y, neighborChunk.getMinBlockZ() + relZ);
                           skyLightWorldPosQueue.push(skyLightPos.getX(), skyLightPos.getY(), skyLightPos.getZ(), maxSkyLight);
                           skyLightPos.mutateToChunkRelativePos(relLightBlockPos);
                           neighborChunk.setDhSkyLight(relLightBlockPos.getX(), relLightBlockPos.getY(), relLightBlockPos.getZ(), maxSkyLight);
                        }
                     }
                  }
               }
            }

            if (requestedAdjacentPositions.isEmpty()) {
               break;
            }
         }

         if (updateBlockLight) {
            centerChunk.clearDhBlockLighting();
            posIterations += this.propagateChunkLightPosList(
               blockLightWorldPosQueue,
               adjacentChunkHolder,
               (neighbourChunk, relBlockPos) -> neighbourChunk.getDhBlockLight(relBlockPos.getX(), relBlockPos.getY(), relBlockPos.getZ()),
               (neighbourChunk, relBlockPos, newLightValue) -> neighbourChunk.setDhBlockLight(
                  relBlockPos.getX(), relBlockPos.getY(), relBlockPos.getZ(), newLightValue
               ),
               true
            );
         }

         if (updateSkyLight) {
            centerChunk.clearDhSkyLighting();
            posIterations += this.propagateChunkLightPosList(
               skyLightWorldPosQueue,
               adjacentChunkHolder,
               (neighbourChunk, relBlockPos) -> neighbourChunk.getDhSkyLight(relBlockPos.getX(), relBlockPos.getY(), relBlockPos.getZ()),
               (neighbourChunk, relBlockPos, newLightValue) -> neighbourChunk.setDhSkyLight(
                  relBlockPos.getX(), relBlockPos.getY(), relBlockPos.getZ(), newLightValue
               ),
               false
            );
         }
      } catch (Exception var27) {
         LOGGER.error("Unexpected lighting issue for center chunk: " + centerChunkPos, var27);
      } finally {
         DhLightingEngine.StableLightPosStack.returnStableLightPosArray(blockLightWorldPosQueue);
         DhLightingEngine.StableLightPosStack.returnStableLightPosArray(skyLightWorldPosQueue);
      }

      if (updateBlockLight) {
         centerChunk.setIsDhBlockLightCorrect(true);
      }

      if (updateSkyLight) {
         centerChunk.setIsDhSkyLightCorrect(true);
      }

      return posIterations;
   }

   private int propagateChunkLightPosList(
      DhLightingEngine.StableLightPosStack lightPosQueue,
      AdjacentChunkHolder adjacentChunkHolder,
      DhLightingEngine.IGetLightFunc getLightFunc,
      DhLightingEngine.ISetLightFunc setLightFunc,
      boolean propagatingBlockLights
   ) {
      DhLightingEngine.LightPos lightPos = new DhLightingEngine.LightPos(0, 0, 0, 0);
      DhBlockPosMutable neighbourBlockPos = PRIMARY_BLOCK_POS_REF.get();
      DhBlockPosMutable relNeighbourBlockPos = SECONDARY_BLOCK_POS_REF.get();
      IMutableBlockPosWrapper mcBlockPos = null;

      for (int i = 0; i < adjacentChunkHolder.chunkArray.length; i++) {
         IChunkWrapper chunkWrapper = adjacentChunkHolder.chunkArray[i];
         if (chunkWrapper != null) {
            mcBlockPos = chunkWrapper.getMutableBlockPosWrapper();
            break;
         }
      }

      if (mcBlockPos == null) {
         LodUtil.assertNotReach("How did we try to light a chunk with no chunks?");
      }

      IBlockStateWrapper previousBlockState = null;
      int iterations = 0;

      for (int currentLightLevel = 15; currentLightLevel >= 0; currentLightLevel--) {
         lightPos.lightValue = currentLightLevel;

         while (!lightPosQueue.isLightLevelEmpty(currentLightLevel)) {
            lightPosQueue.popMutate(lightPos, currentLightLevel);
            iterations++;
            int lightValue = lightPos.lightValue;

            for (EDhDirection direction : EDhDirection.ALL) {
               lightPos.mutateOffset(direction, neighbourBlockPos);
               neighbourBlockPos.mutateToChunkRelativePos(relNeighbourBlockPos);
               IChunkWrapper neighbourChunk = adjacentChunkHolder.getByBlockPos(neighbourBlockPos.getX(), neighbourBlockPos.getZ());
               if (neighbourChunk != null
                  && relNeighbourBlockPos.getY() >= neighbourChunk.getMinNonEmptyHeight()
                  && relNeighbourBlockPos.getY() < neighbourChunk.getExclusiveMaxBuildHeight()) {
                  int currentBlockLight = getLightFunc.getLight(neighbourChunk, relNeighbourBlockPos);
                  if (currentBlockLight < lightValue - 1) {
                     IBlockStateWrapper neighbourBlockState = neighbourChunk.getBlockState(relNeighbourBlockPos, mcBlockPos, previousBlockState);
                     previousBlockState = neighbourBlockState;
                     int targetLightLevel = lightValue - Math.max(1, neighbourBlockState.getOpacity());
                     if (targetLightLevel > currentBlockLight) {
                        setLightFunc.setLight(neighbourChunk, relNeighbourBlockPos, targetLightLevel);
                        lightPosQueue.push(neighbourBlockPos.getX(), neighbourBlockPos.getY(), neighbourBlockPos.getZ(), targetLightLevel);
                     }
                  }
               }
            }
         }
      }

      for (int currentLightLevel = 15; currentLightLevel >= 0; currentLightLevel--) {
         if (!lightPosQueue.isLightLevelEmpty(currentLightLevel)) {
            LodUtil.assertNotReach("Non empty light pos queue for light level [" + currentLightLevel + "] after light engine running");
         }
      }

      return iterations;
   }

   public void bakeDataSourceSkyLight(FullDataSourceV2 dataSource, int maxSkyLight) {
      BitSet airIDs = new BitSet(dataSource.mapping.size());
      int id = 0;

      for (int size = dataSource.mapping.size(); id < size; id++) {
         if (dataSource.mapping.getBlockStateWrapper(id).getOpacity() == 0) {
            airIDs.set(id, true);
         }
      }

      for (int z = 0; z < 64; z++) {
         for (int x = 0; x < 64; x++) {
            LongArrayList dataPoints = dataSource.getColumnAtRelPos(x, z);
            if (dataPoints != null && !dataPoints.isEmpty()) {
               int sizex = dataPoints.size();

               for (int index = 0; index < sizex; index++) {
                  long point = dataPoints.getLong(index);
                  if (airIDs.get(FullDataPointUtil.getId(point))) {
                     int skylight;
                     if (index == 0) {
                        skylight = maxSkyLight;
                     } else {
                        long above = dataPoints.getLong(index - 1);
                        if (!airIDs.get(FullDataPointUtil.getId(above))) {
                           continue;
                        }

                        skylight = FullDataPointUtil.getSkyLight(above);
                     }

                     point = FullDataPointUtil.setSkyLight(point, skylight);
                     dataPoints.set(index, point);
                     this.recursivelyLightAdjacentDataPoints(dataSource, airIDs, x, z, point);
                  }
               }
            }
         }
      }

      for (LongArrayList list : dataSource.dataPoints) {
         if (list != null) {
            int indexx = 0;

            for (int sizex = list.size(); indexx < sizex; indexx++) {
               long dataPoint = list.getLong(indexx);
               if (indexx == 0) {
                  dataPoint = FullDataPointUtil.setSkyLight(dataPoint, maxSkyLight);
                  list.set(indexx, dataPoint);
               } else if (!airIDs.get(FullDataPointUtil.getId(dataPoint))) {
                  long above = list.getLong(indexx - 1);
                  int aboveLight = FullDataPointUtil.getSkyLight(above);
                  if (airIDs.get(FullDataPointUtil.getId(above))) {
                     dataPoint = FullDataPointUtil.setSkyLight(dataPoint, aboveLight);
                     list.set(indexx, dataPoint);
                  } else {
                     int absorption = dataSource.mapping.getBlockStateWrapper(FullDataPointUtil.getId(above)).getOpacity() * FullDataPointUtil.getHeight(above);
                     if (absorption < aboveLight) {
                        dataPoint = FullDataPointUtil.setSkyLight(dataPoint, aboveLight - absorption);
                        list.set(indexx, dataPoint);
                     }
                  }
               }
            }
         }
      }
   }

   public void recursivelyLightAdjacentDataPoints(FullDataSourceV2 chunk, BitSet airIDs, int relativeX, int relativeZ, long dataPoint) {
      int lightLevel = FullDataPointUtil.getSkyLight(dataPoint);
      if (lightLevel > 1) {
         int minY = FullDataPointUtil.getBottomY(dataPoint);
         int maxY = FullDataPointUtil.getHeight(dataPoint) + minY;
         int offsetIndex = 0;

         while (offsetIndex < ADJACENT_DIRECTION_OFFSETS.length) {
            int adjacentX = relativeX + ADJACENT_DIRECTION_OFFSETS[offsetIndex++];
            int adjacentZ = relativeZ + ADJACENT_DIRECTION_OFFSETS[offsetIndex++];
            if (adjacentX >= 0 && adjacentX < 64 && adjacentZ >= 0 && adjacentZ < 64) {
               LongArrayList adjacentDataPoints = chunk.getColumnAtRelPos(adjacentX, adjacentZ);
               if (adjacentDataPoints != null) {
                  int size = adjacentDataPoints.size();

                  for (int adjacentIndex = 0; adjacentIndex < size; adjacentIndex++) {
                     long adjacentDataPoint = adjacentDataPoints.getLong(adjacentIndex);
                     int adjacentMinY = FullDataPointUtil.getBottomY(adjacentDataPoint);
                     int adjacentMaxY = FullDataPointUtil.getHeight(adjacentDataPoint) + adjacentMinY;
                     if (adjacentMinY < maxY) {
                        if (adjacentMaxY <= minY) {
                           break;
                        }

                        if (airIDs.get(FullDataPointUtil.getId(adjacentDataPoint))) {
                           int adjacentLightLevel = FullDataPointUtil.getSkyLight(adjacentDataPoint);
                           if (lightLevel - 1 > adjacentLightLevel) {
                              adjacentDataPoint = FullDataPointUtil.setSkyLight(adjacentDataPoint, lightLevel - 1);
                              adjacentDataPoints.set(adjacentIndex, adjacentDataPoint);
                              this.recursivelyLightAdjacentDataPoints(chunk, airIDs, adjacentX, adjacentZ, adjacentDataPoint);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void RenderDhLightValuesAsWireframe(AdjacentChunkHolder adjacentChunkHolder, boolean renderBlockLights) {
      for (IChunkWrapper chunk : adjacentChunkHolder.chunkArray) {
         if (chunk != null) {
            int chunkMinX = chunk.getMinBlockX();
            int chunkMinZ = chunk.getMinBlockZ();
            int minY = chunk.getMinNonEmptyHeight();
            int maxY = chunk.getMaxNonEmptyHeight();

            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = minY; y < maxY; y++) {
                     int lightValue = renderBlockLights ? chunk.getDhBlockLight(x, y, z) : chunk.getDhSkyLight(x, y, z);
                     if (lightValue != 0) {
                        Color color;
                        if (lightValue >= 14) {
                           color = Color.WHITE;
                        } else if (lightValue >= 10) {
                           color = Color.PINK;
                        } else if (lightValue >= 6) {
                           color = Color.YELLOW;
                        } else if (lightValue >= 4) {
                           color = Color.ORANGE;
                        } else {
                           color = Color.RED;
                        }

                        if (color != null) {
                           DEBUG_RENDERER.makeParticle(
                              new AbstractDebugWireframeRenderer.BoxParticle(
                                 new AbstractDebugWireframeRenderer.Box(DhSectionPos.encode((byte)0, chunkMinX + x, chunkMinZ + z), y, y + 1, 0.2F, color),
                                 10.0,
                                 0.0F
                              )
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @FunctionalInterface
   interface IGetLightFunc {
      int getLight(IChunkWrapper iChunkWrapper, DhBlockPos dhBlockPos);
   }

   @FunctionalInterface
   interface ISetLightFunc {
      void setLight(IChunkWrapper iChunkWrapper, DhBlockPos dhBlockPos, int i);
   }

   private static class LightPos extends DhBlockPosMutable {
      public int lightValue;

      public LightPos(int x, int y, int z, int lightValue) {
         super(x, y, z);
         this.lightValue = lightValue;
      }

      @Override
      public String toString() {
         return this.lightValue + " - [" + this.x + ", " + this.y + ", " + this.z + "]";
      }
   }

   private static class StableLightPosStack {
      private static final ReentrantLock cacheLock = new ReentrantLock();
      private static final Queue<DhLightingEngine.StableLightPosStack> lightArrayCache = new ArrayDeque<>();
      private int[] indexByLightLevel = new int[16];
      public static final int INTS_PER_LIGHT_POS = 3;
      private final IntArrayList[] lightPositionsByLightLevel = new IntArrayList[16];

      public StableLightPosStack() {
         for (int i = 0; i < this.lightPositionsByLightLevel.length; i++) {
            this.lightPositionsByLightLevel[i] = new IntArrayList(120000);
            this.indexByLightLevel[i] = -1;
         }
      }

      private static DhLightingEngine.StableLightPosStack borrowStableLightPosArray() {
         DhLightingEngine.StableLightPosStack var0;
         try {
            cacheLock.lock();
            var0 = lightArrayCache.isEmpty() ? new DhLightingEngine.StableLightPosStack() : lightArrayCache.remove();
         } finally {
            cacheLock.unlock();
         }

         return var0;
      }

      private static void returnStableLightPosArray(DhLightingEngine.StableLightPosStack stableArray) {
         try {
            cacheLock.lock();
            if (stableArray != null) {
               lightArrayCache.add(stableArray);
            }
         } finally {
            cacheLock.unlock();
         }
      }

      public boolean isLightLevelEmpty(int lightLevel) {
         return this.indexByLightLevel[lightLevel] == -1;
      }

      public void push(int blockX, int blockY, int blockZ, int lightLevel) {
         IntArrayList lightPositions = this.lightPositionsByLightLevel[lightLevel];
         this.indexByLightLevel[lightLevel]++;
         int subIndex = this.indexByLightLevel[lightLevel] * 3;
         if (subIndex < lightPositions.size()) {
            lightPositions.set(subIndex, blockX);
            lightPositions.set(subIndex + 1, blockY);
            lightPositions.set(subIndex + 2, blockZ);
         } else {
            lightPositions.add(blockX);
            lightPositions.add(blockY);
            lightPositions.add(blockZ);
         }
      }

      public void popMutate(DhLightingEngine.LightPos pos, int lightLevel) {
         int subIndex = this.indexByLightLevel[lightLevel] * 3;
         IntArrayList lightPositions = this.lightPositionsByLightLevel[lightLevel];
         pos.setX(lightPositions.getInt(subIndex));
         pos.setY(lightPositions.getInt(subIndex + 1));
         pos.setZ(lightPositions.getInt(subIndex + 2));
         this.indexByLightLevel[lightLevel]--;
      }

      @Override
      public String toString() {
         StringBuilder builder = new StringBuilder();

         for (int i = 0; i < this.indexByLightLevel.length; i++) {
            builder.append("light: ")
               .append(i)
               .append(" size: ")
               .append(this.indexByLightLevel[i])
               .append("/")
               .append(this.lightPositionsByLightLevel[i].size() / 3)
               .append("\n");
         }

         return builder.toString();
      }
   }
}
