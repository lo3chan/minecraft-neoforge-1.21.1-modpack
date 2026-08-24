package org.tukaani.xz.delta;

public class DeltaDecoder extends DeltaCoder {
   public DeltaDecoder(int i) {
      super(i);
   }

   public void decode(byte[] bs, int i, int j) {
      int var4 = i + j;

      for (int var5 = i; var5 < var4; var5++) {
         bs[var5] += this.history[this.distance + this.pos & 0xFF];
         this.history[this.pos-- & 0xFF] = bs[var5];
      }
   }
}
