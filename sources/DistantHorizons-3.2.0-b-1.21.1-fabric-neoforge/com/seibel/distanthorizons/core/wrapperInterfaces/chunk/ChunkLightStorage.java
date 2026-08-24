package com.seibel.distanthorizons.core.wrapperInterfaces.chunk;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.coreapi.util.BitShiftUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkLightStorage {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public int minY;
   public int maxY;
   public ChunkLightStorage.LightSection[] lightSections;
   public int aboveMaxYValue;
   public int belowMinYValue;

   public static ChunkLightStorage createSkyLightStorage(IChunkWrapper chunkWrapper) {
      return createSkyLightStorage(chunkWrapper.getInclusiveMinBuildHeight(), chunkWrapper.getExclusiveMaxBuildHeight());
   }

   public static ChunkLightStorage createSkyLightStorage(int minY, int maxY) {
      return new ChunkLightStorage(minY, maxY, 15, 0);
   }

   public static ChunkLightStorage createBlockLightStorage(IChunkWrapper chunkWrapper) {
      return createBlockLightStorage(chunkWrapper.getInclusiveMinBuildHeight(), chunkWrapper.getExclusiveMaxBuildHeight());
   }

   public static ChunkLightStorage createBlockLightStorage(int minY, int maxY) {
      return new ChunkLightStorage(minY, maxY, 0, 0);
   }

   public ChunkLightStorage(int minY, int maxY, int aboveMaxYValue, int belowMinYValue) {
      this.minY = minY;
      this.maxY = maxY;
      this.aboveMaxYValue = aboveMaxYValue;
      this.belowMinYValue = belowMinYValue;
   }

   public int get(int x, int y, int z) {
      if (y < this.minY) {
         return this.belowMinYValue;
      } else if (y >= this.maxY) {
         return this.aboveMaxYValue;
      } else {
         if (this.lightSections != null) {
            int sectionIndex = BitShiftUtil.divideByPowerOfTwo(y - this.minY, 4);

            try {
               ChunkLightStorage.LightSection lightSection = this.lightSections[sectionIndex];
               if (lightSection != null) {
                  return lightSection.get(x, y, z);
               }
            } catch (IndexOutOfBoundsException var6) {
               throw new IndexOutOfBoundsException(
                  "Failed to get light at x:["
                     + x
                     + "], y:["
                     + y
                     + "], z:["
                     + z
                     + "], index:["
                     + sectionIndex
                     + "]. MinY:["
                     + this.minY
                     + "], maxY:["
                     + this.maxY
                     + "], section length:["
                     + this.lightSections.length
                     + "].  Original error: ["
                     + var6.getMessage()
                     + "]."
               );
            }
         }

         return 0;
      }
   }

   public void set(int x, int y, int z, int lightLevel) {
      if (y >= this.minY && y < this.maxY) {
         if (this.lightSections == null) {
            int arrayLength = (this.maxY - this.minY) / 16;
            this.lightSections = new ChunkLightStorage.LightSection[arrayLength];
         }

         int index = y - this.minY >> 4;
         ChunkLightStorage.LightSection lightSection = this.lightSections[index];
         if (lightSection == null) {
            lightSection = new ChunkLightStorage.LightSection(0);
            this.lightSections[index] = lightSection;
         }

         lightSection.set(x, y, z, lightLevel);
      }
   }

   public boolean isEmpty() {
      return this.lightSections == null || this.lightSections.length == 0;
   }

   public void clear() {
      if (this.lightSections != null) {
         for (int i = 0; i < this.lightSections.length; i++) {
            ChunkLightStorage.LightSection section = this.lightSections[i];
            if (section != null) {
               section.constantValue = 0;
               if (section.data != null) {
                  Arrays.fill(section.data, 0L);
               }
            }
         }
      }
   }

   static class DataRecycler {
      private static final ArrayList<long[]> ARRAY_LIST = new ArrayList<>(256);
      private static final ReentrantLock ACCESS_LOCK = new ReentrantLock();

      static long[] get() {
         long[] var0;
         try {
            ACCESS_LOCK.lock();
            if (!ARRAY_LIST.isEmpty()) {
               return ARRAY_LIST.remove(ARRAY_LIST.size() - 1);
            }

            var0 = new long[256];
         } finally {
            ACCESS_LOCK.unlock();
         }

         return var0;
      }

      static void reclaim(long[] data) {
         try {
            ACCESS_LOCK.lock();
            if (ARRAY_LIST.size() < 256) {
               ARRAY_LIST.add(data);
            }
         } finally {
            ACCESS_LOCK.unlock();
         }
      }
   }

   public static class LightSection {
      public byte constantValue;
      public long[] data;
      public short[] counts;
      private final ReentrantLock concurrencyCheckLock = new ReentrantLock();

      public LightSection(int initialValue) {
         if (initialValue < 0) {
            throw new IllegalArgumentException("The initial light value must be greater than [0].");
         } else {
            this.constantValue = (byte)initialValue;
            this.counts = new short[16];
            this.counts[initialValue] = 4096;
         }
      }

      public int get(int x, int y, int z) {
         if (!this.concurrencyCheckLock.tryLock()) {
            throw new ConcurrentModificationException(
               "Thread [" + Thread.currentThread().getName() + "] attempted to get chunk light, lock: [" + this.concurrencyCheckLock + "]."
            );
         } else {
            byte bits;
            try {
               if (this.constantValue < 0) {
                  x &= 15;
                  y &= 15;
                  z &= 15;
                  long bitsx = this.data[z << 4 | x];
                  return (int)(bitsx >>> (y << 2)) & 15;
               }

               bits = this.constantValue;
            } finally {
               this.concurrencyCheckLock.unlock();
            }

            return bits;
         }
      }

      public void set(int x, int y, int z, int lightLevel) {
         if (!this.concurrencyCheckLock.tryLock()) {
            throw new ConcurrentModificationException(
               "Thread [" + Thread.currentThread().getName() + "] attempted to set chunk light, lock: [" + this.concurrencyCheckLock + "]."
            );
         } else {
            try {
               int oldLightLevel = -1;
               if (this.constantValue >= 0) {
                  oldLightLevel = this.constantValue;
                  if (oldLightLevel == lightLevel) {
                     return;
                  }

                  this.data = ChunkLightStorage.DataRecycler.get();
                  LodUtil.assertTrue(this.data != null);
                  long payload = oldLightLevel;
                  payload |= payload << 4;
                  payload |= payload << 8;
                  payload |= payload << 16;
                  payload |= payload << 32;
                  Arrays.fill(this.data, payload);
                  this.constantValue = -1;
               }

               x &= 15;
               y &= 15;
               z &= 15;
               int index = z << 4 | x;
               long bits = this.data[index];
               if (oldLightLevel < 0) {
                  oldLightLevel = (int)(bits >>> (y << 2)) & 15;
               }

               bits &= ~(15L << (y << 2));
               bits |= (long)lightLevel << (y << 2);
               this.data[index] = bits;
               this.counts[oldLightLevel]--;
               if (++this.counts[lightLevel] == 4096) {
                  this.constantValue = (byte)lightLevel;
                  LodUtil.assertTrue(this.constantValue >= 0);
                  ChunkLightStorage.DataRecycler.reclaim(this.data);
                  this.data = null;
               }
            } finally {
               this.concurrencyCheckLock.unlock();
            }
         }
      }
   }
}
