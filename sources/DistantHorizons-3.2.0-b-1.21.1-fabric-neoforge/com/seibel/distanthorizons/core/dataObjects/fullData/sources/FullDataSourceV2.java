package com.seibel.distanthorizons.core.dataObjects.fullData.sources;

import com.seibel.distanthorizons.api.enums.config.EDhApiWorldCompressionMode;
import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.api.objects.data.DhApiTerrainDataPoint;
import com.seibel.distanthorizons.api.objects.data.IDhApiFullDataSource;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.transformers.FullDataOcclusionCuller;
import com.seibel.distanthorizons.core.dataObjects.transformers.LodDataBuilder;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.sql.dto.util.FullDataMinMaxPosUtil;
import com.seibel.distanthorizons.core.util.BoolUtil;
import com.seibel.distanthorizons.core.util.DhApiTerrainDataPointUtil;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.ListUtil;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.AbstractPhantomArrayList;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.wrapperInterfaces.chunk.IChunkWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullDataSourceV2 extends AbstractPhantomArrayList implements IDhApiFullDataSource {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final boolean RUN_UPDATE_DEV_VALIDATION = false;
   private static final boolean RUN_DATA_ORDER_VALIDATION = ModInfo.IS_DEV_BUILD;
   public static final int WIDTH = 64;
   public static final int NUMB_OF_CHUNKS_WIDE = 4;
   private static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("FullDataV2");
   private static final ThreadLocal<FullDataPointIdMap> DATA_MAP_FOR_REMAPPING_REF = ThreadLocal.withInitial(
      () -> new FullDataPointIdMap(DhSectionPos.encode((byte)0, 0, 0))
   );
   private int cachedHashCode = 0;
   private final long pos;
   public final FullDataPointIdMap mapping;
   public long lastModifiedUnixDateTime;
   public long createdUnixDateTime;
   public final ByteArrayList columnGenerationSteps;
   public final ByteArrayList columnWorldCompressionMode;
   public final LongArrayList[] dataPoints;
   public boolean isEmpty;
   @Nullable
   public Boolean applyToParent = null;
   @Nullable
   public Boolean applyToChildren = null;
   private boolean runApiSetterValidation = false;

   public static FullDataSourceV2 createFromChunk(ILevelWrapper levelWrapper, IChunkWrapper chunkWrapper) {
      return LodDataBuilder.createFromChunk(levelWrapper, chunkWrapper);
   }

   public static FullDataSourceV2 createFromLegacyDataSourceV1(FullDataSourceV1 legacyData) {
      if (FullDataSourceV1.WIDTH != 64) {
         throw new UnsupportedOperationException(
            "Unable to convert ["
               + FullDataSourceV1.class.getSimpleName()
               + "] into ["
               + FullDataSourceV2.class.getSimpleName()
               + "]. Data sources have different data point widths and no converter is present. input width ["
               + FullDataSourceV1.WIDTH
               + "], recipient width ["
               + 64
               + "]."
         );
      } else {
         byte[] columnGenerationSteps = new byte[4096];
         byte[] columnWorldCompressionMode = new byte[4096];
         LongArrayList[] dataPoints = new LongArrayList[4096];

         for (int x = 0; x < 64; x++) {
            for (int z = 0; z < 64; z++) {
               long[] legacyDataColumn = legacyData.get(x, z);
               if (legacyDataColumn != null && legacyDataColumn.length != 0) {
                  int index = relativePosToIndex(x, z);
                  LongArrayList newDataColumn = new LongArrayList(legacyDataColumn);
                  boolean columnHasNonAirBlock = false;

                  for (int i = 0; i < legacyDataColumn.length; i++) {
                     long dataPoint = legacyDataColumn[i];
                     boolean isAir = legacyData.mapping.getBlockStateWrapper(FullDataPointUtil.getId(dataPoint)).isAir();
                     byte blockLight = (byte)FullDataPointUtil.getBlockLight(dataPoint);
                     if (isAir) {
                        blockLight = 0;
                     }

                     dataPoint = FullDataPointUtil.setBlockLight(dataPoint, blockLight);
                     newDataColumn.set(i, dataPoint);
                     if (!columnHasNonAirBlock && !isAir) {
                        columnHasNonAirBlock = true;
                     }
                  }

                  ensureDataColumnOrder(newDataColumn);
                  dataPoints[index] = newDataColumn;
                  columnGenerationSteps[index] = columnHasNonAirBlock ? EDhApiWorldGenerationStep.LIGHT.value : EDhApiWorldGenerationStep.EMPTY.value;
                  columnWorldCompressionMode[index] = EDhApiWorldCompressionMode.MERGE_SAME_BLOCKS.value;
               }
            }
         }

         return createWithData(legacyData.getPos(), legacyData.mapping, dataPoints, columnGenerationSteps, columnWorldCompressionMode);
      }
   }

   public static FullDataSourceV2 createEmpty(long pos) {
      FullDataPointIdMap map = new FullDataPointIdMap(pos);
      return new FullDataSourceV2(pos, map, null, null, null, true);
   }

   public static FullDataSourceV2 createWithData(
      long pos, FullDataPointIdMap mapping, LongArrayList[] data, byte[] columnGenerationStep, byte[] columnWorldCompressionMode
   ) {
      return new FullDataSourceV2(pos, mapping, data, columnGenerationStep, columnWorldCompressionMode, false);
   }

   private FullDataSourceV2(
      long pos,
      FullDataPointIdMap mapping,
      @Nullable LongArrayList[] data,
      @Nullable byte[] columnGenerationSteps,
      @Nullable byte[] columnWorldCompressionMode,
      boolean empty
   ) {
      super(ARRAY_LIST_POOL, 2, 0, 4096, 0, 0);
      LodUtil.assertTrue(data == null || data.length == 4096);
      this.pos = pos;
      this.mapping = mapping;
      this.isEmpty = empty;
      this.dataPoints = new LongArrayList[4096];

      for (int i = 0; i < 4096; i++) {
         this.dataPoints[i] = this.pooledArraysCheckout.getLongArray(i, 0);
      }

      if (data != null) {
         for (int i = 0; i < 4096; i++) {
            this.dataPoints[i].addAll(data[i]);
         }
      }

      this.columnGenerationSteps = this.pooledArraysCheckout.getByteArray(0, 0);
      if (columnGenerationSteps != null) {
         this.columnGenerationSteps.addElements(0, columnGenerationSteps);
      } else {
         ListUtil.clearAndSetSize(this.columnGenerationSteps, 4096);
      }

      this.columnWorldCompressionMode = this.pooledArraysCheckout.getByteArray(1, 0);
      if (columnWorldCompressionMode != null) {
         this.columnWorldCompressionMode.addElements(0, columnWorldCompressionMode);
      } else {
         ListUtil.clearAndSetSize(this.columnWorldCompressionMode, 4096);
      }
   }

   public LongArrayList getColumnAtRelPos(int relX, int relZ) throws IndexOutOfBoundsException {
      return this.dataPoints[relativePosToIndex(relX, relZ)];
   }

   @Nullable
   public LongArrayList tryGetColumnAtRelPos(int relX, int relZ) {
      int index = tryGetRelativePosToIndex(relX, relZ);
      return index == -1 ? null : this.dataPoints[index];
   }

   public long getDataPointAtBlockPos(int blockPosX, int blockPosY, int blockPosZ, int levelMinY) {
      long requestedPos = DhSectionPos.encode((byte)0, blockPosX, blockPosZ);
      long sectionPos = DhSectionPos.encodeContaining((byte)6, new DhBlockPos(blockPosX, blockPosY, blockPosZ));
      if (!DhSectionPos.contains(this.pos, sectionPos)) {
         return 0L;
      } else {
         byte requestDetailLevel = (byte)(DhSectionPos.getDetailLevel(this.pos) - 6);
         long relativePos = DhSectionPos.getDhSectionRelativePositionForDetailLevel(requestedPos, requestDetailLevel);
         LongArrayList dataColumn = this.getColumnAtRelPos(DhSectionPos.getX(relativePos), DhSectionPos.getZ(relativePos));
         if (dataColumn == null) {
            return 0L;
         } else {
            int relBlockPosY = blockPosY - levelMinY;

            for (int i = 0; i < dataColumn.size(); i++) {
               long dataPoint = dataColumn.getLong(i);
               if (dataPoint != 0L) {
                  int bottomY = FullDataPointUtil.getBottomY(dataPoint);
                  int height = FullDataPointUtil.getHeight(dataPoint);
                  int topY = bottomY + height;
                  if (bottomY <= relBlockPosY && relBlockPosY < topY) {
                     return dataPoint;
                  }
               }
            }

            return 0L;
         }
      }
   }

   public boolean updateFromDataSource(@NotNull FullDataSourceV2 inputDataSource) {
      if (inputDataSource.mapping.isEmpty()) {
         return false;
      } else {
         byte thisDetailLevel = DhSectionPos.getDetailLevel(this.pos);
         byte inputDetailLevel = DhSectionPos.getDetailLevel(inputDataSource.pos);
         int[] remappedIds = this.mapping.mergeAndReturnRemappedEntityIds(inputDataSource.mapping);
         boolean dataChanged;
         if (inputDetailLevel == thisDetailLevel) {
            dataChanged = this.updateFromSameDetailLevel(inputDataSource, remappedIds);
            if (this.applyToParent != null || inputDataSource.applyToParent != null) {
               this.applyToParent = (BoolUtil.falseIfNull(this.applyToParent) || BoolUtil.falseIfNull(inputDataSource.applyToParent))
                  && DhSectionPos.getDetailLevel(this.pos) < 15;
            }

            if (this.applyToChildren != null || inputDataSource.applyToChildren != null) {
               this.applyToChildren = (BoolUtil.falseIfNull(this.applyToChildren) || BoolUtil.falseIfNull(inputDataSource.applyToChildren))
                  && DhSectionPos.getDetailLevel(this.pos) > 6;
            }
         } else if (inputDetailLevel + 1 == thisDetailLevel) {
            dataChanged = this.updateFromOneBelowDetailLevel(inputDataSource, remappedIds);
            this.applyToParent = dataChanged
               && (BoolUtil.falseIfNull(this.applyToParent) || BoolUtil.falseIfNull(inputDataSource.applyToParent))
               && DhSectionPos.getDetailLevel(this.pos) < 15;
         } else {
            if (inputDetailLevel - 1 != thisDetailLevel) {
               throw new UnsupportedOperationException(
                  "Unsupported data source update. Expected input detail level of ["
                     + (thisDetailLevel - 1)
                     + "], ["
                     + thisDetailLevel
                     + "], or ["
                     + (thisDetailLevel + 1)
                     + "], received detail level ["
                     + inputDetailLevel
                     + "]."
               );
            }

            dataChanged = this.downsampleFromOneAboveDetailLevel(inputDataSource, remappedIds);
            this.applyToChildren = dataChanged
               && (BoolUtil.falseIfNull(this.applyToChildren) || BoolUtil.falseIfNull(inputDataSource.applyToChildren))
               && DhSectionPos.getDetailLevel(this.pos) > 6;
         }

         this.removeUnusedIdsAndRemap();
         if (dataChanged) {
            EDhApiWorldCompressionMode worldCompressionMode = Config.Common.LodBuilding.worldCompression.get();
            boolean cullHiddenBlocks = worldCompressionMode != EDhApiWorldCompressionMode.MERGE_SAME_BLOCKS;
            if (cullHiddenBlocks) {
               for (int x = 0; x < 64; x++) {
                  for (int z = 0; z < 64; z++) {
                     LongArrayList dataColumn = this.getColumnAtRelPos(x, z);
                     if (dataColumn != null && dataColumn.size() > 1) {
                        FullDataOcclusionCuller.cullHiddenDatapointsInColumn(this, x, z);
                     }
                  }
               }
            }

            this.generateHashCode();
         }

         return dataChanged;
      }
   }

   private boolean updateFromSameDetailLevel(FullDataSourceV2 inputDataSource, int[] remappedIds) {
      if (DhSectionPos.getDetailLevel(inputDataSource.pos) != DhSectionPos.getDetailLevel(this.pos)) {
         throw new IllegalArgumentException(
            "Both data sources must have the same detail level. Expected ["
               + DhSectionPos.getDetailLevel(this.pos)
               + "], received ["
               + DhSectionPos.getDetailLevel(inputDataSource.pos)
               + "]."
         );
      } else {
         boolean dataChanged = false;

         for (int x = 0; x < 64; x++) {
            for (int z = 0; z < 64; z++) {
               int index = relativePosToIndex(x, z);
               LongArrayList inputDataArray = inputDataSource.dataPoints[index];
               if (inputDataArray != null) {
                  byte thisGenState = this.columnGenerationSteps.getByte(index);
                  byte inputGenState = inputDataSource.columnGenerationSteps.getByte(index);
                  boolean genStateAllowsUpdating = false;
                  if (inputGenState != EDhApiWorldGenerationStep.DOWN_SAMPLED.value
                     || thisGenState != EDhApiWorldGenerationStep.EMPTY.value && thisGenState != EDhApiWorldGenerationStep.DOWN_SAMPLED.value) {
                     if (inputGenState != EDhApiWorldGenerationStep.EMPTY.value && thisGenState <= inputGenState) {
                        genStateAllowsUpdating = true;
                     }
                  } else {
                     genStateAllowsUpdating = true;
                  }

                  if (genStateAllowsUpdating) {
                     if (this.dataPoints[index] == null) {
                        this.dataPoints[index] = new LongArrayList(inputDataArray);
                        dataChanged = true;
                     } else if (this.dataPoints[index].size() != inputDataArray.size()) {
                        dataChanged = true;
                     }

                     int oldDataHash = 0;
                     if (!dataChanged) {
                        oldDataHash = this.dataPoints[index].hashCode();
                     }

                     this.dataPoints[index].clear();
                     this.dataPoints[index].addAll(inputDataArray);
                     this.remapDataColumn(index, remappedIds);
                     if (RUN_DATA_ORDER_VALIDATION) {
                        throwIfDataColumnInWrongOrder(inputDataSource.pos, this.dataPoints[index]);
                     }

                     if (!dataChanged && oldDataHash != this.dataPoints[index].hashCode()) {
                        dataChanged = true;
                     }

                     this.columnGenerationSteps.set(index, inputGenState);
                     this.columnWorldCompressionMode.set(index, inputDataSource.columnWorldCompressionMode.getByte(index));
                     this.isEmpty = false;
                  }
               }
            }
         }

         return dataChanged;
      }
   }

   private boolean updateFromOneBelowDetailLevel(FullDataSourceV2 inputDataSource, int[] remappedIds) {
      if (DhSectionPos.getDetailLevel(inputDataSource.pos) + 1 != DhSectionPos.getDetailLevel(this.pos)) {
         throw new IllegalArgumentException(
            "Input data source must be exactly 1 detail level below this data source. Expected ["
               + (DhSectionPos.getDetailLevel(this.pos) - 1)
               + "], received ["
               + DhSectionPos.getDetailLevel(inputDataSource.pos)
               + "]."
         );
      } else {
         int minChildXPos = DhSectionPos.getX(DhSectionPos.getChildByIndex(this.pos, 0));
         int recipientOffsetX = DhSectionPos.getX(inputDataSource.pos) == minChildXPos ? 0 : 32;
         int minChildZPos = DhSectionPos.getZ(DhSectionPos.getChildByIndex(this.pos, 0));
         int recipientOffsetZ = DhSectionPos.getZ(inputDataSource.pos) == minChildZPos ? 0 : 32;
         boolean dataChanged = false;

         for (int x = 0; x < 64; x += 2) {
            for (int z = 0; z < 64; z += 2) {
               int recipientX = x / 2 + recipientOffsetX;
               int recipientZ = z / 2 + recipientOffsetZ;
               int recipientIndex = relativePosToIndex(recipientX, recipientZ);
               byte inputGenStep = determineMinWorldGenStepForTwoByTwoColumn(inputDataSource.columnGenerationSteps, x, z);
               this.columnGenerationSteps.set(recipientIndex, inputGenStep);
               byte worldCompressionMode = determineHighestWorldCompressionForTwoByTwoColumn(inputDataSource.columnWorldCompressionMode, x, z);
               this.columnWorldCompressionMode.set(recipientIndex, worldCompressionMode);
               LongArrayList mergedInputDataArray = mergeInputTwoByTwoDataColumn(inputDataSource, x, z);
               if (this.dataPoints[recipientIndex] == null) {
                  dataChanged = true;
               } else if (this.dataPoints[recipientIndex].size() != mergedInputDataArray.size()) {
                  dataChanged = true;
               }

               int oldDataHash = 0;
               if (!dataChanged) {
                  oldDataHash = this.dataPoints[recipientIndex].hashCode();
               }

               this.dataPoints[recipientIndex] = mergedInputDataArray;
               this.remapDataColumn(recipientIndex, remappedIds);
               if (RUN_DATA_ORDER_VALIDATION) {
                  throwIfDataColumnInWrongOrder(inputDataSource.pos, this.dataPoints[recipientIndex]);
               }

               if (!dataChanged && oldDataHash != this.dataPoints[recipientIndex].hashCode()) {
                  dataChanged = true;
               }

               this.isEmpty = false;
            }
         }

         return dataChanged;
      }
   }

   private static byte determineMinWorldGenStepForTwoByTwoColumn(ByteArrayList columnGenerationSteps, int relX, int relZ) {
      byte minWorldGenStepValue = 127;

      for (int x = 0; x < 2; x++) {
         for (int z = 0; z < 2; z++) {
            int index = relativePosToIndex(x + relX, z + relZ);
            byte worldGenStepValue = columnGenerationSteps.getByte(index);
            minWorldGenStepValue = (byte)Math.min((int)minWorldGenStepValue, (int)worldGenStepValue);
         }
      }

      return minWorldGenStepValue;
   }

   private static byte determineHighestWorldCompressionForTwoByTwoColumn(ByteArrayList columnCompressionMode, int relX, int relZ) {
      byte minWorldGenStepValue = -128;

      for (int x = 0; x < 2; x++) {
         for (int z = 0; z < 2; z++) {
            int index = relativePosToIndex(x + relX, z + relZ);
            byte worldGenStepValue = columnCompressionMode.getByte(index);
            minWorldGenStepValue = (byte)Math.max((int)minWorldGenStepValue, (int)worldGenStepValue);
         }
      }

      return minWorldGenStepValue;
   }

   private static LongArrayList mergeInputTwoByTwoDataColumn(FullDataSourceV2 inputDataSource, int x, int z) {
      LongArrayList newColumnList = new LongArrayList();
      LongArrayList[] inputColumns = new LongArrayList[4];
      int colIndex = 0;

      for (int inputX = x; inputX < x + 2; inputX++) {
         for (int inputZ = z; inputZ < z + 2; colIndex++) {
            inputColumns[colIndex] = inputDataSource.dataPoints[relativePosToIndex(inputX, inputZ)];
            if (inputColumns[colIndex] != null && RUN_DATA_ORDER_VALIDATION) {
               throwIfDataColumnInWrongOrder(inputDataSource.pos, inputColumns[colIndex]);
            }

            inputZ++;
         }
      }

      IntArrayList yTransitions = new IntArrayList();

      for (int i = 0; i < 4; i++) {
         if (inputColumns[i] != null && !inputColumns[i].isEmpty()) {
            for (int j = 0; j < inputColumns[i].size(); j++) {
               long datapoint = inputColumns[i].getLong(j);
               int minY = FullDataPointUtil.getBottomY(datapoint);
               int maxY = minY + FullDataPointUtil.getHeight(datapoint);
               if (!yTransitions.contains(minY)) {
                  yTransitions.add(minY);
               }

               if (!yTransitions.contains(maxY)) {
                  yTransitions.add(maxY);
               }
            }
         }
      }

      if (yTransitions.isEmpty()) {
         return newColumnList;
      } else {
         yTransitions.sort(null);
         int[] currentIndices = new int[4];

         for (int ix = 0; ix < 4; ix++) {
            if (inputColumns[ix] != null && !inputColumns[ix].isEmpty()) {
               currentIndices[ix] = inputColumns[ix].size() - 1;
            } else {
               currentIndices[ix] = -1;
            }
         }

         int lastId = 0;
         byte lastBlockLight = 0;
         byte lastSkyLight = 0;
         int currentMinY = yTransitions.getInt(0);
         int accumulatedHeight = 0;
         int[] mergeIds = new int[4];
         int[] mergeBlockLights = new int[4];
         int[] mergeSkyLights = new int[4];

         for (int yIndex = 0; yIndex < yTransitions.size() - 1; yIndex++) {
            int sliceMinY = yTransitions.getInt(yIndex);
            int sliceMaxY = yTransitions.getInt(yIndex + 1);
            int sliceHeight = sliceMaxY - sliceMinY;
            int sampleY = sliceMinY + sliceHeight / 2;
            Arrays.fill(mergeIds, 0);
            Arrays.fill(mergeBlockLights, 0);
            Arrays.fill(mergeSkyLights, 0);

            for (int ixx = 0; ixx < 4; ixx++) {
               if (currentIndices[ixx] != -1) {
                  LongArrayList column = inputColumns[ixx];
                  if (column != null) {
                     while (currentIndices[ixx] >= 0) {
                        long datapointx = column.getLong(currentIndices[ixx]);
                        int inputMinY = FullDataPointUtil.getBottomY(datapointx);
                        int inputMaxY = inputMinY + FullDataPointUtil.getHeight(datapointx);
                        if (sampleY < inputMaxY) {
                           if (sampleY >= inputMinY && sampleY < inputMaxY) {
                              mergeIds[ixx] = FullDataPointUtil.getId(datapointx);
                              mergeBlockLights[ixx] = FullDataPointUtil.getBlockLight(datapointx);
                              mergeSkyLights[ixx] = FullDataPointUtil.getSkyLight(datapointx);
                           }
                           break;
                        }

                        currentIndices[ixx]--;
                     }
                  }
               }
            }

            int id = determineMostCommonValueInColumnSlice(mergeIds, inputDataSource.mapping);
            byte blockLight = (byte)determineAverageValueInColumnSlice(mergeBlockLights);
            byte skyLight = (byte)determineAverageValueInColumnSlice(mergeSkyLights);
            if (accumulatedHeight == 0) {
               lastId = id;
               lastBlockLight = blockLight;
               lastSkyLight = skyLight;
               currentMinY = sliceMinY;
               accumulatedHeight = sliceHeight;
            } else if (id == lastId && blockLight == lastBlockLight && skyLight == lastSkyLight) {
               accumulatedHeight += sliceHeight;
            } else {
               try {
                  long datapointx = FullDataPointUtil.encode(lastId, accumulatedHeight, currentMinY, lastBlockLight, lastSkyLight);
                  newColumnList.add(datapointx);
               } catch (DataCorruptedException var28) {
                  LOGGER.warn(
                     "Skipping corrupt datapoint for pos ["
                        + DhSectionPos.toString(inputDataSource.pos)
                        + "] at relative position ["
                        + x
                        + ","
                        + z
                        + "] with data: ID["
                        + lastId
                        + "], Height["
                        + accumulatedHeight
                        + "], minY["
                        + currentMinY
                        + "], lastBlockLight["
                        + lastBlockLight
                        + "], lastSkyLight["
                        + lastSkyLight
                        + "]."
                  );
               }

               lastId = id;
               lastBlockLight = blockLight;
               lastSkyLight = skyLight;
               currentMinY = sliceMinY;
               accumulatedHeight = sliceHeight;
            }
         }

         if (accumulatedHeight > 0) {
            try {
               newColumnList.add(FullDataPointUtil.encode(lastId, accumulatedHeight, currentMinY, lastBlockLight, lastSkyLight));
            } catch (DataCorruptedException var27) {
               LOGGER.warn(
                  "Skipping corrupt datapoint for pos ["
                     + DhSectionPos.toString(inputDataSource.pos)
                     + "] at relative position ["
                     + x
                     + ","
                     + z
                     + "] with data: ID["
                     + lastId
                     + "], Height["
                     + accumulatedHeight
                     + "], minY["
                     + currentMinY
                     + "], lastBlockLight["
                     + lastBlockLight
                     + "], lastSkyLight["
                     + lastSkyLight
                     + "]."
               );
            }
         }

         ensureDataColumnOrder(newColumnList);
         return newColumnList;
      }
   }

   private void remapDataColumn(int dataPointIndex, int[] remappedIds) {
      LongArrayList dataColumn = this.dataPoints[dataPointIndex];

      for (int i = 0; i < dataColumn.size(); i++) {
         dataColumn.set(i, FullDataPointUtil.remap(remappedIds, dataColumn.getLong(i)));
      }
   }

   private static int determineMostCommonValueInColumnSlice(int[] sliceArray, @Nullable FullDataPointIdMap mapping) {
      int value0 = sliceArray[0];
      int count0 = 0;
      int value1 = sliceArray[1];
      int count1 = 0;
      int value2 = sliceArray[2];
      int count2 = 0;
      int value3 = sliceArray[3];
      int count3 = 0;

      for (int i = 0; i < 4; i++) {
         int value = sliceArray[i];
         if (mapping == null || !mapping.getBlockStateWrapper(value).isAir()) {
            if (value == value0) {
               count0++;
            } else if (value == value1) {
               count1++;
            } else if (value == value2) {
               count2++;
            } else {
               count3++;
            }
         }
      }

      int maxCount = Math.max(count0, Math.max(count1, Math.max(count2, count3)));
      if (maxCount == count0) {
         return value0;
      } else if (maxCount == count1) {
         return value1;
      } else {
         return maxCount == count2 ? value2 : value3;
      }
   }

   private static int determineAverageValueInColumnSlice(int[] sliceArray) {
      int value = 0;

      for (int i = 0; i < 4; i++) {
         value += sliceArray[i];
      }

      return value / 4;
   }

   public boolean downsampleFromOneAboveDetailLevel(FullDataSourceV2 inputDataSource, int[] remappedIds) {
      if (DhSectionPos.getDetailLevel(inputDataSource.pos) - 1 != DhSectionPos.getDetailLevel(this.pos)) {
         throw new IllegalArgumentException(
            "Input data source must be exactly 1 detail level above this data source. Expected ["
               + (DhSectionPos.getDetailLevel(this.pos) - 1)
               + "], received ["
               + DhSectionPos.getDetailLevel(inputDataSource.pos)
               + "]."
         );
      } else {
         int minParentXPos = DhSectionPos.getX(DhSectionPos.getChildByIndex(inputDataSource.pos, 0));
         int inputOffsetX = DhSectionPos.getX(this.pos) == minParentXPos ? 0 : 32;
         int minParentZPos = DhSectionPos.getZ(DhSectionPos.getChildByIndex(inputDataSource.pos, 0));
         int inputOffsetZ = DhSectionPos.getZ(this.pos) == minParentZPos ? 0 : 32;
         boolean dataChanged = false;

         for (int x = 0; x < 64; x++) {
            for (int z = 0; z < 64; z++) {
               int recipientIndex = relativePosToIndex(x, z);
               int inputX = x / 2 + inputOffsetX;
               int inputZ = z / 2 + inputOffsetZ;
               int inputIndex = relativePosToIndex(inputX, inputZ);
               byte inputGenStep = EDhApiWorldGenerationStep.DOWN_SAMPLED.value;
               this.columnGenerationSteps.set(recipientIndex, inputGenStep);
               byte worldCompressionMode = inputDataSource.columnWorldCompressionMode.getByte(recipientIndex);
               this.columnWorldCompressionMode.set(recipientIndex, worldCompressionMode);
               boolean downSampleColumn;
               if (this.dataPoints[recipientIndex] == null) {
                  downSampleColumn = true;
               } else {
                  downSampleColumn = true;
                  LongListIterator inputDataArray = this.dataPoints[recipientIndex].iterator();

                  while (inputDataArray.hasNext()) {
                     long dataPoint = (Long)inputDataArray.next();
                     if (dataPoint != 0L) {
                        downSampleColumn = false;
                        break;
                     }
                  }
               }

               if (downSampleColumn) {
                  LongArrayList inputDataArray = inputDataSource.dataPoints[inputIndex];
                  this.dataPoints[recipientIndex] = inputDataArray;
                  this.remapDataColumn(recipientIndex, remappedIds);
                  if (RUN_DATA_ORDER_VALIDATION) {
                     throwIfDataColumnInWrongOrder(inputDataSource.pos, this.dataPoints[recipientIndex]);
                  }

                  dataChanged = true;
               }

               this.isEmpty = false;
            }
         }

         return dataChanged;
      }
   }

   private void removeUnusedIdsAndRemap() {
      Int2IntOpenHashMap newIdByOldId = new Int2IntOpenHashMap();
      FullDataPointIdMap newMap = DATA_MAP_FOR_REMAPPING_REF.get();
      newMap.clear(this.pos);

      for (int x = 0; x < 64; x++) {
         for (int z = 0; z < 64; z++) {
            int index = relativePosToIndex(x, z);
            LongArrayList dataColumn = this.dataPoints[index];

            for (int i = 0; i < dataColumn.size(); i++) {
               long dataPoint = dataColumn.getLong(i);
               int oldId = FullDataPointUtil.getId(dataPoint);
               int newId = newMap.addIfNotPresentAndGetId(this.mapping.getBiomeWrapper(oldId), this.mapping.getBlockStateWrapper(oldId));
               newIdByOldId.put(oldId, newId);
            }
         }
      }

      this.mapping.clear(this.pos);
      this.mapping.addAll(newMap);

      for (int x = 0; x < 64; x++) {
         for (int z = 0; z < 64; z++) {
            int index = relativePosToIndex(x, z);
            LongArrayList dataColumn = this.dataPoints[index];

            for (int i = 0; i < dataColumn.size(); i++) {
               long oldDataPoint = dataColumn.getLong(i);
               int oldId = FullDataPointUtil.getId(oldDataPoint);
               int newId = newIdByOldId.get(oldId);
               long newDataPoint = FullDataPointUtil.setId(oldDataPoint, newId);
               dataColumn.set(i, newDataPoint);
            }
         }
      }
   }

   public void clearAllNonAdjData(EDhDirection direction) {
      long encodedMinMaxPos = FullDataMinMaxPosUtil.getEncodedMinMaxPos(direction);
      int minX = FullDataMinMaxPosUtil.getAdjMinX(encodedMinMaxPos);
      int maxX = FullDataMinMaxPosUtil.getAdjMaxX(encodedMinMaxPos);
      int minZ = FullDataMinMaxPosUtil.getAdjMinZ(encodedMinMaxPos);
      int maxZ = FullDataMinMaxPosUtil.getAdjMaxZ(encodedMinMaxPos);

      for (int relX = 0; relX < 64; relX++) {
         for (int relZ = 0; relZ < 64; relZ++) {
            if (relX < minX || relX >= maxX || relZ < minZ || relZ >= maxZ) {
               LongArrayList dataColumn = this.getColumnAtRelPos(relX, relZ);
               dataColumn.clear();
               dataColumn.add(0L);
            }
         }
      }
   }

   public static int tryGetRelativePosToIndex(int relX, int relZ) {
      return relX >= 0 && relZ >= 0 && relX < 64 && relZ < 64 ? relX * 64 + relZ : -1;
   }

   public static int relativePosToIndex(int relX, int relZ) throws IndexOutOfBoundsException {
      int index = tryGetRelativePosToIndex(relX, relZ);
      if (index < 0) {
         throw new IndexOutOfBoundsException(
            "Relative data source positions must be between [0] (inclusive) and [64] (exclusive) the relative pos: ["
               + relX
               + ","
               + relZ
               + "] is outside those boundaries."
         );
      } else {
         return index;
      }
   }

   public static void throwIfDataColumnInWrongOrder(long pos, LongArrayList dataArray) throws IllegalStateException {
      if (dataArray.size() >= 2) {
         long firstDataPoint = dataArray.getLong(0);
         int firstBottomY = FullDataPointUtil.getBottomY(firstDataPoint);
         long lastDataPoint = dataArray.getLong(dataArray.size() - 1);
         int lastBottomY = FullDataPointUtil.getBottomY(lastDataPoint);
         if (firstBottomY < lastBottomY) {
            throw new IllegalStateException(
               "Incorrect data point order at pos: ["
                  + DhSectionPos.toString(pos)
                  + "], first datapoint bottom Y ["
                  + firstBottomY
                  + "], last datapoint bottom Y ["
                  + lastBottomY
                  + "]."
            );
         }
      }
   }

   private static void ensureDataColumnOrder(LongArrayList dataColumn) {
      if (dataColumn.size() >= 2) {
         long firstDataPoint = dataColumn.getLong(0);
         int firstBottomY = FullDataPointUtil.getBottomY(firstDataPoint);
         long lastDataPoint = dataColumn.getLong(dataColumn.size() - 1);
         int lastBottomY = FullDataPointUtil.getBottomY(lastDataPoint);
         if (firstBottomY < lastBottomY) {
            for (int i = 0; i < dataColumn.size() / 2; i++) {
               long temp = dataColumn.getLong(i);
               dataColumn.set(i, dataColumn.getLong(dataColumn.size() - i - 1));
               dataColumn.set(dataColumn.size() - i - 1, temp);
            }
         }
      }
   }

   public long getPos() {
      return this.pos;
   }

   public byte getDataDetailLevel() {
      return (byte)(DhSectionPos.getDetailLevel(this.pos) - 6);
   }

   public void setSingleColumn(
      LongArrayList longArray, int relX, int relZ, EDhApiWorldGenerationStep worldGenStep, EDhApiWorldCompressionMode worldCompressionMode
   ) {
      int index = relativePosToIndex(relX, relZ);
      this.dataPoints[index] = longArray;
      this.columnGenerationSteps.set(index, worldGenStep.value);
      this.columnWorldCompressionMode.set(index, worldCompressionMode.value);
   }

   public void setRunApiSetterValidation(boolean runValidation) {
      this.runApiSetterValidation = runValidation;
   }

   @Override
   public int getWidthInDataColumns() {
      return 64;
   }

   @Override
   public List<DhApiTerrainDataPoint> setApiDataPointColumn(int relX, int relZ, List<DhApiTerrainDataPoint> columnDataPoints) throws IndexOutOfBoundsException, IllegalArgumentException {
      try {
         LodDataBuilder.putListInTopDownOrder(columnDataPoints);
         if (this.runApiSetterValidation) {
            LodDataBuilder.validateOrThrowApiDataColumn(columnDataPoints);
         }

         LongArrayList packedDataPoints = LodDataBuilder.convertApiDataPointListToPackedLongArray(columnDataPoints, this, 0, true);
         this.setSingleColumn(packedDataPoints, relX, relZ, EDhApiWorldGenerationStep.SURFACE, EDhApiWorldCompressionMode.MERGE_SAME_BLOCKS);
         return columnDataPoints;
      } catch (DataCorruptedException var5) {
         throw new IllegalArgumentException(var5.getMessage(), var5);
      }
   }

   @Override
   public List<DhApiTerrainDataPoint> getApiDataPointColumn(int relX, int relZ) throws IndexOutOfBoundsException {
      LongArrayList dataColumn = this.getColumnAtRelPos(relX, relZ);
      ArrayList<DhApiTerrainDataPoint> apiList = new ArrayList<>();

      for (int i = 0; i < dataColumn.size(); i++) {
         long datapoint = dataColumn.getLong(i);
         DhApiTerrainDataPoint apiDataPoint = DhApiTerrainDataPointUtil.createApiDatapoint(0, this.mapping, DhSectionPos.getDetailLevel(this.pos), datapoint);
         apiList.add(apiDataPoint);
      }

      return apiList;
   }

   public PhantomArrayListCheckout getPhantomArrayCheckoutForUnitTesting() {
      return this.pooledArraysCheckout;
   }

   @Override
   public String toString() {
      return DhSectionPos.toString(this.pos);
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode == 0) {
         this.generateHashCode();
      }

      return this.cachedHashCode;
   }

   private void generateHashCode() {
      int result = DhSectionPos.hashCode(this.pos);
      result = 31 * result + Arrays.deepHashCode(this.dataPoints);
      result = 17 * result + this.columnGenerationSteps.hashCode();
      result = 43 * result + this.columnWorldCompressionMode.hashCode();
      this.cachedHashCode = result;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof FullDataSourceV2)) {
         return false;
      } else {
         FullDataSourceV2 other = (FullDataSourceV2)obj;
         return other.pos != this.pos ? false : other.hashCode() == this.hashCode();
      }
   }
}
