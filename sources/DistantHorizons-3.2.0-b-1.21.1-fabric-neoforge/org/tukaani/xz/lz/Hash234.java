package org.tukaani.xz.lz;

import org.tukaani.xz.ArrayCache;

final class Hash234 extends CRC32Hash {
   private static final int HASH_2_SIZE = 1024;
   private static final int HASH_2_MASK = 1023;
   private static final int HASH_3_SIZE = 65536;
   private static final int HASH_3_MASK = 65535;
   private final int hash4Mask;
   private final int[] hash2Table;
   private final int[] hash3Table;
   private final int[] hash4Table;
   private final int hash4Size;
   private int hash2Value = 0;
   private int hash3Value = 0;
   private int hash4Value = 0;

   static int getHash4Size(int i) {
      int var1 = i - 1;
      var1 |= var1 >>> 1;
      var1 |= var1 >>> 2;
      var1 |= var1 >>> 4;
      var1 |= var1 >>> 8;
      var1 >>>= 1;
      var1 |= 65535;
      if (var1 > 16777216) {
         var1 >>>= 1;
      }

      return var1 + 1;
   }

   static int getMemoryUsage(int i) {
      return (66560 + getHash4Size(i)) / 256 + 4;
   }

   Hash234(int i, ArrayCache arrayCache) {
      this.hash2Table = arrayCache.getIntArray(1024, true);
      this.hash3Table = arrayCache.getIntArray(65536, true);
      this.hash4Size = getHash4Size(i);
      this.hash4Table = arrayCache.getIntArray(this.hash4Size, true);
      this.hash4Mask = this.hash4Size - 1;
   }

   void putArraysToCache(ArrayCache arrayCache) {
      arrayCache.putArray(this.hash4Table);
      arrayCache.putArray(this.hash3Table);
      arrayCache.putArray(this.hash2Table);
   }

   void calcHashes(byte[] bs, int i) {
      int var3 = crcTable[bs[i] & 255] ^ bs[i + 1] & 255;
      this.hash2Value = var3 & 1023;
      var3 ^= (bs[i + 2] & 255) << 8;
      this.hash3Value = var3 & 65535;
      var3 ^= crcTable[bs[i + 3] & 255] << 5;
      this.hash4Value = var3 & this.hash4Mask;
   }

   int getHash2Pos() {
      return this.hash2Table[this.hash2Value];
   }

   int getHash3Pos() {
      return this.hash3Table[this.hash3Value];
   }

   int getHash4Pos() {
      return this.hash4Table[this.hash4Value];
   }

   void updateTables(int i) {
      this.hash2Table[this.hash2Value] = i;
      this.hash3Table[this.hash3Value] = i;
      this.hash4Table[this.hash4Value] = i;
   }

   void normalize(int i) {
      LZEncoder.normalize(this.hash2Table, 1024, i);
      LZEncoder.normalize(this.hash3Table, 65536, i);
      LZEncoder.normalize(this.hash4Table, this.hash4Size, i);
   }
}
