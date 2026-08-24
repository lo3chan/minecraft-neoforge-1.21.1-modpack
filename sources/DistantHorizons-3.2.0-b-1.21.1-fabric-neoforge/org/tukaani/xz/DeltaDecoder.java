package org.tukaani.xz;

import java.io.InputStream;

class DeltaDecoder extends DeltaCoder implements FilterDecoder {
   private final int distance;

   DeltaDecoder(byte[] bs) throws UnsupportedOptionsException {
      if (bs.length != 1) {
         throw new UnsupportedOptionsException("Unsupported Delta filter properties");
      } else {
         this.distance = (bs[0] & 255) + 1;
      }
   }

   @Override
   public int getMemoryUsage() {
      return 1;
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      return new DeltaInputStream(inputStream, this.distance);
   }
}
