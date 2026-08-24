package org.tukaani.xz.delta;

public class DeltaEncoder extends DeltaCoder {
   public DeltaEncoder(int i) {
      super(i);
   }

   public void encode(byte[] bs, int i, int j, byte[] cs) {
      for (int var5 = 0; var5 < j; var5++) {
         byte var6 = this.history[this.distance + this.pos & 0xFF];
         this.history[this.pos-- & 0xFF] = bs[i + var5];
         cs[var5] = (byte)(bs[i + var5] - var6);
      }
   }
}
