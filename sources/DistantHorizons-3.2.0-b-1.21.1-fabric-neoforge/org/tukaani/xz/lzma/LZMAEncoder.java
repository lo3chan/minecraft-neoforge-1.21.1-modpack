package org.tukaani.xz.lzma;

import java.io.IOException;
import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.lz.LZEncoder;
import org.tukaani.xz.lz.Matches;
import org.tukaani.xz.rangecoder.RangeEncoder;

public abstract class LZMAEncoder extends LZMACoder {
   public static final int MODE_FAST = 1;
   public static final int MODE_NORMAL = 2;
   private static final int LZMA2_UNCOMPRESSED_LIMIT = 2096879;
   private static final int LZMA2_COMPRESSED_LIMIT = 65510;
   private static final int DIST_PRICE_UPDATE_INTERVAL = 128;
   private static final int ALIGN_PRICE_UPDATE_INTERVAL = 16;
   private final RangeEncoder rc;
   final LZEncoder lz;
   final LZMAEncoder.LiteralEncoder literalEncoder;
   final LZMAEncoder.LengthEncoder matchLenEncoder;
   final LZMAEncoder.LengthEncoder repLenEncoder;
   final int niceLen;
   private int distPriceCount = 0;
   private int alignPriceCount = 0;
   private final int distSlotPricesSize;
   private final int[][] distSlotPrices;
   private final int[][] fullDistPrices = new int[4][128];
   private final int[] alignPrices = new int[16];
   int back = 0;
   int readAhead = -1;
   private int uncompressedSize = 0;

   public static int getMemoryUsage(int i, int j, int k, int l) {
      int var4 = 80;
      switch (i) {
         case 1:
            var4 += LZMAEncoderFast.getMemoryUsage(j, k, l);
            break;
         case 2:
            var4 += LZMAEncoderNormal.getMemoryUsage(j, k, l);
            break;
         default:
            throw new IllegalArgumentException();
      }

      return var4;
   }

   public static LZMAEncoder getInstance(RangeEncoder rangeEncoder, int i, int j, int k, int l, int m, int n, int o, int p, int q, ArrayCache arrayCache) {
      switch (l) {
         case 1:
            return new LZMAEncoderFast(rangeEncoder, i, j, k, m, n, o, p, q, arrayCache);
         case 2:
            return new LZMAEncoderNormal(rangeEncoder, i, j, k, m, n, o, p, q, arrayCache);
         default:
            throw new IllegalArgumentException();
      }
   }

   public void putArraysToCache(ArrayCache arrayCache) {
      this.lz.putArraysToCache(arrayCache);
   }

   public static int getDistSlot(int i) {
      if (i <= 4 && i >= 0) {
         return i;
      } else {
         int var1 = i;
         int var2 = 31;
         if ((i & -65536) == 0) {
            var1 = i << 16;
            var2 = 15;
         }

         if ((var1 & 0xFF000000) == 0) {
            var1 <<= 8;
            var2 -= 8;
         }

         if ((var1 & -268435456) == 0) {
            var1 <<= 4;
            var2 -= 4;
         }

         if ((var1 & -1073741824) == 0) {
            var1 <<= 2;
            var2 -= 2;
         }

         if ((var1 & -2147483648) == 0) {
            var2--;
         }

         return (var2 << 1) + (i >>> var2 - 1 & 1);
      }
   }

   abstract int getNextSymbol();

   LZMAEncoder(RangeEncoder rangeEncoder, LZEncoder lZEncoder, int i, int j, int k, int l, int m) {
      super(k);
      this.rc = rangeEncoder;
      this.lz = lZEncoder;
      this.niceLen = m;
      this.literalEncoder = new LZMAEncoder.LiteralEncoder(i, j);
      this.matchLenEncoder = new LZMAEncoder.LengthEncoder(k, m);
      this.repLenEncoder = new LZMAEncoder.LengthEncoder(k, m);
      this.distSlotPricesSize = getDistSlot(l - 1) + 1;
      this.distSlotPrices = new int[4][this.distSlotPricesSize];
      this.reset();
   }

   public LZEncoder getLZEncoder() {
      return this.lz;
   }

   @Override
   public void reset() {
      super.reset();
      this.literalEncoder.reset();
      this.matchLenEncoder.reset();
      this.repLenEncoder.reset();
      this.distPriceCount = 0;
      this.alignPriceCount = 0;
      this.uncompressedSize = this.uncompressedSize + this.readAhead + 1;
      this.readAhead = -1;
   }

   public int getUncompressedSize() {
      return this.uncompressedSize;
   }

   public void resetUncompressedSize() {
      this.uncompressedSize = 0;
   }

   public void encodeForLZMA1() throws IOException {
      if (this.lz.isStarted() || this.encodeInit()) {
         while (this.encodeSymbol()) {
         }
      }
   }

   public void encodeLZMA1EndMarker() throws IOException {
      int var1 = this.lz.getPos() - this.readAhead & this.posMask;
      this.rc.encodeBit(this.isMatch[this.state.get()], var1, 1);
      this.rc.encodeBit(this.isRep, this.state.get(), 0);
      this.encodeMatch(-1, 2, var1);
   }

   public boolean encodeForLZMA2() {
      try {
         if (!this.lz.isStarted() && !this.encodeInit()) {
            return false;
         } else {
            while (this.uncompressedSize <= 2096879 && this.rc.getPendingSize() <= 65510) {
               if (!this.encodeSymbol()) {
                  return false;
               }
            }

            return true;
         }
      } catch (IOException var2) {
         throw new Error();
      }
   }

   private boolean encodeInit() throws IOException {
      assert this.readAhead == -1;

      if (!this.lz.hasEnoughData(0)) {
         return false;
      } else {
         this.skip(1);
         this.rc.encodeBit(this.isMatch[this.state.get()], 0, 0);
         this.literalEncoder.encodeInit();
         this.readAhead--;

         assert this.readAhead == -1;

         this.uncompressedSize++;

         assert this.uncompressedSize == 1;

         return true;
      }
   }

   private boolean encodeSymbol() throws IOException {
      if (!this.lz.hasEnoughData(this.readAhead + 1)) {
         return false;
      } else {
         int var1 = this.getNextSymbol();

         assert this.readAhead >= 0;

         int var2 = this.lz.getPos() - this.readAhead & this.posMask;
         if (this.back == -1) {
            assert var1 == 1;

            this.rc.encodeBit(this.isMatch[this.state.get()], var2, 0);
            this.literalEncoder.encode();
         } else {
            this.rc.encodeBit(this.isMatch[this.state.get()], var2, 1);
            if (this.back < 4) {
               assert this.lz.getMatchLen(-this.readAhead, this.reps[this.back], var1) == var1;

               this.rc.encodeBit(this.isRep, this.state.get(), 1);
               this.encodeRepMatch(this.back, var1, var2);
            } else {
               assert this.lz.getMatchLen(-this.readAhead, this.back - 4, var1) == var1;

               this.rc.encodeBit(this.isRep, this.state.get(), 0);
               this.encodeMatch(this.back - 4, var1, var2);
            }
         }

         this.readAhead -= var1;
         this.uncompressedSize += var1;
         return true;
      }
   }

   private void encodeMatch(int i, int j, int k) throws IOException {
      this.state.updateMatch();
      this.matchLenEncoder.encode(j, k);
      int var4 = getDistSlot(i);
      this.rc.encodeBitTree(this.distSlots[getDistState(j)], var4);
      if (var4 >= 4) {
         int var5 = (var4 >>> 1) - 1;
         int var6 = (2 | var4 & 1) << var5;
         int var7 = i - var6;
         if (var4 < 14) {
            this.rc.encodeReverseBitTree(this.distSpecial[var4 - 4], var7);
         } else {
            this.rc.encodeDirectBits(var7 >>> 4, var5 - 4);
            this.rc.encodeReverseBitTree(this.distAlign, var7 & 15);
            this.alignPriceCount--;
         }
      }

      this.reps[3] = this.reps[2];
      this.reps[2] = this.reps[1];
      this.reps[1] = this.reps[0];
      this.reps[0] = i;
      this.distPriceCount--;
   }

   private void encodeRepMatch(int i, int j, int k) throws IOException {
      if (i == 0) {
         this.rc.encodeBit(this.isRep0, this.state.get(), 0);
         this.rc.encodeBit(this.isRep0Long[this.state.get()], k, j == 1 ? 0 : 1);
      } else {
         int var4 = this.reps[i];
         this.rc.encodeBit(this.isRep0, this.state.get(), 1);
         if (i == 1) {
            this.rc.encodeBit(this.isRep1, this.state.get(), 0);
         } else {
            this.rc.encodeBit(this.isRep1, this.state.get(), 1);
            this.rc.encodeBit(this.isRep2, this.state.get(), i - 2);
            if (i == 3) {
               this.reps[3] = this.reps[2];
            }

            this.reps[2] = this.reps[1];
         }

         this.reps[1] = this.reps[0];
         this.reps[0] = var4;
      }

      if (j == 1) {
         this.state.updateShortRep();
      } else {
         this.repLenEncoder.encode(j, k);
         this.state.updateLongRep();
      }
   }

   Matches getMatches() {
      this.readAhead++;
      Matches var1 = this.lz.getMatches();

      assert this.lz.verifyMatches(var1);

      return var1;
   }

   void skip(int i) {
      this.readAhead += i;
      this.lz.skip(i);
   }

   int getAnyMatchPrice(State state, int i) {
      return RangeEncoder.getBitPrice(this.isMatch[state.get()][i], 1);
   }

   int getNormalMatchPrice(int i, State state) {
      return i + RangeEncoder.getBitPrice(this.isRep[state.get()], 0);
   }

   int getAnyRepPrice(int i, State state) {
      return i + RangeEncoder.getBitPrice(this.isRep[state.get()], 1);
   }

   int getShortRepPrice(int i, State state, int j) {
      return i + RangeEncoder.getBitPrice(this.isRep0[state.get()], 0) + RangeEncoder.getBitPrice(this.isRep0Long[state.get()][j], 0);
   }

   int getLongRepPrice(int i, int j, State state, int k) {
      int var5;
      if (j == 0) {
         var5 = i + RangeEncoder.getBitPrice(this.isRep0[state.get()], 0) + RangeEncoder.getBitPrice(this.isRep0Long[state.get()][k], 1);
      } else {
         var5 = i + RangeEncoder.getBitPrice(this.isRep0[state.get()], 1);
         if (j == 1) {
            var5 += RangeEncoder.getBitPrice(this.isRep1[state.get()], 0);
         } else {
            var5 += RangeEncoder.getBitPrice(this.isRep1[state.get()], 1) + RangeEncoder.getBitPrice(this.isRep2[state.get()], j - 2);
         }
      }

      return var5;
   }

   int getLongRepAndLenPrice(int i, int j, State state, int k) {
      int var5 = this.getAnyMatchPrice(state, k);
      int var6 = this.getAnyRepPrice(var5, state);
      int var7 = this.getLongRepPrice(var6, i, state, k);
      return var7 + this.repLenEncoder.getPrice(j, k);
   }

   int getMatchAndLenPrice(int i, int j, int k, int l) {
      int var5 = i + this.matchLenEncoder.getPrice(k, l);
      int var6 = getDistState(k);
      if (j < 128) {
         var5 += this.fullDistPrices[var6][j];
      } else {
         int var7 = getDistSlot(j);
         var5 += this.distSlotPrices[var6][var7] + this.alignPrices[j & 15];
      }

      return var5;
   }

   private void updateDistPrices() {
      this.distPriceCount = 128;

      for (int var1 = 0; var1 < 4; var1++) {
         for (int var2 = 0; var2 < this.distSlotPricesSize; var2++) {
            this.distSlotPrices[var1][var2] = RangeEncoder.getBitTreePrice(this.distSlots[var1], var2);
         }

         for (int var11 = 14; var11 < this.distSlotPricesSize; var11++) {
            int var3 = (var11 >>> 1) - 1 - 4;
            this.distSlotPrices[var1][var11] = this.distSlotPrices[var1][var11] + RangeEncoder.getDirectBitsPrice(var3);
         }

         for (int var12 = 0; var12 < 4; var12++) {
            this.fullDistPrices[var1][var12] = this.distSlotPrices[var1][var12];
         }
      }

      int var10 = 4;

      for (int var13 = 4; var13 < 14; var13++) {
         int var14 = (var13 >>> 1) - 1;
         int var4 = (2 | var13 & 1) << var14;
         int var5 = this.distSpecial[var13 - 4].length;

         for (int var6 = 0; var6 < var5; var6++) {
            int var7 = var10 - var4;
            int var8 = RangeEncoder.getReverseBitTreePrice(this.distSpecial[var13 - 4], var7);

            for (int var9 = 0; var9 < 4; var9++) {
               this.fullDistPrices[var9][var10] = this.distSlotPrices[var9][var13] + var8;
            }

            var10++;
         }
      }

      assert var10 == 128;
   }

   private void updateAlignPrices() {
      this.alignPriceCount = 16;

      for (int var1 = 0; var1 < 16; var1++) {
         this.alignPrices[var1] = RangeEncoder.getReverseBitTreePrice(this.distAlign, var1);
      }
   }

   void updatePrices() {
      if (this.distPriceCount <= 0) {
         this.updateDistPrices();
      }

      if (this.alignPriceCount <= 0) {
         this.updateAlignPrices();
      }

      this.matchLenEncoder.updatePrices();
      this.repLenEncoder.updatePrices();
   }

   class LengthEncoder extends LZMACoder.LengthCoder {
      private static final int PRICE_UPDATE_INTERVAL = 32;
      private final int[] counters;
      private final int[][] prices;

      LengthEncoder(int i, int j) {
         int var4 = 1 << i;
         this.counters = new int[var4];
         int var5 = Math.max(j - 2 + 1, 16);
         this.prices = new int[var4][var5];
      }

      @Override
      void reset() {
         super.reset();

         for (int var1 = 0; var1 < this.counters.length; var1++) {
            this.counters[var1] = 0;
         }
      }

      void encode(int i, int j) throws IOException {
         i -= 2;
         if (i < 8) {
            LZMAEncoder.this.rc.encodeBit(this.choice, 0, 0);
            LZMAEncoder.this.rc.encodeBitTree(this.low[j], i);
         } else {
            LZMAEncoder.this.rc.encodeBit(this.choice, 0, 1);
            i -= 8;
            if (i < 8) {
               LZMAEncoder.this.rc.encodeBit(this.choice, 1, 0);
               LZMAEncoder.this.rc.encodeBitTree(this.mid[j], i);
            } else {
               LZMAEncoder.this.rc.encodeBit(this.choice, 1, 1);
               LZMAEncoder.this.rc.encodeBitTree(this.high, i - 8);
            }
         }

         this.counters[j]--;
      }

      int getPrice(int i, int j) {
         return this.prices[j][i - 2];
      }

      void updatePrices() {
         for (int var1 = 0; var1 < this.counters.length; var1++) {
            if (this.counters[var1] <= 0) {
               this.counters[var1] = 32;
               this.updatePrices(var1);
            }
         }
      }

      private void updatePrices(int i) {
         int var2 = RangeEncoder.getBitPrice(this.choice[0], 0);

         int var3;
         for (var3 = 0; var3 < 8; var3++) {
            this.prices[i][var3] = var2 + RangeEncoder.getBitTreePrice(this.low[i], var3);
         }

         var2 = RangeEncoder.getBitPrice(this.choice[0], 1);

         for (int var4 = RangeEncoder.getBitPrice(this.choice[1], 0); var3 < 16; var3++) {
            this.prices[i][var3] = var2 + var4 + RangeEncoder.getBitTreePrice(this.mid[i], var3 - 8);
         }

         for (int var6 = RangeEncoder.getBitPrice(this.choice[1], 1); var3 < this.prices[i].length; var3++) {
            this.prices[i][var3] = var2 + var6 + RangeEncoder.getBitTreePrice(this.high, var3 - 8 - 8);
         }
      }
   }

   class LiteralEncoder extends LZMACoder.LiteralCoder {
      private final LZMAEncoder.LiteralEncoder.LiteralSubencoder[] subencoders;

      LiteralEncoder(int i, int j) {
         super(i, j);
         this.subencoders = new LZMAEncoder.LiteralEncoder.LiteralSubencoder[1 << i + j];

         for (int var4 = 0; var4 < this.subencoders.length; var4++) {
            this.subencoders[var4] = new LZMAEncoder.LiteralEncoder.LiteralSubencoder();
         }
      }

      void reset() {
         for (int var1 = 0; var1 < this.subencoders.length; var1++) {
            this.subencoders[var1].reset();
         }
      }

      void encodeInit() throws IOException {
         assert LZMAEncoder.this.readAhead >= 0;

         this.subencoders[0].encode();
      }

      void encode() throws IOException {
         assert LZMAEncoder.this.readAhead >= 0;

         int var1 = this.getSubcoderIndex(
            LZMAEncoder.this.lz.getByte(1 + LZMAEncoder.this.readAhead), LZMAEncoder.this.lz.getPos() - LZMAEncoder.this.readAhead
         );
         this.subencoders[var1].encode();
      }

      int getPrice(int i, int j, int k, int l, State state) {
         int var6 = RangeEncoder.getBitPrice(LZMAEncoder.this.isMatch[state.get()][l & LZMAEncoder.this.posMask], 0);
         int var7 = this.getSubcoderIndex(k, l);
         return var6 + (state.isLiteral() ? this.subencoders[var7].getNormalPrice(i) : this.subencoders[var7].getMatchedPrice(i, j));
      }

      private class LiteralSubencoder extends LZMACoder.LiteralCoder.LiteralSubcoder {
         private LiteralSubencoder() {
         }

         void encode() throws IOException {
            int var1 = LZMAEncoder.this.lz.getByte(LZMAEncoder.this.readAhead) | 256;
            if (LZMAEncoder.this.state.isLiteral()) {
               do {
                  int var2 = var1 >>> 8;
                  int var3 = var1 >>> 7 & 1;
                  LZMAEncoder.this.rc.encodeBit(this.probs, var2, var3);
                  var1 <<= 1;
               } while (var1 < 65536);
            } else {
               int var7 = LZMAEncoder.this.lz.getByte(LZMAEncoder.this.reps[0] + 1 + LZMAEncoder.this.readAhead);
               int var8 = 256;

               do {
                  var7 <<= 1;
                  int var5 = var7 & var8;
                  int var4 = var8 + var5 + (var1 >>> 8);
                  int var6 = var1 >>> 7 & 1;
                  LZMAEncoder.this.rc.encodeBit(this.probs, var4, var6);
                  var1 <<= 1;
                  var8 &= ~(var7 ^ var1);
               } while (var1 < 65536);
            }

            LZMAEncoder.this.state.updateLiteral();
         }

         int getNormalPrice(int i) {
            int var2 = 0;
            i |= 256;

            do {
               int var3 = i >>> 8;
               int var4 = i >>> 7 & 1;
               var2 += RangeEncoder.getBitPrice(this.probs[var3], var4);
               i <<= 1;
            } while (i < 65536);

            return var2;
         }

         int getMatchedPrice(int i, int j) {
            int var3 = 0;
            int var4 = 256;
            i |= 256;

            do {
               j <<= 1;
               int var6 = j & var4;
               int var5 = var4 + var6 + (i >>> 8);
               int var7 = i >>> 7 & 1;
               var3 += RangeEncoder.getBitPrice(this.probs[var5], var7);
               i <<= 1;
               var4 &= ~(j ^ i);
            } while (i < 65536);

            return var3;
         }
      }
   }
}
