package org.tukaani.xz;

import java.util.ArrayList;
import java.util.List;

public class ResettableArrayCache extends ArrayCache {
   private final ArrayCache arrayCache;
   private final List<byte[]> byteArrays;
   private final List<int[]> intArrays;

   public ResettableArrayCache(ArrayCache arrayCache) {
      this.arrayCache = arrayCache;
      if (arrayCache == ArrayCache.getDummyCache()) {
         this.byteArrays = null;
         this.intArrays = null;
      } else {
         this.byteArrays = new ArrayList<>();
         this.intArrays = new ArrayList<>();
      }
   }

   @Override
   public byte[] getByteArray(int i, boolean bl) {
      byte[] var3 = this.arrayCache.getByteArray(i, bl);
      if (this.byteArrays != null) {
         synchronized (this.byteArrays) {
            this.byteArrays.add(var3);
         }
      }

      return var3;
   }

   @Override
   public void putArray(byte[] bs) {
      if (this.byteArrays != null) {
         synchronized (this.byteArrays) {
            int var3 = this.byteArrays.lastIndexOf(bs);
            if (var3 != -1) {
               this.byteArrays.remove(var3);
            }
         }

         this.arrayCache.putArray(bs);
      }
   }

   @Override
   public int[] getIntArray(int i, boolean bl) {
      int[] var3 = this.arrayCache.getIntArray(i, bl);
      if (this.intArrays != null) {
         synchronized (this.intArrays) {
            this.intArrays.add(var3);
         }
      }

      return var3;
   }

   @Override
   public void putArray(int[] is) {
      if (this.intArrays != null) {
         synchronized (this.intArrays) {
            int var3 = this.intArrays.lastIndexOf(is);
            if (var3 != -1) {
               this.intArrays.remove(var3);
            }
         }

         this.arrayCache.putArray(is);
      }
   }

   public void reset() {
      if (this.byteArrays != null) {
         synchronized (this.byteArrays) {
            for (int var2 = this.byteArrays.size() - 1; var2 >= 0; var2--) {
               this.arrayCache.putArray(this.byteArrays.get(var2));
            }

            this.byteArrays.clear();
         }

         synchronized (this.intArrays) {
            for (int var8 = this.intArrays.size() - 1; var8 >= 0; var8--) {
               this.arrayCache.putArray(this.intArrays.get(var8));
            }

            this.intArrays.clear();
         }
      }
   }
}
