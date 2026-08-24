package org.tukaani.xz.lz;

import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.ArrayCache;
import org.tukaani.xz.CorruptedInputException;

public final class LZDecoder {
   private final byte[] buf;
   private final int bufSize;
   private int start = 0;
   private int pos = 0;
   private int full = 0;
   private int limit = 0;
   private int pendingLen = 0;
   private int pendingDist = 0;

   public LZDecoder(int i, byte[] bs, ArrayCache arrayCache) {
      this.bufSize = i;
      this.buf = arrayCache.getByteArray(this.bufSize, false);
      if (bs != null) {
         this.pos = Math.min(bs.length, i);
         this.full = this.pos;
         this.start = this.pos;
         System.arraycopy(bs, bs.length - this.pos, this.buf, 0, this.pos);
      }
   }

   public void putArraysToCache(ArrayCache arrayCache) {
      arrayCache.putArray(this.buf);
   }

   public void reset() {
      this.start = 0;
      this.pos = 0;
      this.full = 0;
      this.limit = 0;
      this.buf[this.bufSize - 1] = 0;
   }

   public void setLimit(int i) {
      if (this.bufSize - this.pos <= i) {
         this.limit = this.bufSize;
      } else {
         this.limit = this.pos + i;
      }
   }

   public boolean hasSpace() {
      return this.pos < this.limit;
   }

   public boolean hasPending() {
      return this.pendingLen > 0;
   }

   public int getPos() {
      return this.pos;
   }

   public int getByte(int i) {
      int var2 = this.pos - i - 1;
      if (i >= this.pos) {
         var2 += this.bufSize;
      }

      return this.buf[var2] & 0xFF;
   }

   public void putByte(byte b) {
      this.buf[this.pos++] = b;
      if (this.full < this.pos) {
         this.full = this.pos;
      }
   }

   public void repeat(int i, int j) throws IOException {
      if (i >= 0 && i < this.full) {
         int var3 = Math.min(this.limit - this.pos, j);
         this.pendingLen = j - var3;
         this.pendingDist = i;
         int var4 = this.pos - i - 1;
         if (var4 < 0) {
            assert this.full == this.bufSize;

            var4 += this.bufSize;
            int var5 = Math.min(this.bufSize - var4, var3);

            assert var5 <= i + 1;

            System.arraycopy(this.buf, var4, this.buf, this.pos, var5);
            this.pos += var5;
            var4 = 0;
            var3 -= var5;
            if (var3 == 0) {
               return;
            }
         }

         assert var4 < this.pos;

         assert var3 > 0;

         do {
            int var7 = Math.min(var3, this.pos - var4);
            System.arraycopy(this.buf, var4, this.buf, this.pos, var7);
            this.pos += var7;
            var3 -= var7;
         } while (var3 > 0);

         if (this.full < this.pos) {
            this.full = this.pos;
         }
      } else {
         throw new CorruptedInputException();
      }
   }

   public void repeatPending() throws IOException {
      if (this.pendingLen > 0) {
         this.repeat(this.pendingDist, this.pendingLen);
      }
   }

   public void copyUncompressed(DataInputStream dataInputStream, int i) throws IOException {
      int var3 = Math.min(this.bufSize - this.pos, i);
      dataInputStream.readFully(this.buf, this.pos, var3);
      this.pos += var3;
      if (this.full < this.pos) {
         this.full = this.pos;
      }
   }

   public int flush(byte[] bs, int i) {
      int var3 = this.pos - this.start;
      if (this.pos == this.bufSize) {
         this.pos = 0;
      }

      System.arraycopy(this.buf, this.start, bs, i, var3);
      this.start = this.pos;
      return var3;
   }
}
