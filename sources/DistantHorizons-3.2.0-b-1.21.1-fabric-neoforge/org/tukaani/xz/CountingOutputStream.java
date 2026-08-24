package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FinishableOutputStream {
   private final OutputStream out;
   private long size = 0L;

   public CountingOutputStream(OutputStream outputStream) {
      this.out = outputStream;
   }

   @Override
   public void write(int i) throws IOException {
      this.out.write(i);
      if (this.size >= 0L) {
         this.size++;
      }
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      this.out.write(bs, i, j);
      if (this.size >= 0L) {
         this.size += j;
      }
   }

   @Override
   public void flush() throws IOException {
      this.out.flush();
   }

   @Override
   public void close() throws IOException {
      this.out.close();
   }

   public long getSize() {
      return this.size;
   }
}
