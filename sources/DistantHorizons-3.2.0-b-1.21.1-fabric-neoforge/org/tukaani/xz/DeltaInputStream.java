package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public class DeltaInputStream extends InputStream {
   public static final int DISTANCE_MIN = 1;
   public static final int DISTANCE_MAX = 256;
   private InputStream in;
   private final org.tukaani.xz.delta.DeltaDecoder delta;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   public DeltaInputStream(InputStream inputStream, int i) {
      if (inputStream == null) {
         throw new NullPointerException();
      } else {
         this.in = inputStream;
         this.delta = new org.tukaani.xz.delta.DeltaDecoder(i);
      }
   }

   @Override
   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 0xFF;
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      if (j == 0) {
         return 0;
      } else if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         int var4;
         try {
            var4 = this.in.read(bs, i, j);
         } catch (IOException var6) {
            this.exception = var6;
            throw var6;
         }

         if (var4 == -1) {
            return -1;
         } else {
            this.delta.decode(bs, i, var4);
            return var4;
         }
      }
   }

   @Override
   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return this.in.available();
      }
   }

   @Override
   public void close() throws IOException {
      if (this.in != null) {
         try {
            this.in.close();
         } finally {
            this.in = null;
         }
      }
   }
}
