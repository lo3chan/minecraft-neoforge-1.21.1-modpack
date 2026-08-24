package org.tukaani.xz;

import java.io.IOException;

class DeltaOutputStream extends FinishableOutputStream {
   private static final int FILTER_BUF_SIZE = 4096;
   private FinishableOutputStream out;
   private final org.tukaani.xz.delta.DeltaEncoder delta;
   private final byte[] filterBuf = new byte[4096];
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 5;
   }

   DeltaOutputStream(FinishableOutputStream finishableOutputStream, DeltaOptions deltaOptions) {
      this.out = finishableOutputStream;
      this.delta = new org.tukaani.xz.delta.DeltaEncoder(deltaOptions.getDistance());
   }

   @Override
   public void write(int i) throws IOException {
      this.tempBuf[0] = (byte)i;
      this.write(this.tempBuf, 0, 1);
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      if (i < 0 || j < 0 || i + j < 0 || i + j > bs.length) {
         throw new IndexOutOfBoundsException();
      } else if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished");
      } else {
         try {
            while (j > 4096) {
               this.delta.encode(bs, i, 4096, this.filterBuf);
               this.out.write(this.filterBuf);
               i += 4096;
               j -= 4096;
            }

            this.delta.encode(bs, i, j, this.filterBuf);
            this.out.write(this.filterBuf, 0, j);
         } catch (IOException var5) {
            this.exception = var5;
            throw var5;
         }
      }
   }

   @Override
   public void flush() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            this.out.flush();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   @Override
   public void finish() throws IOException {
      if (!this.finished) {
         if (this.exception != null) {
            throw this.exception;
         }

         try {
            this.out.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
      }
   }

   @Override
   public void close() throws IOException {
      if (this.out != null) {
         try {
            this.out.close();
         } catch (IOException var2) {
            if (this.exception == null) {
               this.exception = var2;
            }
         }

         this.out = null;
      }

      if (this.exception != null) {
         throw this.exception;
      }
   }
}
