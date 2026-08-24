package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.simple.SimpleFilter;

class SimpleInputStream extends InputStream {
   private static final int FILTER_BUF_SIZE = 4096;
   private InputStream in;
   private final SimpleFilter simpleFilter;
   private final byte[] filterBuf = new byte[4096];
   private int pos = 0;
   private int filtered = 0;
   private int unfiltered = 0;
   private boolean endReached = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 5;
   }

   SimpleInputStream(InputStream inputStream, SimpleFilter simpleFilter) {
      if (inputStream == null) {
         throw new NullPointerException();
      } else {
         assert simpleFilter != null;

         this.in = inputStream;
         this.simpleFilter = simpleFilter;
      }
   }

   @Override
   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 0xFF;
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      if (i < 0 || j < 0 || i + j < 0 || i + j > bs.length) {
         throw new IndexOutOfBoundsException();
      } else if (j == 0) {
         return 0;
      } else if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         try {
            int var4 = 0;

            while (true) {
               int var5 = Math.min(this.filtered, j);
               System.arraycopy(this.filterBuf, this.pos, bs, i, var5);
               this.pos += var5;
               this.filtered -= var5;
               i += var5;
               j -= var5;
               var4 += var5;
               if (this.pos + this.filtered + this.unfiltered == 4096) {
                  System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.filtered + this.unfiltered);
                  this.pos = 0;
               }

               if (j == 0 || this.endReached) {
                  return var4 > 0 ? var4 : -1;
               }

               assert this.filtered == 0;

               int var6 = 4096 - (this.pos + this.filtered + this.unfiltered);
               var6 = this.in.read(this.filterBuf, this.pos + this.filtered + this.unfiltered, var6);
               if (var6 == -1) {
                  this.endReached = true;
                  this.filtered = this.unfiltered;
                  this.unfiltered = 0;
               } else {
                  this.unfiltered += var6;
                  this.filtered = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);

                  assert this.filtered <= this.unfiltered;

                  this.unfiltered = this.unfiltered - this.filtered;
               }
            }
         } catch (IOException var7) {
            this.exception = var7;
            throw var7;
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
         return this.filtered;
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
