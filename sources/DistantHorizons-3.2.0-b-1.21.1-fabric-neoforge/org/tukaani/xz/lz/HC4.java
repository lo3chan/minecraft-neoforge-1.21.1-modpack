package org.tukaani.xz.lz;

import org.tukaani.xz.ArrayCache;

final class HC4 extends LZEncoder {
   private final Hash234 hash;
   private final int[] chain;
   private final Matches matches;
   private final int depthLimit;
   private final int cyclicSize;
   private int cyclicPos = -1;
   private int lzPos;

   static int getMemoryUsage(int i) {
      return Hash234.getMemoryUsage(i) + i / 256 + 10;
   }

   HC4(int i, int j, int k, int l, int m, int n, ArrayCache arrayCache) {
      super(i, j, k, l, m, arrayCache);
      this.hash = new Hash234(i, arrayCache);
      this.cyclicSize = i + 1;
      this.chain = arrayCache.getIntArray(this.cyclicSize, false);
      this.lzPos = this.cyclicSize;
      this.matches = new Matches(l - 1);
      this.depthLimit = n > 0 ? n : 4 + l / 4;
   }

   @Override
   public void putArraysToCache(ArrayCache arrayCache) {
      arrayCache.putArray(this.chain);
      this.hash.putArraysToCache(arrayCache);
      super.putArraysToCache(arrayCache);
   }

   private int movePos() {
      int var1 = this.movePos(4, 4);
      if (var1 != 0) {
         if (++this.lzPos == 2147483647) {
            int var2 = 2147483647 - this.cyclicSize;
            this.hash.normalize(var2);
            normalize(this.chain, this.cyclicSize, var2);
            this.lzPos -= var2;
         }

         if (++this.cyclicPos == this.cyclicSize) {
            this.cyclicPos = 0;
         }
      }

      return var1;
   }

   @Override
   public Matches getMatches() {
      this.matches.count = 0;
      int var1 = this.matchLenMax;
      int var2 = this.niceLen;
      int var3 = this.movePos();
      if (var3 < var1) {
         if (var3 == 0) {
            return this.matches;
         }

         var1 = var3;
         if (var2 > var3) {
            var2 = var3;
         }
      }

      this.hash.calcHashes(this.buf, this.readPos);
      int var4 = this.lzPos - this.hash.getHash2Pos();
      int var5 = this.lzPos - this.hash.getHash3Pos();
      int var6 = this.hash.getHash4Pos();
      this.hash.updateTables(this.lzPos);
      this.chain[this.cyclicPos] = var6;
      int var7 = 0;
      if (var4 < this.cyclicSize && this.buf[this.readPos - var4] == this.buf[this.readPos]) {
         var7 = 2;
         this.matches.len[0] = 2;
         this.matches.dist[0] = var4 - 1;
         this.matches.count = 1;
      }

      if (var4 != var5 && var5 < this.cyclicSize && this.buf[this.readPos - var5] == this.buf[this.readPos]) {
         var7 = 3;
         this.matches.dist[this.matches.count++] = var5 - 1;
         var4 = var5;
      }

      if (this.matches.count > 0) {
         while (var7 < var1 && this.buf[this.readPos + var7 - var4] == this.buf[this.readPos + var7]) {
            var7++;
         }

         this.matches.len[this.matches.count - 1] = var7;
         if (var7 >= var2) {
            return this.matches;
         }
      }

      if (var7 < 3) {
         var7 = 3;
      }

      int var8 = this.depthLimit;

      while (true) {
         int var9 = this.lzPos - var6;
         if (var8-- == 0 || var9 >= this.cyclicSize) {
            return this.matches;
         }

         var6 = this.chain[this.cyclicPos - var9 + (var9 > this.cyclicPos ? this.cyclicSize : 0)];
         if (this.buf[this.readPos + var7 - var9] == this.buf[this.readPos + var7] && this.buf[this.readPos - var9] == this.buf[this.readPos]) {
            int var10 = 0;

            do {
               var10++;
            } while (var10 < var1 && this.buf[this.readPos + var10 - var9] == this.buf[this.readPos + var10]);

            if (var10 > var7) {
               var7 = var10;
               this.matches.len[this.matches.count] = var10;
               this.matches.dist[this.matches.count] = var9 - 1;
               this.matches.count++;
               if (var10 >= var2) {
                  return this.matches;
               }
            }
         }
      }
   }

   @Override
   public void skip(int i) {
      assert i >= 0;

      while (i-- > 0) {
         if (this.movePos() != 0) {
            this.hash.calcHashes(this.buf, this.readPos);
            this.chain[this.cyclicPos] = this.hash.getHash4Pos();
            this.hash.updateTables(this.lzPos);
         }
      }
   }
}
