package org.tukaani.xz.rangecoder;

import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.CorruptedInputException;

public final class RangeDecoderFromBuffer extends RangeDecoder {
   private static final int INIT_SIZE = 5;
   private final byte[] buf;
   private int pos;

   public RangeDecoderFromBuffer(int i, ArrayCache arrayCache) {
      this.buf = arrayCache.getByteArray(i - 5, false);
      this.pos = this.buf.length;
   }

   public void putArraysToCache(ArrayCache arrayCache) {
      arrayCache.putArray(this.buf);
   }

   public void prepareInputBuffer(DataInputStream dataInputStream, int i) throws IOException {
      if (i < 5) {
         throw new CorruptedInputException();
      } else if (dataInputStream.readUnsignedByte() != 0) {
         throw new CorruptedInputException();
      } else {
         this.code = dataInputStream.readInt();
         this.range = -1;
         i -= 5;
         this.pos = this.buf.length - i;
         dataInputStream.readFully(this.buf, this.pos, i);
      }
   }

   public boolean isFinished() {
      return this.pos == this.buf.length && this.code == 0;
   }

   @Override
   public void normalize() throws IOException {
      if ((this.range & 0xFF000000) == 0) {
         try {
            this.code = this.code << 8 | this.buf[this.pos++] & 255;
            this.range <<= 8;
         } catch (ArrayIndexOutOfBoundsException var2) {
            throw new CorruptedInputException();
         }
      }
   }
}
