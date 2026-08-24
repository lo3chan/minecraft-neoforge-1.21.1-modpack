package org.tukaani.xz.rangecoder;

import java.io.IOException;

public abstract class RangeEncoder extends RangeCoder {
   private static final int MOVE_REDUCING_BITS = 4;
   private static final int BIT_PRICE_SHIFT_BITS = 4;
   private static final int[] prices = new int[128];
   private long low;
   private int range;
   long cacheSize;
   private byte cache;

   public void reset() {
      this.low = 0L;
      this.range = -1;
      this.cache = 0;
      this.cacheSize = 1L;
   }

   public int getPendingSize() {
      throw new Error();
   }

   public int finish() throws IOException {
      for (int var1 = 0; var1 < 5; var1++) {
         this.shiftLow();
      }

      return -1;
   }

   abstract void writeByte(int i) throws IOException;

   private void shiftLow() throws IOException {
      int var1 = (int)(this.low >>> 32);
      if (var1 != 0 || this.low < 4278190080L) {
         short var2 = this.cache;

         do {
            this.writeByte(var2 + var1);
            var2 = 255;
         } while (--this.cacheSize != 0L);

         this.cache = (byte)(this.low >>> 24);
      }

      this.cacheSize++;
      this.low = (this.low & 16777215L) << 8;
   }

   public void encodeBit(short[] ss, int i, int j) throws IOException {
      short var4 = ss[i];
      int var5 = (this.range >>> 11) * var4;
      if (j == 0) {
         this.range = var5;
         ss[i] = (short)(var4 + (2048 - var4 >>> 5));
      } else {
         this.low += var5 & 4294967295L;
         this.range -= var5;
         ss[i] = (short)(var4 - (var4 >>> 5));
      }

      if ((this.range & 0xFF000000) == 0) {
         this.range <<= 8;
         this.shiftLow();
      }
   }

   public static int getBitPrice(int i, int j) {
      assert j == 0 || j == 1;

      return prices[(i ^ -j & 2047) >>> 4];
   }

   public void encodeBitTree(short[] ss, int i) throws IOException {
      byte var3 = 1;
      int var4 = ss.length;

      do {
         var4 >>>= 1;
         int var5 = i & var4;
         this.encodeBit(ss, var3, var5);
         var3 <<= 1;
         if (var5 != 0) {
            var3 |= 1;
         }
      } while (var4 != 1);
   }

   public static int getBitTreePrice(short[] ss, int i) {
      int var2 = 0;
      i |= ss.length;

      do {
         int var3 = i & 1;
         i >>>= 1;
         var2 += getBitPrice(ss[i], var3);
      } while (i != 1);

      return var2;
   }

   public void encodeReverseBitTree(short[] ss, int i) throws IOException {
      int var3 = 1;
      i |= ss.length;

      do {
         int var4 = i & 1;
         i >>>= 1;
         this.encodeBit(ss, var3, var4);
         var3 = var3 << 1 | var4;
      } while (i != 1);
   }

   public static int getReverseBitTreePrice(short[] ss, int i) {
      int var2 = 0;
      int var3 = 1;
      i |= ss.length;

      do {
         int var4 = i & 1;
         i >>>= 1;
         var2 += getBitPrice(ss[var3], var4);
         var3 = var3 << 1 | var4;
      } while (i != 1);

      return var2;
   }

   public void encodeDirectBits(int i, int j) throws IOException {
      do {
         this.range >>>= 1;
         j--;
         this.low = this.low + (this.range & 0 - (i >>> j & 1));
         if ((this.range & 0xFF000000) == 0) {
            this.range <<= 8;
            this.shiftLow();
         }
      } while (j != 0);
   }

   public static int getDirectBitsPrice(int i) {
      return i << 4;
   }

   static {
      for (byte var0 = 8; var0 < 2048; var0 += 16) {
         byte var1 = var0;
         int var2 = 0;

         for (int var3 = 0; var3 < 4; var3++) {
            var1 *= var1;

            for (var2 <<= 1; (var1 & -65536) != 0; var2++) {
               var1 >>>= 1;
            }
         }

         prices[var0 >> 4] = 161 - var2;
      }
   }
}
