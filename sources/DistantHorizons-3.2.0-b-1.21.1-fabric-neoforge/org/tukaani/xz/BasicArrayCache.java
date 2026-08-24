package org.tukaani.xz;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class BasicArrayCache extends ArrayCache {
   private static final int CACHEABLE_SIZE_MIN = 32768;
   private static final int STACKS_MAX = 32;
   private static final int ELEMENTS_PER_STACK = 512;
   private final BasicArrayCache.CacheMap<byte[]> byteArrayCache = new BasicArrayCache.CacheMap<>();
   private final BasicArrayCache.CacheMap<int[]> intArrayCache = new BasicArrayCache.CacheMap<>();

   public static BasicArrayCache getInstance() {
      return BasicArrayCache.LazyHolder.INSTANCE;
   }

   private static <T> T getArray(BasicArrayCache.CacheMap<T> cacheMap, int i) {
      if (i < 32768) {
         return null;
      } else {
         BasicArrayCache.CyclicStack var2;
         synchronized (cacheMap) {
            var2 = cacheMap.get(i);
         }

         if (var2 == null) {
            return null;
         } else {
            Object var3;
            do {
               Reference var4 = (Reference)var2.pop();
               if (var4 == null) {
                  return null;
               }

               var3 = var4.get();
            } while (var3 == null);

            return (T)var3;
         }
      }
   }

   private static <T> void putArray(BasicArrayCache.CacheMap<T> cacheMap, T object, int i) {
      if (i >= 32768) {
         BasicArrayCache.CyclicStack var3;
         synchronized (cacheMap) {
            var3 = cacheMap.get(i);
            if (var3 == null) {
               var3 = new BasicArrayCache.CyclicStack();
               cacheMap.put(i, var3);
            }
         }

         var3.push(new SoftReference<>(object));
      }
   }

   @Override
   public byte[] getByteArray(int i, boolean bl) {
      byte[] var3 = getArray(this.byteArrayCache, i);
      if (var3 == null) {
         var3 = new byte[i];
      } else if (bl) {
         Arrays.fill(var3, (byte)0);
      }

      return var3;
   }

   @Override
   public void putArray(byte[] bs) {
      putArray(this.byteArrayCache, bs, bs.length);
   }

   @Override
   public int[] getIntArray(int i, boolean bl) {
      int[] var3 = getArray(this.intArrayCache, i);
      if (var3 == null) {
         var3 = new int[i];
      } else if (bl) {
         Arrays.fill(var3, 0);
      }

      return var3;
   }

   @Override
   public void putArray(int[] is) {
      putArray(this.intArrayCache, is, is.length);
   }

   private static class CacheMap<T> extends LinkedHashMap<Integer, BasicArrayCache.CyclicStack<Reference<T>>> {
      private static final long serialVersionUID = 1L;

      public CacheMap() {
         super(64, 0.75F, true);
      }

      @Override
      protected boolean removeEldestEntry(Entry<Integer, BasicArrayCache.CyclicStack<Reference<T>>> entry) {
         return this.size() > 32;
      }
   }

   private static class CyclicStack<T> {
      private final T[] elements = (T[])(new Object[512]);
      private int pos = 0;

      private CyclicStack() {
      }

      public synchronized T pop() {
         Object var1 = this.elements[this.pos];
         this.elements[this.pos] = null;
         this.pos = this.pos - 1 & 511;
         return (T)var1;
      }

      public synchronized void push(T object) {
         this.pos = this.pos + 1 & 511;
         this.elements[this.pos] = (T)object;
      }
   }

   private static final class LazyHolder {
      static final BasicArrayCache INSTANCE = new BasicArrayCache();
   }
}
