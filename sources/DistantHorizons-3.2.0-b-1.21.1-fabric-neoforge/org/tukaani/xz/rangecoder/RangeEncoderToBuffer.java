package org.tukaani.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.ArrayCache;

public final class RangeEncoderToBuffer extends RangeEncoder {
   private final byte[] buf;
   private int bufPos;

   public RangeEncoderToBuffer(int i, ArrayCache arrayCache) {
      this.buf = arrayCache.getByteArray(i, false);
      this.reset();
   }

   public void putArraysToCache(ArrayCache arrayCache) {
      arrayCache.putArray(this.buf);
   }

   @Override
   public void reset() {
      super.reset();
      this.bufPos = 0;
   }

   @Override
   public int getPendingSize() {
      return this.bufPos + (int)this.cacheSize + 5 - 1;
   }

   @Override
   public int finish() {
      try {
         super.finish();
      } catch (IOException var2) {
         throw new Error();
      }

      return this.bufPos;
   }

   public void write(OutputStream outputStream) throws IOException {
      outputStream.write(this.buf, 0, this.bufPos);
   }

   @Override
   void writeByte(int i) {
      this.buf[this.bufPos++] = (byte)i;
   }
}
