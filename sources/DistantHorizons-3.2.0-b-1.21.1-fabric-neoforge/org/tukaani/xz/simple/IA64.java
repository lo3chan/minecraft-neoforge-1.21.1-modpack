package org.tukaani.xz.simple;

public final class IA64 implements SimpleFilter {
   private static final int[] BRANCH_TABLE = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 6, 6, 0, 0, 7, 7, 4, 4, 0, 0, 4, 4, 0, 0};
   private final boolean isEncoder;
   private int pos;

   public IA64(boolean bl, int i) {
      this.isEncoder = bl;
      this.pos = i;
   }

   @Override
   public int code(byte[] bs, int i, int j) {
      int var4 = i + j - 16;

      int var5;
      for (var5 = i; var5 <= var4; var5 += 16) {
         int var6 = bs[var5] & 31;
         int var7 = BRANCH_TABLE[var6];
         int var8 = 0;

         for (byte var9 = 5; var8 < 3; var9 += 41) {
            if ((var7 >>> var8 & 1) != 0) {
               int var10 = var9 >>> 3;
               int var11 = var9 & 7;
               long var12 = 0L;

               for (int var14 = 0; var14 < 6; var14++) {
                  var12 |= (bs[var5 + var10 + var14] & 255L) << 8 * var14;
               }

               long var22 = var12 >>> var11;
               if ((var22 >>> 37 & 15L) == 5L && (var22 >>> 9 & 7L) == 0L) {
                  int var16 = (int)(var22 >>> 13 & 1048575L);
                  var16 |= ((int)(var22 >>> 36) & 1) << 20;
                  var16 <<= 4;
                  int var17;
                  if (this.isEncoder) {
                     var17 = var16 + (this.pos + var5 - i);
                  } else {
                     var17 = var16 - (this.pos + var5 - i);
                  }

                  var17 >>>= 4;
                  var22 &= -77309403137L;
                  var22 |= (var17 & 1048575L) << 13;
                  var22 |= (var17 & 1048576L) << 16;
                  var12 &= (1 << var11) - 1;
                  var12 |= var22 << var11;

                  for (int var18 = 0; var18 < 6; var18++) {
                     bs[var5 + var10 + var18] = (byte)(var12 >>> 8 * var18);
                  }
               }
            }

            var8++;
         }
      }

      var5 -= i;
      this.pos += var5;
      return var5;
   }
}
