package com.seibel.distanthorizons.core.dataObjects.transformers;

import com.seibel.distanthorizons.api.enums.config.EDhApiWorldCompressionMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiChunkProcessingEvent;
import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IMutableBlockPosWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class LodDataBuilder {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IWrapperFactory WRAPPER_FACTORY = SingletonInjector.INSTANCE.get(IWrapperFactory.class);
   private static final IBlockStateWrapper AIR = WRAPPER_FACTORY.getAirBlockStateWrapper();
   private static boolean getTopErrorLogged = false;

   public static FullDataSourceV2 createFromChunk(ILevelWrapper levelWrapper, IChunkWrapper chunkWrapper) {
      LodUtil.assertTrue(chunkWrapper.isDhBlockLightingCorrect(), "Provided chunk's DH Block lighting hasn't been baked.");
      int chunkPosX = chunkWrapper.getChunkPos().getX();
      int chunkPosZ = chunkWrapper.getChunkPos().getZ();
      int sectionPosX = getXOrZSectionPosFromChunkPos(chunkWrapper.getChunkPos().getX());
      int sectionPosZ = getXOrZSectionPosFromChunkPos(chunkWrapper.getChunkPos().getZ());
      long pos = DhSectionPos.encode((byte)6, sectionPosX, sectionPosZ);
      FullDataSourceV2 dataSource = FullDataSourceV2.createEmpty(pos);
      dataSource.isEmpty = false;
      dataSource.applyToParent = true;
      int chunkOffsetX = chunkWrapper.getChunkPos().getX() & 3;
      int chunkOffsetZ = chunkWrapper.getChunkPos().getZ() & 3;
      chunkOffsetX *= 16;
      chunkOffsetZ *= 16;
      EDhApiWorldCompressionMode worldCompressionMode = Config.Common.LodBuilding.worldCompression.get();

      try {
         IMutableBlockPosWrapper mcBlockPos = chunkWrapper.getMutableBlockPosWrapper();
         IBlockStateWrapper previousBlockState = null;
         DhApiChunkProcessingEvent.EventParam mutableChunkProcessedEventParam = new DhApiChunkProcessingEvent.EventParam(levelWrapper, chunkPosX, chunkPosZ);
         int minBuildHeight = chunkWrapper.getMinNonEmptyHeight();
         int exclusiveMaxBuildHeight = chunkWrapper.getExclusiveMaxBuildHeight();
         int inclusiveMinBuildHeight = chunkWrapper.getInclusiveMinBuildHeight();
         int dataCapacity = chunkWrapper.getHeight() / 4;

         for (int relBlockX = 0; relBlockX < 16; relBlockX++) {
            for (int relBlockZ = 0; relBlockZ < 16; relBlockZ++) {
               int columnX = relBlockX + chunkOffsetX;
               int columnZ = relBlockZ + chunkOffsetZ;
               LongArrayList longs = dataSource.getColumnAtRelPos(columnX, columnZ);
               if (longs == null) {
                  longs = new LongArrayList(dataCapacity);
               } else {
                  longs.clear();
               }

               int lastY = exclusiveMaxBuildHeight;
               IBiomeWrapper currentBiome = chunkWrapper.getBiome(relBlockX, exclusiveMaxBuildHeight, relBlockZ);
               IBlockStateWrapper currentBlockState = AIR;
               int mappedId = dataSource.mapping.addIfNotPresentAndGetId(currentBiome, currentBlockState);
               byte blockLight = 0;
               byte skyLight = 15;
               int y = Math.max(chunkWrapper.getLightBlockingHeightMapValue(relBlockX, relBlockZ), chunkWrapper.getSolidHeightMapValue(relBlockX, relBlockZ));
               IBlockStateWrapper topBlockState = previousBlockState = chunkWrapper.getBlockState(relBlockX, y, relBlockZ, mcBlockPos, previousBlockState);

               while (!topBlockState.isAir() && y < exclusiveMaxBuildHeight) {
                  try {
                     topBlockState = previousBlockState = chunkWrapper.getBlockState(relBlockX, ++y, relBlockZ, mcBlockPos, previousBlockState);
                  } catch (Exception var37) {
                     if (!getTopErrorLogged) {
                        LOGGER.warn(
                           "Unexpected issue in LodDataBuilder, future errors won't be logged. Chunk ["
                              + chunkWrapper.getChunkPos()
                              + "] with max height: ["
                              + exclusiveMaxBuildHeight
                              + "] had issue getting block at pos ["
                              + relBlockX
                              + ","
                              + y
                              + ","
                              + relBlockZ
                              + "] error: "
                              + var37.getMessage(),
                           var37
                        );
                        getTopErrorLogged = true;
                     }

                     y--;
                     break;
                  }
               }

               for (boolean forceSingleBlock = false; y >= minBuildHeight; y--) {
                  IBiomeWrapper newBiome = chunkWrapper.getBiome(relBlockX, y, relBlockZ);
                  IBlockStateWrapper newBlockState = previousBlockState = chunkWrapper.getBlockState(relBlockX, y, relBlockZ, mcBlockPos, previousBlockState);
                  byte newBlockLight = (byte)chunkWrapper.getDhBlockLight(relBlockX, y + 1, relBlockZ);
                  byte newSkyLight = (byte)chunkWrapper.getDhSkyLight(relBlockX, y + 1, relBlockZ);
                  if (!newBiome.equals(currentBiome) || !newBlockState.equals(currentBlockState) || forceSingleBlock) {
                     forceSingleBlock = !currentBlockState.isAir()
                        && !currentBlockState.isSolid()
                        && !currentBlockState.isLiquid()
                        && currentBlockState.getOpacity() != 16;
                     mutableChunkProcessedEventParam.updateForPosition(relBlockX, y, relBlockZ, newBlockState, newBiome);
                     ApiEventInjector.INSTANCE.fireAllEvents(DhApiChunkProcessingEvent.class, mutableChunkProcessedEventParam);
                     if (mutableChunkProcessedEventParam.getBlockOverride() != null) {
                        newBlockState = (IBlockStateWrapper)mutableChunkProcessedEventParam.getBlockOverride();
                     }

                     if (mutableChunkProcessedEventParam.getBiomeOverride() != null) {
                        newBiome = (IBiomeWrapper)mutableChunkProcessedEventParam.getBiomeOverride();
                     }

                     longs.add(FullDataPointUtil.encode(mappedId, lastY - y, y + 1 - inclusiveMinBuildHeight, blockLight, skyLight));
                     currentBiome = newBiome;
                     currentBlockState = newBlockState;
                     mappedId = dataSource.mapping.addIfNotPresentAndGetId(newBiome, newBlockState);
                     blockLight = newBlockLight;
                     skyLight = newSkyLight;
                     lastY = y;
                     if (dataSource.isEmpty && newBlockState != null && !newBlockState.isAir()) {
                        dataSource.isEmpty = false;
                     }
                  }
               }

               longs.add(FullDataPointUtil.encode(mappedId, lastY - y, y + 1 - inclusiveMinBuildHeight, blockLight, skyLight));
               dataSource.setSingleColumn(longs, columnX, columnZ, EDhApiWorldGenerationStep.LIGHT, worldCompressionMode);
            }
         }

         return dataSource;
      } catch (DataCorruptedException var38) {
         LOGGER.error("Unable to convert chunk at pos [" + chunkWrapper.getChunkPos() + "] to an LOD. Error: " + var38.getMessage(), var38);
         return null;
      }
   }

   public static FullDataSourceV2 createFromApiChunkData(DhApiChunk apiChunk, boolean runAdditionalValidation) throws ClassCastException, DataCorruptedException, IllegalArgumentException {
      int sectionPosX = getXOrZSectionPosFromChunkPos(apiChunk.chunkPosX);
      int sectionPosZ = getXOrZSectionPosFromChunkPos(apiChunk.chunkPosZ);
      long pos = DhSectionPos.encode((byte)6, sectionPosX, sectionPosZ);
      int relSourceBlockX = (apiChunk.chunkPosX & 3) * 16;
      int relSourceBlockZ = (apiChunk.chunkPosZ & 3) * 16;
      FullDataSourceV2 dataSource = FullDataSourceV2.createEmpty(pos);

      for (int relBlockZ = 0; relBlockZ < 16; relBlockZ++) {
         for (int relBlockX = 0; relBlockX < 16; relBlockX++) {
            List<DhApiTerrainDataPoint> columnDataPoints = apiChunk.getDataPoints(relBlockX, relBlockZ);
            if (dataSource.isEmpty) {
               for (int i = 0; i < columnDataPoints.size(); i++) {
                  DhApiTerrainDataPoint dataPoint = columnDataPoints.get(i);
                  if (dataPoint.blockStateWrapper != null && !dataPoint.blockStateWrapper.isAir()) {
                     dataSource.isEmpty = false;
                     break;
                  }
               }
            }

            putListInTopDownOrder(columnDataPoints);
            if (runAdditionalValidation) {
               validateOrThrowApiDataColumn(columnDataPoints);
            }

            LongArrayList packedDataPoints = convertApiDataPointListToPackedLongArray(
               columnDataPoints, dataSource, apiChunk.bottomYBlockPos, runAdditionalValidation
            );
            dataSource.setSingleColumn(
               packedDataPoints,
               relBlockX + relSourceBlockX,
               relBlockZ + relSourceBlockZ,
               EDhApiWorldGenerationStep.LIGHT,
               EDhApiWorldCompressionMode.MERGE_SAME_BLOCKS
            );
         }
      }

      return dataSource;
   }

   public static LongArrayList convertApiDataPointListToPackedLongArray(
      @Nullable List<DhApiTerrainDataPoint> topDownColumnDataPoints, FullDataSourceV2 dataSource, int bottomYBlockPos, boolean runAdditionalValidation
   ) throws DataCorruptedException {
      if (topDownColumnDataPoints != null && topDownColumnDataPoints.size() != 0) {
         int size = topDownColumnDataPoints.size();
         LongArrayList packedDataPoints = new LongArrayList(size);
         packedDataPoints.clear();
         if (runAdditionalValidation) {
            int lastTopY = 2147483647;

            for (int i = 0; i < size; i++) {
               DhApiTerrainDataPoint apiDataPoint = topDownColumnDataPoints.get(i);
               if (lastTopY != apiDataPoint.topYBlockPos && i != 0) {
                  throw new DataCorruptedException(
                     "LOD data has a gap between ["
                        + lastTopY
                        + "] and ["
                        + apiDataPoint.bottomYBlockPos
                        + "]. Empty areas should be filled with air datapoints so light propagates correctly."
                  );
               }

               lastTopY = apiDataPoint.bottomYBlockPos;
            }
         }

         long lastDataPoint = 0L;

         for (int i = 0; i < size; i++) {
            DhApiTerrainDataPoint apiDataPoint = topDownColumnDataPoints.get(i);
            int thisId = dataSource.mapping
               .addIfNotPresentAndGetId((IBiomeWrapper)apiDataPoint.biomeWrapper, (IBlockStateWrapper)apiDataPoint.blockStateWrapper);
            int thisHeight = apiDataPoint.topYBlockPos - apiDataPoint.bottomYBlockPos;
            int lastId = FullDataPointUtil.getId(lastDataPoint);
            byte lastBlockLight = (byte)FullDataPointUtil.getBlockLight(lastDataPoint);
            byte lastSkyLight = (byte)FullDataPointUtil.getSkyLight(lastDataPoint);
            if (thisId == lastId && apiDataPoint.blockLightLevel == lastBlockLight && apiDataPoint.skyLightLevel == lastSkyLight && i != 0) {
               int lastHeight = FullDataPointUtil.getHeight(lastDataPoint);
               int newHeight = lastHeight + thisHeight;
               long var20 = FullDataPointUtil.setHeight(lastDataPoint, newHeight);
               int lastBottomY = FullDataPointUtil.getBottomY(var20);
               int newBottomY = lastBottomY - thisHeight;
               lastDataPoint = FullDataPointUtil.setBottomY(var20, newBottomY);
               packedDataPoints.set(packedDataPoints.size() - 1, lastDataPoint);
            } else {
               long dataPoint = FullDataPointUtil.encode(
                  thisId, thisHeight, apiDataPoint.bottomYBlockPos - bottomYBlockPos, (byte)apiDataPoint.blockLightLevel, (byte)apiDataPoint.skyLightLevel
               );
               lastDataPoint = dataPoint;
               packedDataPoints.add(dataPoint);
            }
         }

         return packedDataPoints;
      } else {
         return new LongArrayList(0);
      }
   }

   public static void putListInTopDownOrder(List<DhApiTerrainDataPoint> dataPoints) {
      if (dataPoints.size() > 1) {
         DhApiTerrainDataPoint first = dataPoints.get(0);
         DhApiTerrainDataPoint last = dataPoints.get(dataPoints.size() - 1);
         if (first.bottomYBlockPos < last.bottomYBlockPos) {
            Collections.reverse(dataPoints);
         }
      }
   }

   public static void validateOrThrowApiDataColumn(List<DhApiTerrainDataPoint> dataPoints) throws IllegalArgumentException {
      int lastBottomYPos = -2147483648;

      for (int i = 0; i < dataPoints.size(); i++) {
         DhApiTerrainDataPoint dataPoint = dataPoints.get(i);
         if (dataPoint == null) {
            throw new IllegalArgumentException(
               "Datapoint: [" + i + "] is null DhApiTerrainDataPoints are not allowed. If you want to represent empty terrain, please use AIR."
            );
         }

         if (dataPoint.detailLevel != 0) {
            throw new IllegalArgumentException(
               "Datapoint: ["
                  + i
                  + "] has the wrong detail level ["
                  + dataPoint.detailLevel
                  + "], all data points must be block sized; IE their detail level must be [0]."
            );
         }

         int bottomYPos = dataPoint.bottomYBlockPos;
         int topYPos = dataPoint.topYBlockPos;
         int height = dataPoint.topYBlockPos - dataPoint.bottomYBlockPos;
         if (bottomYPos > topYPos) {
            throw new IllegalArgumentException("Datapoint: [" + i + "] is upside down. Top Pos: [" + topYPos + "], bottom pos: [" + bottomYPos + "].");
         }

         if (height <= 0 || height >= 6095) {
            throw new IllegalArgumentException("Datapoint: [" + i + "] has invalid height. Height must be in the range [1 - " + 6095 + "] (inclusive).");
         }

         if (lastBottomYPos > topYPos) {
            throw new IllegalArgumentException(
               "DhApiTerrainDataPoint ["
                  + i
                  + "] is overlapping with the last datapoint, this top Y: ["
                  + topYPos
                  + "], lastBottomYPos: ["
                  + lastBottomYPos
                  + "]."
            );
         }

         if (topYPos != lastBottomYPos && lastBottomYPos != -2147483648) {
            throw new IllegalArgumentException(
               "DhApiTerrainDataPoint ["
                  + i
                  + "] has a gap between it and index ["
                  + (i - 1)
                  + "]. Empty spaces should be filled by air, otherwise DH's downsampling won't calculate lighting correctly."
            );
         }

         lastBottomYPos = bottomYPos;
      }
   }

   private static int getXOrZSectionPosFromChunkPos(int chunkXOrZPos) {
      return chunkXOrZPos < 0 ? (chunkXOrZPos + 1) / 4 - 1 : chunkXOrZPos / 4;
   }
}
