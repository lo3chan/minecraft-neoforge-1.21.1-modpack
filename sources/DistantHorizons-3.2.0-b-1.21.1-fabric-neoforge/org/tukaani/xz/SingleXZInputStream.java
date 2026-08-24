package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.index.IndexHash;

public class SingleXZInputStream extends InputStream {
   private InputStream in;
   private final ArrayCache arrayCache;
   private final int memoryLimit;
   private final StreamFlags streamHeaderFlags;
   private final Check check;
   private final boolean verifyCheck;
   private BlockInputStream blockDecoder = null;
   private final IndexHash indexHash = new IndexHash();
   private boolean endReached = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   private static byte[] readStreamHeader(InputStream inputStream) throws IOException {
      byte[] var1 = new byte[12];
      new DataInputStream(inputStream).readFully(var1);
      return var1;
   }

   public SingleXZInputStream(InputStream inputStream) throws IOException {
      this(inputStream, -1);
   }

   public SingleXZInputStream(InputStream inputStream, ArrayCache arrayCache) throws IOException {
      this(inputStream, -1, arrayCache);
   }

   public SingleXZInputStream(InputStream inputStream, int i) throws IOException {
      this(inputStream, i, true);
   }

   public SingleXZInputStream(InputStream inputStream, int i, ArrayCache arrayCache) throws IOException {
      this(inputStream, i, true, arrayCache);
   }

   public SingleXZInputStream(InputStream inputStream, int i, boolean bl) throws IOException {
      this(inputStream, i, bl, ArrayCache.getDefaultCache());
   }

   public SingleXZInputStream(InputStream inputStream, int i, boolean bl, ArrayCache arrayCache) throws IOException {
      this(inputStream, i, bl, readStreamHeader(inputStream), arrayCache);
   }

   SingleXZInputStream(InputStream inputStream, int i, boolean bl, byte[] bs, ArrayCache arrayCache) throws IOException {
      this.arrayCache = arrayCache;
      this.in = inputStream;
      this.memoryLimit = i;
      this.verifyCheck = bl;
      this.streamHeaderFlags = DecoderUtil.decodeStreamHeader(bs);
      this.check = Check.getInstance(this.streamHeaderFlags.checkType);
   }

   public int getCheckType() {
      return this.streamHeaderFlags.checkType;
   }

   public String getCheckName() {
      return this.check.getName();
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
         int var4 = 0;

         try {
            while (j > 0) {
               if (this.blockDecoder == null) {
                  try {
                     this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, -1L, -1L, this.arrayCache);
                  } catch (IndexIndicatorException var6) {
                     this.indexHash.validate(this.in);
                     this.validateStreamFooter();
                     this.endReached = true;
                     return var4 > 0 ? var4 : -1;
                  }
               }

               int var5 = this.blockDecoder.read(bs, i, j);
               if (var5 > 0) {
                  var4 += var5;
                  i += var5;
                  j -= var5;
               } else if (var5 == -1) {
                  this.indexHash.add(this.blockDecoder.getUnpaddedSize(), this.blockDecoder.getUncompressedSize());
                  this.blockDecoder = null;
               }
            }
         } catch (IOException var7) {
            this.exception = var7;
            if (var4 == 0) {
               throw var7;
            }
         }

         return var4;
      }
   }

   private void validateStreamFooter() throws IOException {
      byte[] var1 = new byte[12];
      new DataInputStream(this.in).readFully(var1);
      StreamFlags var2 = DecoderUtil.decodeStreamFooter(var1);
      if (!DecoderUtil.areStreamFlagsEqual(this.streamHeaderFlags, var2) || this.indexHash.getIndexSize() != var2.backwardSize) {
         throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
      }
   }

   @Override
   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return this.blockDecoder == null ? 0 : this.blockDecoder.available();
      }
   }

   @Override
   public void close() throws IOException {
      this.close(true);
   }

   public void close(boolean bl) throws IOException {
      if (this.in != null) {
         if (this.blockDecoder != null) {
            this.blockDecoder.close();
            this.blockDecoder = null;
         }

         try {
            if (bl) {
               this.in.close();
            }
         } finally {
            this.in = null;
         }
      }
   }
}
