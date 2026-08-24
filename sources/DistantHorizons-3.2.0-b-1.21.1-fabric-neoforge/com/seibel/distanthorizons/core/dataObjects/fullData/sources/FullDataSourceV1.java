package com.seibel.distanthorizons.core.dataObjects.fullData.sources;

import com.seibel.distanthorizons.api.enums.worldGeneration.EDhApiWorldGenerationStep;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.level.IDhLevel;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.dto.FullDataSourceV1DTO;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataInputStream;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataOutputStream;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import java.io.IOException;
import java.util.Arrays;

public class FullDataSourceV1 {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final byte SECTION_SIZE_OFFSET = 6;
   public static final int WIDTH = BitShiftUtil.powerOfTwo(6);
   public static final byte DATA_FORMAT_VERSION = 3;
   public static final String DATA_TYPE_NAME = "CompleteFullDataSource";
   private static final int DATA_GUARD_BYTE = -1;
   private static final int NO_DATA_FLAG_BYTE = 1;
   public final FullDataPointIdMap mapping;
   public EDhApiWorldGenerationStep worldGenStep = EDhApiWorldGenerationStep.EMPTY;
   private final long[][] dataArrays;
   private long pos;
   private boolean isEmpty = true;

   public static FullDataSourceV1 createEmpty(long pos) {
      return new FullDataSourceV1(pos);
   }

   private FullDataSourceV1(long pos) {
      this.dataArrays = new long[WIDTH * WIDTH][0];
      this.mapping = new FullDataPointIdMap(pos);
      this.pos = pos;
   }

   public Long getKey() {
      return this.pos;
   }

   public String getKeyDisplayString() {
      return DhSectionPos.toString(this.pos);
   }

   public long getPos() {
      return this.pos;
   }

   public void resizeDataStructuresForRepopulation(long pos) {
      this.pos = pos;
   }

   public byte getDataDetailLevel() {
      return (byte)(DhSectionPos.getDetailLevel(this.pos) - 6);
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public long[] get(int index) {
      return this.get(index / WIDTH, index % WIDTH);
   }

   public long[] get(int relativeX, int relativeZ) {
      int dataArrayIndex = relativeX * WIDTH + relativeZ;
      if (dataArrayIndex >= this.dataArrays.length) {
         LodUtil.assertNotReach(
            "FullDataArrayAccessor.get() called with a relative position that is outside the data source. \ngiven relative pos X: ["
               + relativeX
               + "] Z: ["
               + relativeZ
               + "]\ndataArrays.length: ["
               + this.dataArrays.length
               + "] dataArrayIndex: ["
               + dataArrayIndex
               + "]."
         );
      }

      return this.dataArrays[dataArrayIndex];
   }

   public void repopulateFromStream(FullDataSourceV1DTO dto, DhDataInputStream inputStream, IDhLevel level) throws IOException, InterruptedException, DataCorruptedException {
      this.resizeDataStructuresForRepopulation(dto.pos);
      this.mapping.clear(dto.pos);
      this.populateFromStream(dto, inputStream, level);
   }

   public void populateFromStream(FullDataSourceV1DTO dto, DhDataInputStream inputStream, IDhLevel level) throws IOException, InterruptedException, DataCorruptedException {
      FullDataSourceV1.FullDataSourceSummaryData summaryData = this.readSourceSummaryInfo(dto, inputStream, level);
      this.setSourceSummaryData(summaryData);
      long[][] dataPoints = this.readDataPoints(summaryData.dataWidth, inputStream);
      if (dataPoints != null) {
         this.setDataPoints(dataPoints);
         this.readIdMappings(this.mapping, inputStream, level.getLevelWrapper());
      }
   }

   @Deprecated
   public void writeSourceSummaryInfo(IDhLevel level, DhDataOutputStream outputStream) throws IOException {
      outputStream.writeInt(this.getDataDetailLevel());
      outputStream.writeInt(WIDTH);
      outputStream.writeInt(level.getLevelWrapper().getMinHeight());
      outputStream.writeByte(this.worldGenStep.value);
   }

   public FullDataSourceV1.FullDataSourceSummaryData readSourceSummaryInfo(FullDataSourceV1DTO dto, DhDataInputStream inputStream, IDhLevel level) throws IOException {
      int dataDetail = inputStream.readInt();
      if (dataDetail != dto.dataDetailLevel) {
         throw new IOException("Data level mismatch. Expected: [" + dto.dataDetailLevel + "], found [" + dataDetail + "].");
      } else {
         int width = inputStream.readInt();
         if (width != WIDTH) {
            throw new IOException("Section width mismatch: [" + width + "] != [" + WIDTH + "] (Currently only 1 section width is supported)");
         } else {
            int minY = inputStream.readInt();
            if (minY != level.getLevelWrapper().getMinHeight()) {
               LOGGER.warn("Data minY mismatch: [" + minY + "] != [" + level.getLevelWrapper().getMinHeight() + "]. Will ignore data's y level");
            }

            byte worldGenByte = inputStream.readByte();
            EDhApiWorldGenerationStep worldGenStep = EDhApiWorldGenerationStep.fromValue(worldGenByte);
            if (worldGenStep == null) {
               worldGenStep = EDhApiWorldGenerationStep.SURFACE;
               LOGGER.warn("Missing WorldGenStep, defaulting to: " + worldGenStep.name());
            }

            return new FullDataSourceV1.FullDataSourceSummaryData(width, worldGenStep);
         }
      }
   }

   public void setSourceSummaryData(FullDataSourceV1.FullDataSourceSummaryData summaryData) {
      this.worldGenStep = summaryData.worldGenStep;
   }

   @Deprecated
   public boolean writeDataPoints(DhDataOutputStream outputStream) throws IOException {
      if (this.isEmpty()) {
         outputStream.writeInt(1);
         return false;
      } else {
         outputStream.writeInt(-1);

         for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
               outputStream.writeInt(this.get(x, z).length);
            }
         }

         outputStream.writeInt(-1);

         for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
               long[] dataColumn = this.get(x, z);
               if (dataColumn != null) {
                  for (long dataPoint : dataColumn) {
                     outputStream.writeLong(dataPoint);
                  }
               }
            }
         }

         return true;
      }
   }

   public long[][] readDataPoints(int width, DhDataInputStream dataInputStream) throws IOException {
      int dataPresentFlag = dataInputStream.readInt();
      if (dataPresentFlag == 1) {
         return null;
      } else if (dataPresentFlag != -1) {
         throw new IOException(
            "Invalid file format. Data Points guard byte expected: (no data) [1] or (data present) [-1], but found [" + dataPresentFlag + "]."
         );
      } else {
         long[][] dataPointArrays;
         if (WIDTH == width) {
            dataPointArrays = this.dataArrays;
         } else {
            dataPointArrays = new long[width * width][];
         }

         for (int x = 0; x < width; x++) {
            for (int z = 0; z < width; z++) {
               int requestedArrayLength = dataInputStream.readInt();
               int arrayIndex = x * width + z;
               if (dataPointArrays[arrayIndex] != null && dataPointArrays[arrayIndex].length == requestedArrayLength) {
                  Arrays.fill(dataPointArrays[arrayIndex], 0L);
               } else {
                  dataPointArrays[arrayIndex] = new long[requestedArrayLength];
               }
            }
         }

         int arrayStartFlag = dataInputStream.readInt();
         if (arrayStartFlag != -1) {
            throw new IOException("invalid data length end guard");
         } else {
            for (int xz = 0; xz < dataPointArrays.length; xz++) {
               if (dataPointArrays[xz].length != 0) {
                  for (int y = 0; y < dataPointArrays[xz].length; y++) {
                     dataPointArrays[xz][y] = dataInputStream.readLong();
                  }
               }
            }

            return dataPointArrays;
         }
      }
   }

   public void setDataPoints(long[][] dataPoints) {
      LodUtil.assertTrue(this.dataArrays.length == dataPoints.length, "Data point array length mismatch.");
      this.isEmpty = false;
      System.arraycopy(dataPoints, 0, this.dataArrays, 0, dataPoints.length);
   }

   @Deprecated
   public void writeIdMappings(DhDataOutputStream outputStream) throws IOException {
      outputStream.writeInt(-1);
      this.mapping.serialize(outputStream);
   }

   public void readIdMappings(FullDataPointIdMap map, DhDataInputStream inputStream, ILevelWrapper levelWrapper) throws IOException, InterruptedException, DataCorruptedException {
      int guardByte = inputStream.readInt();
      if (guardByte != -1) {
         throw new IOException("Invalid data content end guard for ID mapping");
      } else {
         FullDataPointIdMap.deserialize(map, inputStream, this.pos, levelWrapper);
      }
   }

   private static class FullDataSourceSummaryData {
      public final int dataWidth;
      public EDhApiWorldGenerationStep worldGenStep;

      public FullDataSourceSummaryData(int dataWidth, EDhApiWorldGenerationStep worldGenStep) {
         this.dataWidth = dataWidth;
         this.worldGenStep = worldGenStep;
      }
   }
}
