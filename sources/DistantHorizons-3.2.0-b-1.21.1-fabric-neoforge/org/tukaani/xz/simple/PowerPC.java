package org.tukaani.xz.simple;

public final class PowerPC implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public PowerPC(boolean bl, int i) {
      this.isEncoder = bl;
      this.pos = i;
   }

   @Override
   public int code(byte[] bs, int i, int j) {
      int var4 = i + j - 4;

      int var5;
      for (var5 = i; var5 <= var4; var5 += 4) {
         if ((bs[var5] & 252) == 72 && (bs[var5 + 3] & 3) == 1) {
            int var6 = (bs[var5] & 3) << 24 | (bs[var5 + 1] & 255) << 16 | (bs[var5 + 2] & 255) << 8 | bs[var5 + 3] & 252;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - i);
            } else {
               var7 = var6 - (this.pos + var5 - i);
            }

            bs[var5] = (byte)(72 | var7 >>> 24 & 3);
            bs[var5 + 1] = (byte)(var7 >>> 16);
            bs[var5 + 2] = (byte)(var7 >>> 8);
            bs[var5 + 3] = (byte)(bs[var5 + 3] & 3 | var7);
         }
      }

      var5 -= i;
      this.pos += var5;
      return var5;
   }
}
