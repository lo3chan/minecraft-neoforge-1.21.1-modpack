package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.lz.LZDecoder;
import org.tukaani.xz.lzma.LZMADecoder;
import org.tukaani.xz.rangecoder.RangeDecoderFromBuffer;

public class LZMA2InputStream extends InputStream {
   public static final int DICT_SIZE_MIN = 4096;
   public static final int DICT_SIZE_MAX = 2147483632;
   private static final int COMPRESSED_SIZE_MAX = 65536;
   private final ArrayCache arrayCache;
   private DataInputStream in;
   private LZDecoder lz;
   private RangeDecoderFromBuffer rc;
   private LZMADecoder lzma;
   private int uncompressedSize = 0;
   private boolean isLZMAChunk = false;
   private boolean needDictReset = true;
   private boolean needProps = true;
   private boolean endReached = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   public static int getMemoryUsage(int i) {
      return 104 + getDictSize(i) / 1024;
   }

   private static int getDictSize(int i) {
      if (i >= 4096 && i <= 2147483632) {
         return i + 15 & -16;
      } else {
         throw new IllegalArgumentException("Unsupported dictionary size " + i);
      }
   }

   public LZMA2InputStream(InputStream inputStream, int i) {
      this(inputStream, i, null);
   }

   public LZMA2InputStream(InputStream inputStream, int i, byte[] bs) {
      this(inputStream, i, bs, ArrayCache.getDefaultCache());
   }

   LZMA2InputStream(InputStream inputStream, int i, byte[] bs, ArrayCache arrayCache) {
      if (inputStream == null) {
         throw new NullPointerException();
      } else {
         this.arrayCache = arrayCache;
         this.in = new DataInputStream(inputStream);
         this.rc = new RangeDecoderFromBuffer(65536, arrayCache);
         this.lz = new LZDecoder(getDictSize(i), bs, arrayCache);
         if (bs != null && bs.length > 0) {
            this.needDictReset = false;
         }
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
      } else if (this.endReached) {
         return -1;
      } else {
         try {
            int var4 = 0;

            while (j > 0) {
               if (this.uncompressedSize == 0) {
                  this.decodeChunkHeader();
                  if (this.endReached) {
                     return var4 == 0 ? -1 : var4;
                  }
               }

               int var5 = Math.min(this.uncompressedSize, j);
               if (!this.isLZMAChunk) {
                  this.lz.copyUncompressed(this.in, var5);
               } else {
                  this.lz.setLimit(var5);
                  this.lzma.decode();
               }

               int var6 = this.lz.flush(bs, i);
               i += var6;
               j -= var6;
               var4 += var6;
               this.uncompressedSize -= var6;
               if (this.uncompressedSize == 0 && (!this.rc.isFinished() || this.lz.hasPending())) {
                  throw new CorruptedInputException();
               }
            }

            return var4;
         } catch (IOException var7) {
            this.exception = var7;
            throw var7;
         }
      }
   }

   private void decodeChunkHeader() throws IOException {
      int var1 = this.in.readUnsignedByte();
      if (var1 == 0) {
         this.endReached = true;
         this.putArraysToCache();
      } else {
         if (var1 < 224 && var1 != 1) {
            if (this.needDictReset) {
               throw new CorruptedInputException();
            }
         } else {
            this.needProps = true;
            this.needDictReset = false;
            this.lz.reset();
         }

         if (var1 >= 128) {
            this.isLZMAChunk = true;
            this.uncompressedSize = (var1 & 31) << 16;
            this.uncompressedSize = this.uncompressedSize + this.in.readUnsignedShort() + 1;
            int var2 = this.in.readUnsignedShort() + 1;
            if (var1 >= 192) {
               this.needProps = false;
               this.decodeProps();
            } else {
               if (this.needProps) {
                  throw new CorruptedInputException();
               }

               if (var1 >= 160) {
                  this.lzma.reset();
               }
            }

            this.rc.prepareInputBuffer(this.in, var2);
         } else {
            if (var1 > 2) {
               throw new CorruptedInputException();
            }

            this.isLZMAChunk = false;
            this.uncompressedSize = this.in.readUnsignedShort() + 1;
         }
      }
   }

   private void decodeProps() throws IOException {
      int var1 = this.in.readUnsignedByte();
      if (var1 > 224) {
         throw new CorruptedInputException();
      } else {
         int var2 = var1 / 45;
         var1 -= var2 * 9 * 5;
         int var3 = var1 / 9;
         int var4 = var1 - var3 * 9;
         if (var4 + var3 > 4) {
            throw new CorruptedInputException();
         } else {
            this.lzma = new LZMADecoder(this.lz, this.rc, var4, var3, var2);
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
         return this.isLZMAChunk ? this.uncompressedSize : Math.min(this.uncompressedSize, this.in.available());
      }
   }

   private void putArraysToCache() {
      if (this.lz != null) {
         this.lz.putArraysToCache(this.arrayCache);
         this.lz = null;
         this.rc.putArraysToCache(this.arrayCache);
         this.rc = null;
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
