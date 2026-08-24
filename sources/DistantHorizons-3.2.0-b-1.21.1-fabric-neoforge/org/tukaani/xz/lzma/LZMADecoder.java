package org.tukaani.xz.lzma;

import java.io.IOException;
import org.tukaani.xz.lz.LZDecoder;
import org.tukaani.xz.rangecoder.RangeDecoder;

public final class LZMADecoder extends LZMACoder {
   private final LZDecoder lz;
   private final RangeDecoder rc;
   private final LZMADecoder.LiteralDecoder literalDecoder;
   private final LZMADecoder.LengthDecoder matchLenDecoder = new LZMADecoder.LengthDecoder();
   private final LZMADecoder.LengthDecoder repLenDecoder = new LZMADecoder.LengthDecoder();

   public LZMADecoder(LZDecoder lZDecoder, RangeDecoder rangeDecoder, int i, int j, int k) {
      super(k);
      this.lz = lZDecoder;
      this.rc = rangeDecoder;
      this.literalDecoder = new LZMADecoder.LiteralDecoder(i, j);
      this.reset();
   }

   @Override
   public void reset() {
      super.reset();
      this.literalDecoder.reset();
      this.matchLenDecoder.reset();
      this.repLenDecoder.reset();
   }

   public boolean endMarkerDetected() {
      return this.reps[0] == -1;
   }

   public void decode() throws IOException {
      this.lz.repeatPending();

      while (this.lz.hasSpace()) {
         int var1 = this.lz.getPos() & this.posMask;
         if (this.rc.decodeBit(this.isMatch[this.state.get()], var1) == 0) {
            this.literalDecoder.decode();
         } else {
            int var2 = this.rc.decodeBit(this.isRep, this.state.get()) == 0 ? this.decodeMatch(var1) : this.decodeRepMatch(var1);
            this.lz.repeat(this.reps[0], var2);
         }
      }

      this.rc.normalize();
   }

   private int decodeMatch(int i) throws IOException {
      this.state.updateMatch();
      this.reps[3] = this.reps[2];
      this.reps[2] = this.reps[1];
      this.reps[1] = this.reps[0];
      int var2 = this.matchLenDecoder.decode(i);
      int var3 = this.rc.decodeBitTree(this.distSlots[getDistState(var2)]);
      if (var3 < 4) {
         this.reps[0] = var3;
      } else {
         int var4 = (var3 >> 1) - 1;
         this.reps[0] = (2 | var3 & 1) << var4;
         if (var3 < 14) {
            this.reps[0] = this.reps[0] | this.rc.decodeReverseBitTree(this.distSpecial[var3 - 4]);
         } else {
            this.reps[0] = this.reps[0] | this.rc.decodeDirectBits(var4 - 4) << 4;
            this.reps[0] = this.reps[0] | this.rc.decodeReverseBitTree(this.distAlign);
         }
      }

      return var2;
   }

   private int decodeRepMatch(int i) throws IOException {
      if (this.rc.decodeBit(this.isRep0, this.state.get()) == 0) {
         if (this.rc.decodeBit(this.isRep0Long[this.state.get()], i) == 0) {
            this.state.updateShortRep();
            return 1;
         }
      } else {
         int var2;
         if (this.rc.decodeBit(this.isRep1, this.state.get()) == 0) {
            var2 = this.reps[1];
         } else {
            if (this.rc.decodeBit(this.isRep2, this.state.get()) == 0) {
               var2 = this.reps[2];
            } else {
               var2 = this.reps[3];
               this.reps[3] = this.reps[2];
            }

            this.reps[2] = this.reps[1];
         }

         this.reps[1] = this.reps[0];
         this.reps[0] = var2;
      }

      this.state.updateLongRep();
      return this.repLenDecoder.decode(i);
   }

   private class LengthDecoder extends LZMACoder.LengthCoder {
      private LengthDecoder() {
      }

      int decode(int i) throws IOException {
         if (LZMADecoder.this.rc.decodeBit(this.choice, 0) == 0) {
            return LZMADecoder.this.rc.decodeBitTree(this.low[i]) + 2;
         } else {
            return LZMADecoder.this.rc.decodeBit(this.choice, 1) == 0
               ? LZMADecoder.this.rc.decodeBitTree(this.mid[i]) + 2 + 8
               : LZMADecoder.this.rc.decodeBitTree(this.high) + 2 + 8 + 8;
         }
      }
   }

   private class LiteralDecoder extends LZMACoder.LiteralCoder {
      private final LZMADecoder.LiteralDecoder.LiteralSubdecoder[] subdecoders;

      LiteralDecoder(int i, int j) {
         super(i, j);
         this.subdecoders = new LZMADecoder.LiteralDecoder.LiteralSubdecoder[1 << i + j];

         for (int var4 = 0; var4 < this.subdecoders.length; var4++) {
            this.subdecoders[var4] = new LZMADecoder.LiteralDecoder.LiteralSubdecoder();
         }
      }

      void reset() {
         for (int var1 = 0; var1 < this.subdecoders.length; var1++) {
            this.subdecoders[var1].reset();
         }
      }

      void decode() throws IOException {
         int var1 = this.getSubcoderIndex(LZMADecoder.this.lz.getByte(0), LZMADecoder.this.lz.getPos());
         this.subdecoders[var1].decode();
      }

      private class LiteralSubdecoder extends LZMACoder.LiteralCoder.LiteralSubcoder {
         private LiteralSubdecoder() {
         }

         void decode() throws IOException {
            int var1 = 1;
            if (LZMADecoder.this.state.isLiteral()) {
               do {
                  var1 = var1 << 1 | LZMADecoder.this.rc.decodeBit(this.probs, var1);
               } while (var1 < 256);
            } else {
               int var2 = LZMADecoder.this.lz.getByte(LZMADecoder.this.reps[0]);
               int var3 = 256;

               do {
                  var2 <<= 1;
                  int var4 = var2 & var3;
                  int var5 = LZMADecoder.this.rc.decodeBit(this.probs, var3 + var4 + var1);
                  var1 = var1 << 1 | var5;
                  var3 &= 0 - var5 ^ ~var4;
               } while (var1 < 256);
            }

            LZMADecoder.this.lz.putByte((byte)var1);
            LZMADecoder.this.state.updateLiteral();
         }
      }
   }
}
