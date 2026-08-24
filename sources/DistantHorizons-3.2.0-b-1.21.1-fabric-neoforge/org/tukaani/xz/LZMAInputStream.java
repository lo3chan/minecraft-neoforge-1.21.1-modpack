package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.lz.LZDecoder;
import org.tukaani.xz.lzma.LZMADecoder;
import org.tukaani.xz.rangecoder.RangeDecoderFromStream;

public class LZMAInputStream extends InputStream {
   public static final int DICT_SIZE_MAX = 2147483632;
   private InputStream in;
   private ArrayCache arrayCache;
   private LZDecoder lz;
   private RangeDecoderFromStream rc;
   private LZMADecoder lzma;
   private boolean endReached = false;
   private boolean relaxedEndCondition = false;
   private final byte[] tempBuf = new byte[1];
   private long remainingSize;
   private IOException exception = null;

   public static int getMemoryUsage(int i, byte b) throws UnsupportedOptionsException, CorruptedInputException {
      if (i >= 0 && i <= 2147483632) {
         int var2 = b & 255;
         if (var2 > 224) {
            throw new CorruptedInputException("Invalid LZMA properties byte");
         } else {
            var2 %= 45;
            int var3 = var2 / 9;
            int var4 = var2 - var3 * 9;
            return getMemoryUsage(i, var4, var3);
         }
      } else {
         throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation");
      }
   }

   public static int getMemoryUsage(int i, int j, int k) {
      if (j >= 0 && j <= 8 && k >= 0 && k <= 4) {
         return 10 + getDictSize(i) / 1024 + (1536 << j + k) / 1024;
      } else {
         throw new IllegalArgumentException("Invalid lc or lp");
      }
   }

   private static int getDictSize(int i) {
      if (i >= 0 && i <= 2147483632) {
         if (i < 4096) {
            i = 4096;
         }

         return i + 15 & -16;
      } else {
         throw new IllegalArgumentException("LZMA dictionary is too big for this implementation");
      }
   }

   public LZMAInputStream(InputStream inputStream) throws IOException {
      this(inputStream, -1);
   }

   public LZMAInputStream(InputStream inputStream, ArrayCache arrayCache) throws IOException {
      this(inputStream, -1, arrayCache);
   }

   public LZMAInputStream(InputStream inputStream, int i) throws IOException {
      this(inputStream, i, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream inputStream, int i, ArrayCache arrayCache) throws IOException {
      DataInputStream var4 = new DataInputStream(inputStream);
      byte var5 = var4.readByte();
      int var6 = 0;

      for (int var7 = 0; var7 < 4; var7++) {
         var6 |= var4.readUnsignedByte() << 8 * var7;
      }

      long var10 = 0L;

      for (int var9 = 0; var9 < 8; var9++) {
         var10 |= (long)var4.readUnsignedByte() << 8 * var9;
      }

      int var11 = getMemoryUsage(var6, var5);
      if (i != -1 && var11 > i) {
         throw new MemoryLimitException(var11, i);
      } else {
         this.initialize(inputStream, var10, var5, var6, null, arrayCache);
      }
   }

   public LZMAInputStream(InputStream inputStream, long l, byte b, int i) throws IOException {
      this.initialize(inputStream, l, b, i, null, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream inputStream, long l, byte b, int i, byte[] bs) throws IOException {
      this.initialize(inputStream, l, b, i, bs, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream inputStream, long l, byte b, int i, byte[] bs, ArrayCache arrayCache) throws IOException {
      this.initialize(inputStream, l, b, i, bs, arrayCache);
   }

   public LZMAInputStream(InputStream inputStream, long l, int i, int j, int k, int m, byte[] bs) throws IOException {
      this.initialize(inputStream, l, i, j, k, m, bs, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream inputStream, long l, int i, int j, int k, int m, byte[] bs, ArrayCache arrayCache) throws IOException {
      this.initialize(inputStream, l, i, j, k, m, bs, arrayCache);
   }

   private void initialize(InputStream inputStream, long l, byte b, int i, byte[] bs, ArrayCache arrayCache) throws IOException {
      if (l < -1L) {
         throw new UnsupportedOptionsException("Uncompressed size is too big");
      } else {
         int var8 = b & 255;
         if (var8 > 224) {
            throw new CorruptedInputException("Invalid LZMA properties byte");
         } else {
            int var9 = var8 / 45;
            var8 -= var9 * 9 * 5;
            int var10 = var8 / 9;
            int var11 = var8 - var10 * 9;
            if (i >= 0 && i <= 2147483632) {
               this.initialize(inputStream, l, var11, var10, var9, i, bs, arrayCache);
            } else {
               throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation");
            }
         }
      }
   }

   private void initialize(InputStream inputStream, long l, int i, int j, int k, int m, byte[] bs, ArrayCache arrayCache) throws IOException {
      if (l >= -1L && i >= 0 && i <= 8 && j >= 0 && j <= 4 && k >= 0 && k <= 4) {
         this.in = inputStream;
         this.arrayCache = arrayCache;
         m = getDictSize(m);
         if (l >= 0L && m > l) {
            m = getDictSize((int)l);
         }

         this.lz = new LZDecoder(getDictSize(m), bs, arrayCache);
         this.rc = new RangeDecoderFromStream(inputStream);
         this.lzma = new LZMADecoder(this.lz, this.rc, i, j, k);
         this.remainingSize = l;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void enableRelaxedEndCondition() {
      this.relaxedEndCondition = true;
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
      } else if (this.endReached) {
         return -1;
      } else {
         try {
            int var4 = 0;

            while (j > 0) {
               int var5 = j;
               if (this.remainingSize >= 0L && this.remainingSize < j) {
                  var5 = (int)this.remainingSize;
               }

               this.lz.setLimit(var5);

               try {
                  this.lzma.decode();
               } catch (CorruptedInputException var7) {
                  if (this.remainingSize != -1L || !this.lzma.endMarkerDetected()) {
                     throw var7;
                  }

                  this.endReached = true;
                  this.rc.normalize();
               }

               int var6 = this.lz.flush(bs, i);
               i += var6;
               j -= var6;
               var4 += var6;
               if (this.remainingSize >= 0L) {
                  this.remainingSize -= var6;

                  assert this.remainingSize >= 0L;

                  if (this.remainingSize == 0L) {
                     this.endReached = true;
                  }
               }

               if (this.endReached) {
                  if (!this.lz.hasPending() && (this.relaxedEndCondition || this.rc.isFinished())) {
                     this.putArraysToCache();
                     return var4 == 0 ? -1 : var4;
                  }

                  throw new CorruptedInputException();
               }
            }

            return var4;
         } catch (IOException var8) {
            this.exception = var8;
            throw var8;
         }
      }
   }

   private void putArraysToCache() {
      if (this.lz != null) {
         this.lz.putArraysToCache(this.arrayCache);
         this.lz = null;
      }
   }

   @Override
   public void close() throws IOException {
      if (this.in != null) {
         this.putArraysToCache();

         try {
            this.in.close();
         } finally {
            this.in = null;
         }
      }
   }
}
