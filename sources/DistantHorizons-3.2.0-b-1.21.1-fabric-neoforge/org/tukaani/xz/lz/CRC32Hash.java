package org.tukaani.xz.lz;

class CRC32Hash {
   private static final int CRC32_POLY = -306674912;
   static final int[] crcTable = new int[256];

   static {
      for (int var0 = 0; var0 < 256; var0++) {
         int var1 = var0;

         for (int var2 = 0; var2 < 8; var2++) {
            if ((var1 & 1) != 0) {
               var1 = var1 >>> 1 ^ -306674912;
            } else {
               var1 >>>= 1;
            }
         }

         crcTable[var0] = var1;
      }
   }
}
