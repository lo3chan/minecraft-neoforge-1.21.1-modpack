package org.jcodec.common.dct;

import org.jcodec.api.NotSupportedException;

public abstract class DCT {
   public short[] encode(byte[] orig) {
      throw new NotSupportedException("");
   }

   public abstract int[] decode(int[] var1);

   public void decodeAll(int[][] src) {
      for (int i = 0; i < src.length; i++) {
         src[i] = this.decode(src[i]);
      }
   }
}
