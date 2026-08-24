package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.simple.SimpleFilter;

class SimpleOutputStream extends FinishableOutputStream {
   private static final int FILTER_BUF_SIZE = 4096;
   private FinishableOutputStream out;
   private final SimpleFilter simpleFilter;
   private final byte[] filterBuf = new byte[4096];
   private int pos = 0;
   private int unfiltered = 0;
   private IOException exception = null;
   private boolean finished = false;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 5;
   }

   SimpleOutputStream(FinishableOutputStream finishableOutputStream, SimpleFilter simpleFilter) {
      if (finishableOutputStream == null) {
         throw new NullPointerException();
      } else {
         this.out = finishableOutputStream;
         this.simpleFilter = simpleFilter;
      }
   }

   @Override
   public void write(int i) throws IOException {
      this.tempBuf[0] = (byte)i;
      this.write(this.tempBuf, 0, 1);
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      if (i >= 0 && j >= 0 && i + j >= 0 && i + j <= bs.length) {
         if (this.exception != null) {
            throw this.exception;
         } else if (this.finished) {
            throw new XZIOException("Stream finished or closed");
         } else {
            while (j > 0) {
               int var4 = Math.min(j, 4096 - (this.pos + this.unfiltered));
               System.arraycopy(bs, i, this.filterBuf, this.pos + this.unfiltered, var4);
               i += var4;
               j -= var4;
               this.unfiltered += var4;
               int var5 = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);

               assert var5 <= this.unfiltered;

               this.unfiltered -= var5;

               try {
                  this.out.write(this.filterBuf, this.pos, var5);
               } catch (IOException var7) {
                  this.exception = var7;
                  throw var7;
               }

               this.pos += var5;
               if (this.pos + this.unfiltered == 4096) {
                  System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.unfiltered);
                  this.pos = 0;
               }
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void writePending() throws IOException {
      assert !this.finished;

      if (this.exception != null) {
         throw this.exception;
      } else {
         try {
            this.out.write(this.filterBuf, this.pos, this.unfiltered);
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
      }
   }

   @Override
   public void flush() throws IOException {
      throw new UnsupportedOptionsException("Flushing is not supported");
   }

   @Override
   public void finish() throws IOException {
      if (!this.finished) {
         this.writePending();

         try {
            this.out.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   @Override
   public void close() throws IOException {
      if (this.out != null) {
         if (!this.finished) {
            try {
               this.writePending();
            } catch (IOException var2) {
            }
         }

         try {
            this.out.close();
         } catch (IOException var3) {
            if (this.exception == null) {
               this.exception = var3;
            }
         }

         this.out = null;
      }

      if (this.exception != null) {
         throw this.exception;
      }
   }
}
