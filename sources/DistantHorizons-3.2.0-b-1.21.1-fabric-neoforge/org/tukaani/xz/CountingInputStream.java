package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

class CountingInputStream extends CloseIgnoringInputStream {
   private long size = 0L;

   public CountingInputStream(InputStream inputStream) {
      super(inputStream);
   }

   @Override
   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 != -1 && this.size >= 0L) {
         this.size++;
      }

      return var1;
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      int var4 = this.in.read(bs, i, j);
      if (var4 > 0 && this.size >= 0L) {
         this.size += var4;
      }

      return var4;
   }

   public long getSize() {
      return this.size;
   }
}
