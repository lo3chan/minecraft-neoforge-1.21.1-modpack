package com.seibel.distanthorizons.core.sql.dto;

import com.google.common.base.MoreObjects;
import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.network.INetworkObject;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.sql.dto.util.FullDataMinMaxPosUtil;
import com.seibel.distanthorizons.core.sql.dto.util.VarintUtil;
import com.seibel.distanthorizons.core.util.BoolUtil;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.util.ListUtil;
import com.seibel.distanthorizons.core.util.objects.DataCorruptedException;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataInputStream;
import com.seibel.distanthorizons.core.util.objects.dataStreams.DhDataOutputStream;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.AbstractPhantomArrayList;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListPool;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.ILevelWrapper;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.EOFException;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullDataSourceV2DTO extends AbstractPhantomArrayList implements IBaseDTO<Long>, INetworkObject, AutoCloseable {
   public static final boolean VALIDATE_INPUT_DATAPOINTS = true;
   public static final PhantomArrayListPool ARRAY_LIST_POOL = new PhantomArrayListPool("V2DTO");
   public long pos;
   public int dataChecksum;
   public ByteArrayList compressedDataByteArray = this.pooledArraysCheckout.getByteArray(0, 0);
   public ByteArrayList compressedNorthAdjDataByteArray;
   public ByteArrayList compressedSouthAdjDataByteArray;
   public ByteArrayList compressedEastAdjDataByteArray;
   public ByteArrayList compressedWestAdjDataByteArray;
   public ByteArrayList compressedColumnGenStepByteArray = this.pooledArraysCheckout.getByteArray(1, 0);
   public ByteArrayList compressedWorldCompressionModeByteArray = this.pooledArraysCheckout.getByteArray(2, 0);
   public ByteArrayList compressedMappingByteArray = this.pooledArraysCheckout.getByteArray(3, 0);
   public byte dataFormatVersion;
   public byte compressionModeValue;
   @Nullable
   public Boolean applyToParent;
   @Nullable
   public Boolean applyToChildren;
   public long lastModifiedUnixDateTime;
   public long createdUnixDateTime;

   public static FullDataSourceV2DTO CreateFromDataSource(FullDataSourceV2 dataSource, EDhApiDataCompressionMode compressionModeEnum) throws IOException {
      FullDataSourceV2DTO dto = CreateEmptyDataSourceForDecoding();
      writeDataSourceDataArrayToBlobV2(dataSource.dataPoints, dto.compressedDataByteArray, null, compressionModeEnum);
      writeGenerationStepsToBlob(dataSource.columnGenerationSteps, dto.compressedColumnGenStepByteArray, compressionModeEnum);
      writeWorldCompressionModeToBlob(dataSource.columnWorldCompressionMode, dto.compressedWorldCompressionModeByteArray, compressionModeEnum);
      writeDataMappingToBlob(dataSource.mapping, dto.compressedMappingByteArray, compressionModeEnum);
      writeDataSourceDataArrayToBlobV2(dataSource.dataPoints, dto.compressedNorthAdjDataByteArray, EDhDirection.NORTH, compressionModeEnum);
      writeDataSourceDataArrayToBlobV2(dataSource.dataPoints, dto.compressedSouthAdjDataByteArray, EDhDirection.SOUTH, compressionModeEnum);
      writeDataSourceDataArrayToBlobV2(dataSource.dataPoints, dto.compressedEastAdjDataByteArray, EDhDirection.EAST, compressionModeEnum);
      writeDataSourceDataArrayToBlobV2(dataSource.dataPoints, dto.compressedWestAdjDataByteArray, EDhDirection.WEST, compressionModeEnum);
      dto.pos = dataSource.getPos();
      dto.dataChecksum = dataSource.hashCode();
      dto.dataFormatVersion = 2;
      dto.compressionModeValue = compressionModeEnum.value;
      dto.lastModifiedUnixDateTime = dataSource.lastModifiedUnixDateTime;
      dto.createdUnixDateTime = dataSource.createdUnixDateTime;
      dto.applyToParent = dataSource.applyToParent;
      dto.applyToChildren = dataSource.applyToChildren;
      return dto;
   }

   public static FullDataSourceV2DTO CreateEmptyDataSourceForDecoding() {
      return new FullDataSourceV2DTO();
   }

   private FullDataSourceV2DTO() {
      super(ARRAY_LIST_POOL, 8, 0, 0, 0, 0);
      this.compressedNorthAdjDataByteArray = this.pooledArraysCheckout.getByteArray(4, 0);
      this.compressedSouthAdjDataByteArray = this.pooledArraysCheckout.getByteArray(5, 0);
      this.compressedEastAdjDataByteArray = this.pooledArraysCheckout.getByteArray(6, 0);
      this.compressedWestAdjDataByteArray = this.pooledArraysCheckout.getByteArray(7, 0);
   }

   public FullDataSourceV2 createDataSource(@NotNull ILevelWrapper levelWrapper, EDhDirection direction) throws IOException, InterruptedException, DataCorruptedException {
      FullDataSourceV2 dataSource = FullDataSourceV2.createEmpty(this.pos);

      try {
         this.populateDataSource(dataSource, levelWrapper, direction, false);
         return dataSource;
      } catch (Exception var5) {
         dataSource.close();
         throw var5;
      }
   }

   public FullDataSourceV2 createUnitTestDataSource() throws IOException, InterruptedException, DataCorruptedException {
      return this.createUnitTestDataSource(null);
   }

   public FullDataSourceV2 createUnitTestDataSource(EDhDirection direction) throws IOException, InterruptedException, DataCorruptedException {
      return this.populateDataSource(FullDataSourceV2.createEmpty(this.pos), null, direction, true);
   }

   private FullDataSourceV2 populateDataSource(FullDataSourceV2 dataSource, ILevelWrapper levelWrapper, @Nullable EDhDirection direction, boolean unitTest) throws IOException, InterruptedException, DataCorruptedException {
      if (this.dataFormatVersion != 1 && this.dataFormatVersion != 2) {
         throw new IllegalStateException("Data source population only supports formats: [1,2], data format found: [" + this.dataFormatVersion + "].");
      } else if (direction != null && this.dataFormatVersion == 1) {
         throw new IllegalStateException("Data format [" + this.dataFormatVersion + "] doesn't support adjacent data. Automatic conversion must be done.");
      } else {
         EDhApiDataCompressionMode compressionModeEnum;
         try {
            compressionModeEnum = EDhApiDataCompressionMode.getFromValue(this.compressionModeValue);
         } catch (IllegalArgumentException var8) {
            throw new DataCorruptedException(var8);
         }

         for (int i = 0; i < 4096; i++) {
            LongArrayList array = dataSource.dataPoints[i];
            array.clear();
            array.add(0L);
         }

         if (direction == null) {
            readBlobToGenerationSteps(this.compressedColumnGenStepByteArray, dataSource.columnGenerationSteps, compressionModeEnum);
            readBlobToWorldCompressionMode(this.compressedWorldCompressionModeByteArray, dataSource.columnWorldCompressionMode, compressionModeEnum);
            if (this.dataFormatVersion == 1) {
               readBlobToDataSourceDataArrayV1(this.compressedDataByteArray, dataSource.dataPoints, compressionModeEnum);
            } else {
               readBlobToDataSourceDataArrayV2(this.compressedDataByteArray, dataSource.dataPoints, null, compressionModeEnum);
               readBlobToDataSourceDataArrayV2(this.compressedNorthAdjDataByteArray, dataSource.dataPoints, EDhDirection.NORTH, compressionModeEnum);
               readBlobToDataSourceDataArrayV2(this.compressedSouthAdjDataByteArray, dataSource.dataPoints, EDhDirection.SOUTH, compressionModeEnum);
               readBlobToDataSourceDataArrayV2(this.compressedEastAdjDataByteArray, dataSource.dataPoints, EDhDirection.EAST, compressionModeEnum);
               readBlobToDataSourceDataArrayV2(this.compressedWestAdjDataByteArray, dataSource.dataPoints, EDhDirection.WEST, compressionModeEnum);
            }
         } else {
            readBlobToDataSourceDataArrayV2(this.compressedDataByteArray, dataSource.dataPoints, direction, compressionModeEnum);
         }

         dataSource.mapping.clear(dataSource.getPos());
         if (!unitTest) {
            if (levelWrapper == null) {
               throw new NullPointerException("No level wrapper present, unable to deserialize data map. This should only be used for unit tests.");
            }

            readBlobToDataMapping(dataSource.mapping, this.compressedMappingByteArray, dataSource.getPos(), levelWrapper, compressionModeEnum);
         }

         dataSource.lastModifiedUnixDateTime = this.lastModifiedUnixDateTime;
         dataSource.createdUnixDateTime = this.createdUnixDateTime;
         dataSource.isEmpty = false;
         if (this.applyToParent != null) {
            dataSource.applyToParent = this.applyToParent;
         }

         if (this.applyToChildren != null) {
            dataSource.applyToChildren = this.applyToChildren;
         }

         return dataSource;
      }
   }

   public static void writeDataSourceDataArrayToBlobV1(
      LongArrayList[] inputDataArray, ByteArrayList outputByteArray, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException {
      DhDataOutputStream compressedOut = DhDataOutputStream.create(compressionModeEnum, outputByteArray);

      try {
         int dataArrayLength = 4096;

         for (int xz = 0; xz < dataArrayLength; xz++) {
            LongArrayList dataColumn = inputDataArray[xz];
            short columnLength = dataColumn != null ? (short)dataColumn.size() : 0;
            compressedOut.writeShort(columnLength);

            for (int y = 0; y < columnLength; y++) {
               compressedOut.writeLong(dataColumn.getLong(y));
            }
         }
      } catch (Throwable var10) {
         if (compressedOut != null) {
            try {
               compressedOut.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (compressedOut != null) {
         compressedOut.close();
      }
   }

   private static void readBlobToDataSourceDataArrayV1(
      ByteArrayList inputCompressedDataByteArray, LongArrayList[] outputDataLongArray, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException, DataCorruptedException {
      PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

      try {
         DhDataInputStream compressedIn = DhDataInputStream.create(inputCompressedDataByteArray, compressionModeEnum, checkout);

         try {
            int dataArrayLength = 4096;

            for (int xz = 0; xz < dataArrayLength; xz++) {
               short dataColumnLength = compressedIn.readShort();
               if (dataColumnLength < 0) {
                  throw new DataCorruptedException(
                     "Read DataSource Blob data at index [" + xz + "], column length [" + dataColumnLength + "] should be greater than zero."
                  );
               }

               LongArrayList dataColumn = outputDataLongArray[xz];
               ListUtil.clearAndSetSize(dataColumn, dataColumnLength);

               for (int y = 0; y < dataColumnLength; y++) {
                  long dataPoint = compressedIn.readLong();
                  FullDataPointUtil.validateDatapoint(dataPoint);
                  dataColumn.set(y, dataPoint);
               }
            }
         } catch (Throwable var14) {
            if (compressedIn != null) {
               try {
                  compressedIn.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }
            }

            throw var14;
         }

         if (compressedIn != null) {
            compressedIn.close();
         }
      } catch (Throwable var15) {
         if (checkout != null) {
            try {
               checkout.close();
            } catch (Throwable var12) {
               var15.addSuppressed(var12);
            }
         }

         throw var15;
      }

      if (checkout != null) {
         checkout.close();
      }
   }

   private static void writeDataSourceDataArrayToBlobV2(
      LongArrayList[] inputDataArray, ByteArrayList outputByteArray, @Nullable EDhDirection direction, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException {
      int minX;
      int maxX;
      int minZ;
      int maxZ;
      if (direction != null) {
         long encodedMinMaxPos = FullDataMinMaxPosUtil.getEncodedMinMaxPos(direction);
         minX = FullDataMinMaxPosUtil.getAdjMinX(encodedMinMaxPos);
         maxX = FullDataMinMaxPosUtil.getAdjMaxX(encodedMinMaxPos);
         minZ = FullDataMinMaxPosUtil.getAdjMinZ(encodedMinMaxPos);
         maxZ = FullDataMinMaxPosUtil.getAdjMaxZ(encodedMinMaxPos);
      } else {
         minX = 1;
         maxX = 63;
         minZ = 1;
         maxZ = 63;
      }

      DhDataOutputStream compressedOut = DhDataOutputStream.create(compressionModeEnum, outputByteArray);

      try {
         for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
               int index = FullDataSourceV2.relativePosToIndex(x, z);
               LongArrayList col = inputDataArray[index];
               int size = col != null ? col.size() : 0;
               VarintUtil.writeVarint(compressedOut, size);
            }
         }

         int previousBottomY = 0;

         for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
               int index = FullDataSourceV2.relativePosToIndex(x, z);
               LongArrayList col = inputDataArray[index];
               int size = col != null ? col.size() : 0;

               for (int y = 0; y < size; y++) {
                  long data = col.getLong(y);
                  int id = FullDataPointUtil.getId(data);
                  int height = FullDataPointUtil.getHeight(data);
                  int bottomY = FullDataPointUtil.getBottomY(data);
                  boolean hasLight = (FullDataPointUtil.getBlockLight(data) | FullDataPointUtil.getSkyLight(data)) != 0;
                  int expectedBottomY = previousBottomY - height;
                  boolean hasDiscontinuity = bottomY != expectedBottomY;
                  previousBottomY = bottomY;
                  VarintUtil.writeVarint(compressedOut, id << 2 | (hasLight ? 2 : 0) | (hasDiscontinuity ? 1 : 0));
               }
            }
         }

         for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
               int index = FullDataSourceV2.relativePosToIndex(x, z);
               LongArrayList col = inputDataArray[index];
               int size = col != null ? col.size() : 0;

               for (int y = 0; y < size; y++) {
                  long data = col.getLong(y);
                  VarintUtil.writeVarint(compressedOut, FullDataPointUtil.getHeight(data));
               }
            }
         }

         previousBottomY = 0;

         for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
               int index = FullDataSourceV2.relativePosToIndex(x, z);
               LongArrayList col = inputDataArray[index];
               int size = col != null ? col.size() : 0;

               for (int y = 0; y < size; y++) {
                  long data = col.getLong(y);
                  int height = FullDataPointUtil.getHeight(data);
                  int bottomY = FullDataPointUtil.getBottomY(data);
                  int expectedBottomY = previousBottomY - height;
                  if (bottomY != expectedBottomY) {
                     VarintUtil.writeVarint(compressedOut, VarintUtil.zigzagEncode(bottomY - expectedBottomY));
                  }

                  previousBottomY = bottomY;
               }
            }
         }

         for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
               int index = FullDataSourceV2.relativePosToIndex(x, z);
               LongArrayList col = inputDataArray[index];
               int size = col != null ? col.size() : 0;

               for (int y = 0; y < size; y++) {
                  long data = col.getLong(y);
                  int blockLight = FullDataPointUtil.getBlockLight(data);
                  int skyLight = FullDataPointUtil.getSkyLight(data);
                  byte packedLight = (byte)(blockLight << 4 | skyLight);
                  if (packedLight != 0) {
                     compressedOut.writeByte(packedLight);
                  }
               }
            }
         }
      } catch (Throwable var25) {
         if (compressedOut != null) {
            try {
               compressedOut.close();
            } catch (Throwable var24) {
               var25.addSuppressed(var24);
            }
         }

         throw var25;
      }

      if (compressedOut != null) {
         compressedOut.close();
      }
   }

   private static void readBlobToDataSourceDataArrayV2(
      ByteArrayList inputCompressedDataByteArray,
      LongArrayList[] outputDataLongArray,
      @Nullable EDhDirection direction,
      EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException, DataCorruptedException {
      int minX;
      int maxX;
      int minZ;
      int maxZ;
      if (direction != null) {
         long encodedMinMaxPos = FullDataMinMaxPosUtil.getEncodedMinMaxPos(direction);
         minX = FullDataMinMaxPosUtil.getAdjMinX(encodedMinMaxPos);
         maxX = FullDataMinMaxPosUtil.getAdjMaxX(encodedMinMaxPos);
         minZ = FullDataMinMaxPosUtil.getAdjMinZ(encodedMinMaxPos);
         maxZ = FullDataMinMaxPosUtil.getAdjMaxZ(encodedMinMaxPos);
      } else {
         minX = 1;
         maxX = 63;
         minZ = 1;
         maxZ = 63;
      }

      try {
         PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

         try {
            DhDataInputStream compressedIn = DhDataInputStream.create(inputCompressedDataByteArray, compressionModeEnum, checkout);

            try {
               for (int x = minX; x < maxX; x++) {
                  for (int z = minZ; z < maxZ; z++) {
                     int index = FullDataSourceV2.relativePosToIndex(x, z);
                     int count = VarintUtil.readVarint(compressedIn);
                     ListUtil.clearAndSetSize(outputDataLongArray[index], count);
                  }
               }

               for (int x = minX; x < maxX; x++) {
                  for (int z = minZ; z < maxZ; z++) {
                     int index = FullDataSourceV2.relativePosToIndex(x, z);
                     LongArrayList col = outputDataLongArray[index];

                     for (int i = 0; i < col.size(); i++) {
                        int encodedId = VarintUtil.readVarint(compressedIn);
                        col.set(i, FullDataPointUtil.encode(encodedId >> 2, 1, encodedId & 1, (byte)(encodedId & 2), (byte)0));
                     }
                  }
               }

               for (int x = minX; x < maxX; x++) {
                  for (int z = minZ; z < maxZ; z++) {
                     int index = FullDataSourceV2.relativePosToIndex(x, z);
                     LongArrayList col = outputDataLongArray[index];

                     for (int i = 0; i < col.size(); i++) {
                        int height = VarintUtil.readVarint(compressedIn);
                        long data = col.getLong(i);
                        col.set(i, FullDataPointUtil.setHeight(data, height));
                     }
                  }
               }

               int previousBottomY = 0;

               for (int x = minX; x < maxX; x++) {
                  for (int z = minZ; z < maxZ; z++) {
                     int index = FullDataSourceV2.relativePosToIndex(x, z);
                     LongArrayList col = outputDataLongArray[index];

                     for (int i = 0; i < col.size(); i++) {
                        long data = col.getLong(i);
                        int error = 0;
                        if (FullDataPointUtil.getBottomY(data) != 0) {
                           error = VarintUtil.zigzagDecode(VarintUtil.readVarint(compressedIn));
                        }

                        int bottomY = previousBottomY - FullDataPointUtil.getHeight(data) + error;
                        col.set(i, FullDataPointUtil.setBottomY(data, bottomY));
                        previousBottomY = bottomY;
                     }
                  }
               }

               for (int x = minX; x < maxX; x++) {
                  for (int z = minZ; z < maxZ; z++) {
                     int index = FullDataSourceV2.relativePosToIndex(x, z);
                     LongArrayList col = outputDataLongArray[index];

                     for (int i = 0; i < col.size(); i++) {
                        long data = col.getLong(i);
                        boolean hasLight = FullDataPointUtil.getBlockLight(data) != 0;
                        byte skyLight = 0;
                        byte blockLight = 0;
                        if (hasLight) {
                           byte packedLight = compressedIn.readByte();
                           skyLight = (byte)(packedLight & 15);
                           blockLight = (byte)(packedLight >> 4);
                        }

                        col.set(i, FullDataPointUtil.setSkyLight(FullDataPointUtil.setBlockLight(data, blockLight), skyLight));
                     }
                  }
               }

               if (FullDataPointUtil.RUN_VALIDATION) {
                  for (int x = minX; x < maxX; x++) {
                     for (int z = minZ; z < maxZ; z++) {
                        int index = FullDataSourceV2.relativePosToIndex(x, z);
                        LongArrayList col = outputDataLongArray[index];

                        for (int i = 0; i < col.size(); i++) {
                           FullDataPointUtil.validateDatapoint(col.getLong(i));
                        }
                     }
                  }
               }
            } catch (Throwable var24) {
               if (compressedIn != null) {
                  try {
                     compressedIn.close();
                  } catch (Throwable var23) {
                     var24.addSuppressed(var23);
                  }
               }

               throw var24;
            }

            if (compressedIn != null) {
               compressedIn.close();
            }
         } catch (Throwable var25) {
            if (checkout != null) {
               try {
                  checkout.close();
               } catch (Throwable var22) {
                  var25.addSuppressed(var22);
               }
            }

            throw var25;
         }

         if (checkout != null) {
            checkout.close();
         }
      } catch (EOFException var26) {
         throw new DataCorruptedException(var26);
      }
   }

   private static void writeGenerationStepsToBlob(
      ByteArrayList inputColumnGenStepByteArray, ByteArrayList outputByteArray, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException {
      DhDataOutputStream compressedOut = DhDataOutputStream.create(compressionModeEnum, outputByteArray);

      try {
         for (int i = 0; i < inputColumnGenStepByteArray.size(); i++) {
            compressedOut.writeByte(inputColumnGenStepByteArray.getByte(i));
         }
      } catch (Throwable var7) {
         if (compressedOut != null) {
            try {
               compressedOut.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (compressedOut != null) {
         compressedOut.close();
      }
   }

   private static void readBlobToGenerationSteps(
      ByteArrayList inputCompressedDataByteArray, ByteArrayList outputByteArray, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException, DataCorruptedException {
      try {
         PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

         try {
            DhDataInputStream compressedIn = DhDataInputStream.create(inputCompressedDataByteArray, compressionModeEnum, checkout);

            try {
               compressedIn.readFully(outputByteArray.elements(), 0, 4096);
            } catch (Throwable var9) {
               if (compressedIn != null) {
                  try {
                     compressedIn.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (compressedIn != null) {
               compressedIn.close();
            }
         } catch (Throwable var10) {
            if (checkout != null) {
               try {
                  checkout.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (checkout != null) {
            checkout.close();
         }
      } catch (EOFException var11) {
         throw new DataCorruptedException(var11);
      }
   }

   private static void writeWorldCompressionModeToBlob(
      ByteArrayList inputWorldCompressionModeByteArray, ByteArrayList outputByteArray, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException {
      DhDataOutputStream compressedOut = DhDataOutputStream.create(compressionModeEnum, outputByteArray);

      try {
         for (int i = 0; i < inputWorldCompressionModeByteArray.size(); i++) {
            compressedOut.write(inputWorldCompressionModeByteArray.getByte(i));
         }
      } catch (Throwable var7) {
         if (compressedOut != null) {
            try {
               compressedOut.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (compressedOut != null) {
         compressedOut.close();
      }
   }

   private static void readBlobToWorldCompressionMode(
      ByteArrayList inputCompressedDataByteArray, ByteArrayList outputByteArray, EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException, DataCorruptedException {
      try {
         PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

         try {
            DhDataInputStream compressedIn = DhDataInputStream.create(inputCompressedDataByteArray, compressionModeEnum, checkout);

            try {
               compressedIn.readFully(outputByteArray.elements(), 0, 4096);
            } catch (Throwable var9) {
               if (compressedIn != null) {
                  try {
                     compressedIn.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (compressedIn != null) {
               compressedIn.close();
            }
         } catch (Throwable var10) {
            if (checkout != null) {
               try {
                  checkout.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (checkout != null) {
            checkout.close();
         }
      } catch (EOFException var11) {
         throw new DataCorruptedException(var11);
      }
   }

   private static void writeDataMappingToBlob(FullDataPointIdMap mapping, ByteArrayList outputByteArray, EDhApiDataCompressionMode compressionModeEnum) throws IOException {
      DhDataOutputStream compressedOut = DhDataOutputStream.create(compressionModeEnum, outputByteArray);

      try {
         mapping.serialize(compressedOut);
      } catch (Throwable var7) {
         if (compressedOut != null) {
            try {
               compressedOut.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (compressedOut != null) {
         compressedOut.close();
      }
   }

   private static void readBlobToDataMapping(
      FullDataPointIdMap mapping,
      ByteArrayList inputCompressedDataByteArray,
      long pos,
      @NotNull ILevelWrapper levelWrapper,
      EDhApiDataCompressionMode compressionModeEnum
   ) throws IOException, InterruptedException, DataCorruptedException {
      PhantomArrayListCheckout checkout = ARRAY_LIST_POOL.checkoutByteArrays(1);

      try {
         DhDataInputStream compressedIn = DhDataInputStream.create(inputCompressedDataByteArray, compressionModeEnum, checkout);

         try {
            FullDataPointIdMap.deserialize(mapping, compressedIn, pos, levelWrapper);
         } catch (Throwable var12) {
            if (compressedIn != null) {
               try {
                  compressedIn.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (compressedIn != null) {
            compressedIn.close();
         }
      } catch (Throwable var13) {
         if (checkout != null) {
            try {
               checkout.close();
            } catch (Throwable var10) {
               var13.addSuppressed(var10);
            }
         }

         throw var13;
      }

      if (checkout != null) {
         checkout.close();
      }
   }

   @Override
   public void encode(ByteBuf out) {
      out.writeLong(this.pos);
      out.writeInt(this.dataChecksum);
      out.writeInt(this.compressedDataByteArray.size());
      out.writeBytes(this.compressedDataByteArray.elements(), 0, this.compressedDataByteArray.size());
      out.writeInt(this.compressedNorthAdjDataByteArray.size());
      out.writeBytes(this.compressedNorthAdjDataByteArray.elements(), 0, this.compressedNorthAdjDataByteArray.size());
      out.writeInt(this.compressedSouthAdjDataByteArray.size());
      out.writeBytes(this.compressedSouthAdjDataByteArray.elements(), 0, this.compressedSouthAdjDataByteArray.size());
      out.writeInt(this.compressedEastAdjDataByteArray.size());
      out.writeBytes(this.compressedEastAdjDataByteArray.elements(), 0, this.compressedEastAdjDataByteArray.size());
      out.writeInt(this.compressedWestAdjDataByteArray.size());
      out.writeBytes(this.compressedWestAdjDataByteArray.elements(), 0, this.compressedWestAdjDataByteArray.size());
      out.writeInt(this.compressedColumnGenStepByteArray.size());
      out.writeBytes(this.compressedColumnGenStepByteArray.elements(), 0, this.compressedColumnGenStepByteArray.size());
      out.writeInt(this.compressedWorldCompressionModeByteArray.size());
      out.writeBytes(this.compressedWorldCompressionModeByteArray.elements(), 0, this.compressedWorldCompressionModeByteArray.size());
      out.writeInt(this.compressedMappingByteArray.size());
      out.writeBytes(this.compressedMappingByteArray.elements(), 0, this.compressedMappingByteArray.size());
      out.writeByte(this.dataFormatVersion);
      out.writeByte(this.compressionModeValue);
      out.writeBoolean(BoolUtil.falseIfNull(this.applyToParent));
      out.writeBoolean(BoolUtil.falseIfNull(this.applyToChildren));
      out.writeLong(this.lastModifiedUnixDateTime);
      out.writeLong(this.createdUnixDateTime);
   }

   @Override
   public void decode(ByteBuf in) {
      this.pos = in.readLong();
      this.dataChecksum = in.readInt();
      this.compressedDataByteArray.size(in.readInt());
      in.readBytes(this.compressedDataByteArray.elements(), 0, this.compressedDataByteArray.size());
      this.compressedNorthAdjDataByteArray.size(in.readInt());
      in.readBytes(this.compressedNorthAdjDataByteArray.elements(), 0, this.compressedNorthAdjDataByteArray.size());
      this.compressedSouthAdjDataByteArray.size(in.readInt());
      in.readBytes(this.compressedSouthAdjDataByteArray.elements(), 0, this.compressedSouthAdjDataByteArray.size());
      this.compressedEastAdjDataByteArray.size(in.readInt());
      in.readBytes(this.compressedEastAdjDataByteArray.elements(), 0, this.compressedEastAdjDataByteArray.size());
      this.compressedWestAdjDataByteArray.size(in.readInt());
      in.readBytes(this.compressedWestAdjDataByteArray.elements(), 0, this.compressedWestAdjDataByteArray.size());
      this.compressedColumnGenStepByteArray.size(in.readInt());
      in.readBytes(this.compressedColumnGenStepByteArray.elements(), 0, this.compressedColumnGenStepByteArray.size());
      this.compressedWorldCompressionModeByteArray.size(in.readInt());
      in.readBytes(this.compressedWorldCompressionModeByteArray.elements(), 0, this.compressedWorldCompressionModeByteArray.size());
      this.compressedMappingByteArray.size(in.readInt());
      in.readBytes(this.compressedMappingByteArray.elements(), 0, this.compressedMappingByteArray.size());
      this.dataFormatVersion = in.readByte();
      this.compressionModeValue = in.readByte();
      this.applyToParent = in.readBoolean();
      this.applyToChildren = in.readBoolean();
      this.lastModifiedUnixDateTime = in.readLong();
      this.createdUnixDateTime = in.readLong();
   }

   public Long getKey() {
      return this.pos;
   }

   @Override
   public String getKeyDisplayString() {
      return DhSectionPos.toString(this.pos);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("pos", DhSectionPos.toString(this.pos))
         .add("dataChecksum", this.dataChecksum)
         .add("compressedDataByteArray length", this.compressedDataByteArray.size())
         .add("compressedColumnGenStepByteArray length", this.compressedColumnGenStepByteArray.size())
         .add("compressedWorldCompressionModeByteArray length", this.compressedWorldCompressionModeByteArray.size())
         .add("compressedMappingByteArray length", this.compressedMappingByteArray.size())
         .add("dataFormatVersion", this.dataFormatVersion)
         .add("compressionModeValue", this.compressionModeValue)
         .add("applyToParent", this.applyToParent)
         .add("applyToChildren", this.applyToChildren)
         .add("lastModifiedUnixDateTime", this.lastModifiedUnixDateTime)
         .add("createdUnixDateTime", this.createdUnixDateTime)
         .toString();
   }

   public static class DATA_FORMAT {
      public static final int V1_NO_ADJACENT_DATA = 1;
      public static final int V2_LATEST = 2;
   }
}
