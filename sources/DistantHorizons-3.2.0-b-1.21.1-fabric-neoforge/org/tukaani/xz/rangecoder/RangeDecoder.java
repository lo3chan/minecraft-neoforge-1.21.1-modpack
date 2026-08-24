package org.tukaani.xz.rangecoder;

import java.io.IOException;

public abstract class RangeDecoder extends RangeCoder {
   int range = 0;
   int code = 0;

   public abstract void normalize() throws IOException;

   public int decodeBit(short[] ss, int i) throws IOException {
      this.normalize();
      short var3 = ss[i];
      int var4 = (this.range >>> 11) * var3;
      byte var5;
      if ((this.code ^ -2147483648) < (var4 ^ -2147483648)) {
         this.range = var4;
         ss[i] = (short)(var3 + (2048 - var3 >>> 5));
         var5 = 0;
      } else {
         this.range -= var4;
         this.code -= var4;
         ss[i] = (short)(var3 - (var3 >>> 5));
         var5 = 1;
      }

      return var5;
   }

   public int decodeBitTree(short[] ss) throws IOException {
      int var2 = 1;

      do {
         var2 = var2 << 1 | this.decodeBit(ss, var2);
      } while (var2 < ss.length);

      return var2 - ss.length;
   }

   public int decodeReverseBitTree(short[] ss) throws IOException {
      int var2 = 1;
      int var3 = 0;
      int var4 = 0;

      do {
         int var5 = this.decodeBit(ss, var2);
         var2 = var2 << 1 | var5;
         var4 |= var5 << var3++;
      } while (var2 < ss.length);

      return var4;
   }

   public int decodeDirectBits(int i) throws IOException {
      int var2 = 0;

      do {
         this.normalize();
         this.range >>>= 1;
         int var3 = this.code - this.range >>> 31;
         this.code = this.code - (this.range & var3 - 1);
         var2 = var2 << 1 | 1 - var3;
      } while (--i != 0);

      return var2;
   }
}
