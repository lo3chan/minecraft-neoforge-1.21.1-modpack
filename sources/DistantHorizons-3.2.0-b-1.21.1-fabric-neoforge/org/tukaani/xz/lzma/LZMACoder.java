package org.tukaani.xz.lzma;

import org.tukaani.xz.rangecoder.RangeCoder;

abstract class LZMACoder {
   static final int POS_STATES_MAX = 16;
   static final int MATCH_LEN_MIN = 2;
   static final int MATCH_LEN_MAX = 273;
   static final int DIST_STATES = 4;
   static final int DIST_SLOTS = 64;
   static final int DIST_MODEL_START = 4;
   static final int DIST_MODEL_END = 14;
   static final int FULL_DISTANCES = 128;
   static final int ALIGN_BITS = 4;
   static final int ALIGN_SIZE = 16;
   static final int ALIGN_MASK = 15;
   static final int REPS = 4;
   final int posMask;
   final int[] reps = new int[4];
   final State state = new State();
   final short[][] isMatch = new short[12][16];
   final short[] isRep = new short[12];
   final short[] isRep0 = new short[12];
   final short[] isRep1 = new short[12];
   final short[] isRep2 = new short[12];
   final short[][] isRep0Long = new short[12][16];
   final short[][] distSlots = new short[4][64];
   final short[][] distSpecial = new short[][]{
      new short[2], new short[2], new short[4], new short[4], new short[8], new short[8], new short[16], new short[16], new short[32], new short[32]
   };
   final short[] distAlign = new short[16];

   static final int getDistState(int i) {
      return i < 6 ? i - 2 : 3;
   }

   LZMACoder(int i) {
      this.posMask = (1 << i) - 1;
   }

   void reset() {
      this.reps[0] = 0;
      this.reps[1] = 0;
      this.reps[2] = 0;
      this.reps[3] = 0;
      this.state.reset();

      for (int var1 = 0; var1 < this.isMatch.length; var1++) {
         RangeCoder.initProbs(this.isMatch[var1]);
      }

      RangeCoder.initProbs(this.isRep);
      RangeCoder.initProbs(this.isRep0);
      RangeCoder.initProbs(this.isRep1);
      RangeCoder.initProbs(this.isRep2);

      for (int var2 = 0; var2 < this.isRep0Long.length; var2++) {
         RangeCoder.initProbs(this.isRep0Long[var2]);
      }

      for (int var3 = 0; var3 < this.distSlots.length; var3++) {
         RangeCoder.initProbs(this.distSlots[var3]);
      }

      for (int var4 = 0; var4 < this.distSpecial.length; var4++) {
         RangeCoder.initProbs(this.distSpecial[var4]);
      }

      RangeCoder.initProbs(this.distAlign);
   }

   abstract class LengthCoder {
      static final int LOW_SYMBOLS = 8;
      static final int MID_SYMBOLS = 8;
      static final int HIGH_SYMBOLS = 256;
      final short[] choice = new short[2];
      final short[][] low = new short[16][8];
      final short[][] mid = new short[16][8];
      final short[] high = new short[256];

      void reset() {
         RangeCoder.initProbs(this.choice);

         for (int var1 = 0; var1 < this.low.length; var1++) {
            RangeCoder.initProbs(this.low[var1]);
         }

         for (int var2 = 0; var2 < this.low.length; var2++) {
            RangeCoder.initProbs(this.mid[var2]);
         }

         RangeCoder.initProbs(this.high);
      }
   }

   abstract class LiteralCoder {
      private final int lc;
      private final int literalPosMask;

      LiteralCoder(int i, int j) {
         this.lc = i;
         this.literalPosMask = (1 << j) - 1;
      }

      final int getSubcoderIndex(int i, int j) {
         int var3 = i >> 8 - this.lc;
         int var4 = (j & this.literalPosMask) << this.lc;
         return var3 + var4;
      }

      abstract class LiteralSubcoder {
         final short[] probs = new short[768];

         void reset() {
            RangeCoder.initProbs(this.probs);
         }
      }
   }
}
