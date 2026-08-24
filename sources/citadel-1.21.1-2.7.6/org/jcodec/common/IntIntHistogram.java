package org.jcodec.common;

public class IntIntHistogram extends IntIntMap {
   private int maxBin = -1;

   public int max() {
      return this.maxBin;
   }

   public void increment(int bin) {
      int count = this.get(bin);
      count = count == -2147483648 ? 1 : 1 + count;
      this.put(bin, count);
      if (this.maxBin == -1) {
         this.maxBin = bin;
      }

      int maxCount = this.get(this.maxBin);
      if (count > maxCount) {
         this.maxBin = bin;
      }
   }
}
