package org.tukaani.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;

public final class RangeEncoderToStream extends RangeEncoder {
   private final OutputStream out;

   public RangeEncoderToStream(OutputStream outputStream) {
      this.out = outputStream;
      this.reset();
   }

   @Override
   void writeByte(int i) throws IOException {
      this.out.write(i);
   }
}
