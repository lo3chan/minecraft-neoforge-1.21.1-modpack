package org.tukaani.xz.simple;

public final class ARMThumb implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public ARMThumb(boolean bl, int i) {
      this.isEncoder = bl;
      this.pos = i + 4;
   }

   @Override
   public int code(byte[] bs, int i, int j) {
      int var4 = i + j - 4;

      int var5;
      for (var5 = i; var5 <= var4; var5 += 2) {
         if ((bs[var5 + 1] & 248) == 240 && (bs[var5 + 3] & 248) == 248) {
            int var6 = (bs[var5 + 1] & 7) << 19 | (bs[var5] & 255) << 11 | (bs[var5 + 3] & 7) << 8 | bs[var5 + 2] & 255;
            var6 <<= 1;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - i);
            } else {
               var7 = var6 - (this.pos + var5 - i);
            }

            var7 >>>= 1;
            bs[var5 + 1] = (byte)(240 | var7 >>> 19 & 7);
            bs[var5] = (byte)(var7 >>> 11);
            bs[var5 + 3] = (byte)(248 | var7 >>> 8 & 7);
            bs[var5 + 2] = (byte)var7;
            var5 += 2;
         }
      }

      var5 -= i;
      this.pos += var5;
      return var5;
   }
}
