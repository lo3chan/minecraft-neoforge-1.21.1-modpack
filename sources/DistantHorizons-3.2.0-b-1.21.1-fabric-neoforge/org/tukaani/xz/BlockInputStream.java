package org.tukaani.xz;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.DecoderUtil;

class BlockInputStream extends InputStream {
   private final DataInputStream inData;
   private final CountingInputStream inCounted;
   private InputStream filterChain;
   private final Check check;
   private final boolean verifyCheck;
   private long uncompressedSizeInHeader = -1L;
   private long compressedSizeInHeader = -1L;
   private long compressedSizeLimit;
   private final int headerSize;
   private long uncompressedSize = 0L;
   private boolean endReached = false;
   private final byte[] tempBuf = new byte[1];

   public BlockInputStream(InputStream inputStream, Check check, boolean bl, int i, long l, long m, ArrayCache arrayCache) throws IOException, IndexIndicatorException {
      this.check = check;
      this.verifyCheck = bl;
      this.inData = new DataInputStream(inputStream);
      int var10 = this.inData.readUnsignedByte();
      if (var10 == 0) {
         throw new IndexIndicatorException();
      } else {
         this.headerSize = 4 * (var10 + 1);
         byte[] var11 = new byte[this.headerSize];
         var11[0] = (byte)var10;
         this.inData.readFully(var11, 1, this.headerSize - 1);
         if (!DecoderUtil.isCRC32Valid(var11, 0, this.headerSize - 4, this.headerSize - 4)) {
            throw new CorruptedInputException("XZ Block Header is corrupt");
         } else if ((var11[1] & 60) != 0) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Block Header");
         } else {
            int var12 = (var11[1] & 3) + 1;
            long[] var13 = new long[var12];
            byte[][] var14 = new byte[var12][];
            ByteArrayInputStream var15 = new ByteArrayInputStream(var11, 2, this.headerSize - 6);

            try {
               this.compressedSizeLimit = 9223372036854775804L - this.headerSize - check.getSize();
               if ((var11[1] & 64) != 0) {
                  this.compressedSizeInHeader = DecoderUtil.decodeVLI(var15);
                  if (this.compressedSizeInHeader == 0L || this.compressedSizeInHeader > this.compressedSizeLimit) {
                     throw new CorruptedInputException();
                  }

                  this.compressedSizeLimit = this.compressedSizeInHeader;
               }

               if ((var11[1] & 128) != 0) {
                  this.uncompressedSizeInHeader = DecoderUtil.decodeVLI(var15);
               }

               for (int var16 = 0; var16 < var12; var16++) {
                  var13[var16] = DecoderUtil.decodeVLI(var15);
                  long var17 = DecoderUtil.decodeVLI(var15);
                  if (var17 > var15.available()) {
                     throw new CorruptedInputException();
                  }

                  var14[var16] = new byte[(int)var17];
                  var15.read(var14[var16]);
               }
            } catch (IOException var19) {
               throw new CorruptedInputException("XZ Block Header is corrupt");
            }

            for (int var20 = var15.available(); var20 > 0; var20--) {
               if (var15.read() != 0) {
                  throw new UnsupportedOptionsException("Unsupported options in XZ Block Header");
               }
            }

            if (l != -1L) {
               int var21 = this.headerSize + check.getSize();
               if (var21 >= l) {
                  throw new CorruptedInputException("XZ Index does not match a Block Header");
               }

               long var23 = l - var21;
               if (var23 > this.compressedSizeLimit || this.compressedSizeInHeader != -1L && this.compressedSizeInHeader != var23) {
                  throw new CorruptedInputException("XZ Index does not match a Block Header");
               }

               if (this.uncompressedSizeInHeader != -1L && this.uncompressedSizeInHeader != m) {
                  throw new CorruptedInputException("XZ Index does not match a Block Header");
               }

               this.compressedSizeLimit = var23;
               this.compressedSizeInHeader = var23;
               this.uncompressedSizeInHeader = m;
            }

            FilterDecoder[] var22 = new FilterDecoder[var13.length];

            for (int var24 = 0; var24 < var22.length; var24++) {
               if (var13[var24] == 33L) {
                  var22[var24] = new LZMA2Decoder(var14[var24]);
               } else if (var13[var24] == 3L) {
                  var22[var24] = new DeltaDecoder(var14[var24]);
               } else {
                  if (!BCJDecoder.isBCJFilterID(var13[var24])) {
                     throw new UnsupportedOptionsException("Unknown Filter ID " + var13[var24]);
                  }

                  var22[var24] = new BCJDecoder(var13[var24], var14[var24]);
               }
            }

            RawCoder.validate(var22);
            if (i >= 0) {
               int var25 = 0;

               for (int var18 = 0; var18 < var22.length; var18++) {
                  var25 += var22[var18].getMemoryUsage();
               }

               if (var25 > i) {
                  throw new MemoryLimitException(var25, i);
               }
            }

            this.inCounted = new CountingInputStream(inputStream);
            this.filterChain = this.inCounted;

            for (int var26 = var22.length - 1; var26 >= 0; var26--) {
               this.filterChain = var22[var26].getInputStream(this.filterChain, arrayCache);
            }
         }
      }
   }

   @Override
   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 0xFF;
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      if (this.endReached) {
         return -1;
      } else {
         int var4 = this.filterChain.read(bs, i, j);
         if (var4 > 0) {
            if (this.verifyCheck) {
               this.check.update(bs, i, var4);
            }

            this.uncompressedSize += var4;
            long var5 = this.inCounted.getSize();
            if (var5 < 0L
               || var5 > this.compressedSizeLimit
               || this.uncompressedSize < 0L
               || this.uncompressedSizeInHeader != -1L && this.uncompressedSize > this.uncompressedSizeInHeader) {
               throw new CorruptedInputException();
            }

            if (var4 < j || this.uncompressedSize == this.uncompressedSizeInHeader) {
               if (this.filterChain.read() != -1) {
                  throw new CorruptedInputException();
               }

               this.validate();
               this.endReached = true;
            }
         } else if (var4 == -1) {
            this.validate();
            this.endReached = true;
         }

         return var4;
      }
   }

   private void validate() throws IOException {
      long var1 = this.inCounted.getSize();
      if ((this.compressedSizeInHeader == -1L || this.compressedSizeInHeader == var1)
         && (this.uncompressedSizeInHeader == -1L || this.uncompressedSizeInHeader == this.uncompressedSize)) {
         while ((var1++ & 3L) != 0L) {
            if (this.inData.readUnsignedByte() != 0) {
               throw new CorruptedInputException();
            }
         }

         byte[] var3 = new byte[this.check.getSize()];
         this.inData.readFully(var3);
         if (this.verifyCheck && !Arrays.equals(this.check.finish(), var3)) {
            throw new CorruptedInputException("Integrity check (" + this.check.getName() + ") does not match");
         }
      } else {
         throw new CorruptedInputException();
      }
   }

   @Override
   public int available() throws IOException {
      return this.filterChain.available();
   }

   @Override
   public void close() {
      try {
         this.filterChain.close();
      } catch (IOException var2) {
         assert false;
      }

      this.filterChain = null;
   }

   public long getUnpaddedSize() {
      return this.headerSize + this.inCounted.getSize() + this.check.getSize();
   }

   public long getUncompressedSize() {
      return this.uncompressedSize;
   }
}
