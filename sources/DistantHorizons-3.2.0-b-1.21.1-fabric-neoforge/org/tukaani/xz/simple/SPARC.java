package org.tukaani.xz.simple;

public final class SPARC implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public SPARC(boolean bl, int i) {
      this.isEncoder = bl;
      this.pos = i;
   }

   @Override
   public int code(byte[] bs, int i, int j) {
      int var4 = i + j - 4;

      int var5;
      for (var5 = i; var5 <= var4; var5 += 4) {
         if (bs[var5] == 64 && (bs[var5 + 1] & 192) == 0 || bs[var5] == 127 && (bs[var5 + 1] & 192) == 192) {
            int var6 = (bs[var5] & 255) << 24 | (bs[var5 + 1] & 255) << 16 | (bs[var5 + 2] & 255) << 8 | bs[var5 + 3] & 255;
            var6 <<= 2;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - i);
            } else {
               var7 = var6 - (this.pos + var5 - i);
            }

            var7 >>>= 2;
            var7 = 0 - (var7 >>> 22 & 1) << 22 & 1073741823 | var7 & 4194303 | 1073741824;
            bs[var5] = (byte)(var7 >>> 24);
            bs[var5 + 1] = (byte)(var7 >>> 16);
            bs[var5 + 2] = (byte)(var7 >>> 8);
            bs[var5 + 3] = (byte)var7;
         }
      }

      var5 -= i;
      this.pos += var5;
      return var5;
   }
}
