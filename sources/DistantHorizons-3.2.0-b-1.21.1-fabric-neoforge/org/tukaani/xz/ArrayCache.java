package org.tukaani.xz;

public class ArrayCache {
   private static final ArrayCache dummyCache = new ArrayCache();
   private static volatile ArrayCache defaultCache = dummyCache;

   public static ArrayCache getDummyCache() {
      return dummyCache;
   }

   public static ArrayCache getDefaultCache() {
      return defaultCache;
   }

   public static void setDefaultCache(ArrayCache arrayCache) {
      if (arrayCache == null) {
         throw new NullPointerException();
      } else {
         defaultCache = arrayCache;
      }
   }

   public byte[] getByteArray(int i, boolean bl) {
      return new byte[i];
   }

   public void putArray(byte[] bs) {
   }

   public int[] getIntArray(int i, boolean bl) {
      return new int[i];
   }

   public void putArray(int[] is) {
   }
}
