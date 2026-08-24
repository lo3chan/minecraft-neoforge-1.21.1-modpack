package org.tukaani.xz.lzma;

import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lz.Matches;
import org.tukaani.xz.rangecoder.RangeEncoder;

final class LZMAEncoderFast extends LZMAEncoder {
   private static final int EXTRA_SIZE_BEFORE = 1;
   private static final int EXTRA_SIZE_AFTER = 272;
   private Matches matches = null;

   static int getMemoryUsage(int i, int j, int k) {
      return LZEncoder.getMemoryUsage(i, Math.max(j, 1), 272, 273, k);
   }

   LZMAEncoderFast(RangeEncoder rangeEncoder, int i, int j, int k, int l, int m, int n, int o, int p, ArrayCache arrayCache) {
      super(rangeEncoder, LZEncoder.getInstance(l, Math.max(m, 1), 272, n, 273, o, p, arrayCache), i, j, k, l, n);
   }

   private boolean changePair(int i, int j) {
      return i < j >>> 7;
   }

   @Override
   int getNextSymbol() {
      if (this.readAhead == -1) {
         this.matches = this.getMatches();
      }

      this.back = -1;
      int var1 = Math.min(this.lz.getAvail(), 273);
      if (var1 < 2) {
         return 1;
      } else {
         int var2 = 0;
         int var3 = 0;

         for (int var4 = 0; var4 < 4; var4++) {
            int var5 = this.lz.getMatchLen(this.reps[var4], var1);
            if (var5 >= 2) {
               if (var5 >= this.niceLen) {
                  this.back = var4;
                  this.skip(var5 - 1);
                  return var5;
               }

               if (var5 > var2) {
                  var3 = var4;
                  var2 = var5;
               }
            }
         }

         int var8 = 0;
         int var9 = 0;
         if (this.matches.count > 0) {
            var8 = this.matches.len[this.matches.count - 1];
            var9 = this.matches.dist[this.matches.count - 1];
            if (var8 >= this.niceLen) {
               this.back = var9 + 4;
               this.skip(var8 - 1);
               return var8;
            }

            while (
               this.matches.count > 1
                  && var8 == this.matches.len[this.matches.count - 2] + 1
                  && this.changePair(this.matches.dist[this.matches.count - 2], var9)
            ) {
               this.matches.count--;
               var8 = this.matches.len[this.matches.count - 1];
               var9 = this.matches.dist[this.matches.count - 1];
            }

            if (var8 == 2 && var9 >= 128) {
               var8 = 1;
            }
         }

         if (var2 < 2 || var2 + 1 < var8 && (var2 + 2 < var8 || var9 < 512) && (var2 + 3 < var8 || var9 < 32768)) {
            if (var8 >= 2 && var1 > 2) {
               this.matches = this.getMatches();
               if (this.matches.count > 0) {
                  int var6 = this.matches.len[this.matches.count - 1];
                  int var7 = this.matches.dist[this.matches.count - 1];
                  if (var6 >= var8 && var7 < var9
                     || var6 == var8 + 1 && !this.changePair(var9, var7)
                     || var6 > var8 + 1
                     || var6 + 1 >= var8 && var8 >= 3 && this.changePair(var7, var9)) {
                     return 1;
                  }
               }

               int var10 = Math.max(var8 - 1, 2);

               for (int var11 = 0; var11 < 4; var11++) {
                  if (this.lz.getMatchLen(this.reps[var11], var10) == var10) {
                     return 1;
                  }
               }

               this.back = var9 + 4;
               this.skip(var8 - 2);
               return var8;
            } else {
               return 1;
            }
         } else {
            this.back = var3;
            this.skip(var2 - 1);
            return var2;
         }
      }
   }
}
