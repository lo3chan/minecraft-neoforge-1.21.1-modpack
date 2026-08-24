package com.seibel.distanthorizons.core.util.objects.dataStreams;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class ZstdArrayCache {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final int WARN_CACHE_LENGTH_EXCEEDED = 10;
   public static final AtomicInteger MAX_BYTE_CACHE_LENGTH_REF = new AtomicInteger(10);
   public final IntUnaryOperator maxByteCacheSizeUnaryOperator = x -> Math.max(this.bufferCache.size(), x);
   public final Int2ReferenceArrayMap<ArrayList<ByteBuffer>> bufferCache = new Int2ReferenceArrayMap();

   public ByteBuffer get(int size) {
      ArrayList<ByteBuffer> cacheList = (ArrayList<ByteBuffer>)this.bufferCache.computeIfAbsent(size, newSize -> new ArrayList(4));
      if (cacheList.isEmpty()) {
         return ByteBuffer.allocate(size);
      } else {
         ByteBuffer array = cacheList.remove(cacheList.size() - 1);
         return array == null ? ByteBuffer.allocate(size) : array;
      }
   }

   public void release(ByteBuffer buffer) {
      int size = buffer.array().length;
      this.bufferCache.computeIfAbsent(size, newSize -> new ArrayList());
      ((ArrayList)this.bufferCache.get(size)).add(buffer);
      if (this.bufferCache.size() > 10) {
         int previousMax = MAX_BYTE_CACHE_LENGTH_REF.getAndUpdate(this.maxByteCacheSizeUnaryOperator);
         int newMax = MAX_BYTE_CACHE_LENGTH_REF.get();
         if (newMax > previousMax) {
            LOGGER.warn("LZMA byte array cache expected size exceeded. Expected max length [10], actual length [" + this.bufferCache.size() + "].");
         }
      }
   }
}
