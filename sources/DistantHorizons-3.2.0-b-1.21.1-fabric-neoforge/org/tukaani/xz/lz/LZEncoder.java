package org.tukaani.xz.lz;

import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.ArrayCache;

public abstract class LZEncoder {
   public static final int MF_HC4 = 4;
   public static final int MF_BT4 = 20;
   private final int keepSizeBefore;
   private final int keepSizeAfter;
   final int matchLenMax;
   final int niceLen;
   final byte[] buf;
   final int bufSize;
   int readPos = -1;
   private int readLimit = -1;
   private boolean finishing = false;
   private int writePos = 0;
   private int pendingSize = 0;

   static void normalize(int[] is, int i, int j) {
      for (int var3 = 0; var3 < i; var3++) {
         if (is[var3] <= j) {
            is[var3] = 0;
         } else {
            is[var3] -= j;
         }
      }
   }

   private static int getBufSize(int i, int j, int k, int l) {
      int var4 = j + i;
      int var5 = k + l;
      int var6 = Math.min(i / 2 + 262144, 536870912);
      return var4 + var5 + var6;
   }

   public static int getMemoryUsage(int i, int j, int k, int l, int m) {
      int var5 = getBufSize(i, j, k, l) / 1024 + 10;
      switch (m) {
         case 4:
            var5 += HC4.getMemoryUsage(i);
            break;
         case 20:
            var5 += BT4.getMemoryUsage(i);
            break;
         default:
            throw new IllegalArgumentException();
      }

      return var5;
   }

   public static LZEncoder getInstance(int i, int j, int k, int l, int m, int n, int o, ArrayCache arrayCache) {
      switch (n) {
         case 4:
            return new HC4(i, j, k, l, m, o, arrayCache);
         case 20:
            return new BT4(i, j, k, l, m, o, arrayCache);
         default:
            throw new IllegalArgumentException();
      }
   }

   LZEncoder(int i, int j, int k, int l, int m, ArrayCache arrayCache) {
      this.bufSize = getBufSize(i, j, k, m);
      this.buf = arrayCache.getByteArray(this.bufSize, false);
      this.keepSizeBefore = j + i;
      this.keepSizeAfter = k + m;
      this.matchLenMax = m;
      this.niceLen = l;
   }

   public void putArraysToCache(ArrayCache arrayCache) {
      arrayCache.putArray(this.buf);
   }

   public void setPresetDict(int i, byte[] bs) {
      assert !this.isStarted();

      assert this.writePos == 0;

      if (bs != null) {
         int var3 = Math.min(bs.length, i);
         int var4 = bs.length - var3;
         System.arraycopy(bs, var4, this.buf, 0, var3);
         this.writePos += var3;
         this.skip(var3);
      }
   }

   private void moveWindow() {
      int var1 = this.readPos + 1 - this.keepSizeBefore & -16;
      int var2 = this.writePos - var1;
      System.arraycopy(this.buf, var1, this.buf, 0, var2);
      this.readPos -= var1;
      this.readLimit -= var1;
      this.writePos -= var1;
   }

   public int fillWindow(byte[] bs, int i, int j) {
      assert !this.finishing;

      if (this.readPos >= this.bufSize - this.keepSizeAfter) {
         this.moveWindow();
      }

      if (j > this.bufSize - this.writePos) {
         j = this.bufSize - this.writePos;
      }

      System.arraycopy(bs, i, this.buf, this.writePos, j);
      this.writePos += j;
      if (this.writePos >= this.keepSizeAfter) {
         this.readLimit = this.writePos - this.keepSizeAfter;
      }

      this.processPendingBytes();
      return j;
   }

   private void processPendingBytes() {
      if (this.pendingSize > 0 && this.readPos < this.readLimit) {
         this.readPos = this.readPos - this.pendingSize;
         int var1 = this.pendingSize;
         this.pendingSize = 0;
         this.skip(var1);

         assert this.pendingSize < var1;
      }
   }

   public boolean isStarted() {
      return this.readPos != -1;
   }

   public void setFlushing() {
      this.readLimit = this.writePos - 1;
      this.processPendingBytes();
   }

   public void setFinishing() {
      this.readLimit = this.writePos - 1;
      this.finishing = true;
      this.processPendingBytes();
   }

   public boolean hasEnoughData(int i) {
      return this.readPos - i < this.readLimit;
   }

   public void copyUncompressed(OutputStream outputStream, int i, int j) throws IOException {
      outputStream.write(this.buf, this.readPos + 1 - i, j);
   }

   public int getAvail() {
      assert this.isStarted();

      return this.writePos - this.readPos;
   }

   public int getPos() {
      return this.readPos;
   }

   public int getByte(int i) {
      return this.buf[this.readPos - i] & 0xFF;
   }

   public int getByte(int i, int j) {
      return this.buf[this.readPos + i - j] & 0xFF;
   }

   public int getMatchLen(int i, int j) {
      int var3 = this.readPos - i - 1;
      int var4 = 0;

      while (var4 < j && this.buf[this.readPos + var4] == this.buf[var3 + var4]) {
         var4++;
      }

      return var4;
   }

   public int getMatchLen(int i, int j, int k) {
      int var4 = this.readPos + i;
      int var5 = var4 - j - 1;
      int var6 = 0;

      while (var6 < k && this.buf[var4 + var6] == this.buf[var5 + var6]) {
         var6++;
      }

      return var6;
   }

   public boolean verifyMatches(Matches matches) {
      int var2 = Math.min(this.getAvail(), this.matchLenMax);

      for (int var3 = 0; var3 < matches.count; var3++) {
         if (this.getMatchLen(matches.dist[var3], var2) != matches.len[var3]) {
            return false;
         }
      }

      return true;
   }

   int movePos(int i, int j) {
      assert i >= j;

      this.readPos++;
      int var3 = this.writePos - this.readPos;
      if (var3 < i && (var3 < j || !this.finishing)) {
         this.pendingSize++;
         var3 = 0;
      }

      return var3;
   }

   public abstract Matches getMatches();

   public abstract void skip(int i);
}
