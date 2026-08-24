package org.tukaani.xz.check;

public class CRC64 extends Check {
   private static final long[][] TABLE = new long[4][256];
   private long crc = -1L;

   public CRC64() {
      this.size = 8;
      this.name = "CRC64";
   }

   @Override
   public void update(byte[] bs, int i, int j) {
      int var4 = i + j;
      int var5 = i;

      for (int var6 = var4 - 3; var5 < var6; var5 += 4) {
         int var7 = (int)this.crc;
         this.crc = TABLE[3][var7 & 0xFF ^ bs[var5] & 255]
            ^ TABLE[2][var7 >>> 8 & 0xFF ^ bs[var5 + 1] & 255]
            ^ this.crc >>> 32
            ^ TABLE[1][var7 >>> 16 & 0xFF ^ bs[var5 + 2] & 255]
            ^ TABLE[0][var7 >>> 24 & 0xFF ^ bs[var5 + 3] & 255];
      }

      while (var5 < var4) {
         this.crc = TABLE[0][bs[var5++] & 255 ^ (int)this.crc & 0xFF] ^ this.crc >>> 8;
      }
   }

   @Override
   public byte[] finish() {
      long var1 = ~this.crc;
      this.crc = -1L;
      byte[] var3 = new byte[8];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = (byte)(var1 >> var4 * 8);
      }

      return var3;
   }

   static {
      for (int var0 = 0; var0 < 4; var0++) {
         for (int var1 = 0; var1 < 256; var1++) {
            long var2 = var0 == 0 ? var1 : TABLE[var0 - 1][var1];

            for (int var4 = 0; var4 < 8; var4++) {
               if ((var2 & 1L) == 1L) {
                  var2 = var2 >>> 1 ^ -3932672073523589310L;
               } else {
                  var2 >>>= 1;
               }
            }

            TABLE[var0][var1] = var2;
         }
      }
   }
}
