package org.tukaani.xz;

import java.io.InputStream;

class LZMA2Decoder extends LZMA2Coder implements FilterDecoder {
   private int dictSize;

   LZMA2Decoder(byte[] bs) throws UnsupportedOptionsException {
      if (bs.length == 1 && (bs[0] & 255) <= 37) {
         this.dictSize = 2 | bs[0] & 1;
         this.dictSize = this.dictSize << (bs[0] >>> 1) + 11;
      } else {
         throw new UnsupportedOptionsException("Unsupported LZMA2 properties");
      }
   }

   @Override
   public int getMemoryUsage() {
      return LZMA2InputStream.getMemoryUsage(this.dictSize);
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      return new LZMA2InputStream(inputStream, this.dictSize, null, arrayCache);
   }
}
