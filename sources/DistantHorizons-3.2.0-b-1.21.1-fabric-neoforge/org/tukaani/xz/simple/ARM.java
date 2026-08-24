package org.tukaani.xz.simple;

public final class ARM implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public ARM(boolean bl, int i) {
      this.isEncoder = bl;
      this.pos = i + 8;
   }

   @Override
   public int code(byte[] bs, int i, int j) {
      int var4 = i + j - 4;

      int var5;
      for (var5 = i; var5 <= var4; var5 += 4) {
         if ((bs[var5 + 3] & 255) == 235) {
            int var6 = (bs[var5 + 2] & 255) << 16 | (bs[var5 + 1] & 255) << 8 | bs[var5] & 255;
            var6 <<= 2;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - i);
            } else {
               var7 = var6 - (this.pos + var5 - i);
            }

            var7 >>>= 2;
            bs[var5 + 2] = (byte)(var7 >>> 16);
            bs[var5 + 1] = (byte)(var7 >>> 8);
            bs[var5] = (byte)var7;
         }
      }

      var5 -= i;
      this.pos += var5;
      return var5;
   }
}
