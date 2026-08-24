package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;

public class FinishableWrapperOutputStream extends FinishableOutputStream {
   protected OutputStream out;

   public FinishableWrapperOutputStream(OutputStream outputStream) {
      this.out = outputStream;
   }

   @Override
   public void write(int i) throws IOException {
      this.out.write(i);
   }

   @Override
   public void write(byte[] bs) throws IOException {
      this.out.write(bs);
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      this.out.write(bs, i, j);
   }

   @Override
   public void flush() throws IOException {
      this.out.flush();
   }

   @Override
   public void close() throws IOException {
      this.out.close();
   }
}
