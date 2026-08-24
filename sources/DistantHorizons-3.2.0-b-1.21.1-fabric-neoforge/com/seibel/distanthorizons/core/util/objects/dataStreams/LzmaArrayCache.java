package com.seibel.distanthorizons.core.util.objects.dataStreams;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import org.tukaani.xz.ArrayCache;

public class LzmaArrayCache extends ArrayCache {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int WARN_CACHE_LENGTH_EXCEEDED = 10;
   public static final AtomicInteger MAX_INT_CACHE_LENGTH_REF = new AtomicInteger(10);
   public static final AtomicInteger MAX_BYTE_CACHE_LENGTH_REF = new AtomicInteger(10);
   public final IntUnaryOperator maxByteCacheSizeUnaryOperator = x -> Math.max(this.byteCache.size(), x);
   public final IntUnaryOperator maxIntCacheSizeUnaryOperator = x -> Math.max(this.intCache.size(), x);
   public final Int2ReferenceArrayMap<ArrayList<byte[]>> byteCache = new Int2ReferenceArrayMap();
   public final Int2ReferenceArrayMap<ArrayList<int[]>> intCache = new Int2ReferenceArrayMap();

   @Override
   public byte[] getByteArray(int size, boolean fillWithZeros) {
      ArrayList<byte[]> cacheList = (ArrayList<byte[]>)this.byteCache.computeIfAbsent(size, newSize -> new ArrayList(4));
      if (cacheList.isEmpty()) {
         return new byte[size];
      } else {
         byte[] array = cacheList.remove(cacheList.size() - 1);
         if (array == null) {
            return new byte[size];
         } else {
            if (fillWithZeros) {
               Arrays.fill(array, (byte)0);
            }

            return array;
         }
      }
   }

   @Override
   public void putArray(byte[] array) {
      int size = array.length;
      this.byteCache.computeIfAbsent(size, newSize -> new ArrayList());
      ((ArrayList)this.byteCache.get(size)).add(array);
      if (this.byteCache.size() > 10) {
         int previousMax = MAX_BYTE_CACHE_LENGTH_REF.getAndUpdate(this.maxByteCacheSizeUnaryOperator);
         int newMax = MAX_BYTE_CACHE_LENGTH_REF.get();
         if (newMax > previousMax) {
            LOGGER.warn("LZMA byte array cache expected size exceeded. Expected max length [10], actual length [" + this.byteCache.size() + "].");
         }
      }
   }

   @Override
   public int[] getIntArray(int size, boolean fillWithZeros) {
      ArrayList<int[]> cacheList = (ArrayList<int[]>)this.intCache.computeIfAbsent(size, newSize -> new ArrayList(4));
      if (cacheList.size() == 0) {
         return new int[size];
      } else {
         int[] array = cacheList.remove(cacheList.size() - 1);
         if (array == null) {
            return new int[size];
         } else {
            if (fillWithZeros) {
               Arrays.fill(array, 0);
            }

            return array;
         }
      }
   }

   @Override
   public void putArray(int[] array) {
      int size = array.length;
      this.intCache.computeIfAbsent(size, newSize -> new ArrayList());
      ((ArrayList)this.intCache.get(size)).add(array);
      if (this.intCache.size() > 10) {
         int previousMax = MAX_INT_CACHE_LENGTH_REF.getAndUpdate(this.maxIntCacheSizeUnaryOperator);
         int newMax = MAX_INT_CACHE_LENGTH_REF.get();
         if (newMax > previousMax) {
            LOGGER.warn("LZMA int array cache expected size exceeded. Expected max length [10], actual length [" + this.intCache.size() + "].");
         }
      }
   }
}
