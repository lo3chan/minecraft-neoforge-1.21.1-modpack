package org.tukaani.xz.index;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.MemoryLimitException;
import org.tukaani.xz.SeekableInputStream;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.common.StreamFlags;

public class IndexDecoder extends IndexBase {
   private final StreamFlags streamFlags;
   private final long streamPadding;
   private final int memoryUsage;
   private final long[] unpadded;
   private final long[] uncompressed;
   private long largestBlockSize = 0L;
   private int recordOffset = 0;
   private long compressedOffset = 0L;
   private long uncompressedOffset = 0L;

   public IndexDecoder(SeekableInputStream seekableInputStream, StreamFlags streamFlags, long l, int i) throws IOException {
      super(new CorruptedInputException("XZ Index is corrupt"));
      this.streamFlags = streamFlags;
      this.streamPadding = l;
      long var6 = seekableInputStream.position() + streamFlags.backwardSize - 4L;
      CRC32 var8 = new CRC32();
      CheckedInputStream var9 = new CheckedInputStream(seekableInputStream, var8);
      if (var9.read() != 0) {
         throw new CorruptedInputException("XZ Index is corrupt");
      } else {
         try {
            long var10 = DecoderUtil.decodeVLI(var9);
            if (var10 >= streamFlags.backwardSize / 2L) {
               throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (var10 > 2147483647L) {
               throw new UnsupportedOptionsException("XZ Index has over 2147483647 Records");
            }

            this.memoryUsage = 1 + (int)((16L * var10 + 1023L) / 1024L);
            if (i >= 0 && this.memoryUsage > i) {
               throw new MemoryLimitException(this.memoryUsage, i);
            }

            this.unpadded = new long[(int)var10];
            this.uncompressed = new long[(int)var10];
            int var12 = 0;

            for (int var13 = (int)var10; var13 > 0; var13--) {
               long var14 = DecoderUtil.decodeVLI(var9);
               long var16 = DecoderUtil.decodeVLI(var9);
               if (seekableInputStream.position() > var6) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }

               this.unpadded[var12] = this.blocksSum + var14;
               this.uncompressed[var12] = this.uncompressedSum + var16;
               var12++;
               super.add(var14, var16);

               assert (long)var12 == this.recordCount;

               if (this.largestBlockSize < var16) {
                  this.largestBlockSize = var16;
               }
            }
         } catch (EOFException var18) {
            throw new CorruptedInputException("XZ Index is corrupt");
         }

         int var19 = this.getIndexPaddingSize();
         if (seekableInputStream.position() + var19 != var6) {
            throw new CorruptedInputException("XZ Index is corrupt");
         } else {
            while (var19-- > 0) {
               if (var9.read() != 0) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }
            }

            long var11 = var8.getValue();

            for (int var20 = 0; var20 < 4; var20++) {
               if ((var11 >>> var20 * 8 & 255L) != seekableInputStream.read()) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }
            }
         }
      }
   }

   public void setOffsets(IndexDecoder indexDecoder) {
      this.recordOffset = indexDecoder.recordOffset + (int)indexDecoder.recordCount;
      this.compressedOffset = indexDecoder.compressedOffset + indexDecoder.getStreamSize() + indexDecoder.streamPadding;

      assert (this.compressedOffset & 3L) == 0L;

      this.uncompressedOffset = indexDecoder.uncompressedOffset + indexDecoder.uncompressedSum;
   }

   public int getMemoryUsage() {
      return this.memoryUsage;
   }

   public StreamFlags getStreamFlags() {
      return this.streamFlags;
   }

   public int getRecordCount() {
      return (int)this.recordCount;
   }

   public long getUncompressedSize() {
      return this.uncompressedSum;
   }

   public long getLargestBlockSize() {
      return this.largestBlockSize;
   }

   public boolean hasUncompressedOffset(long l) {
      return l >= this.uncompressedOffset && l < this.uncompressedOffset + this.uncompressedSum;
   }

   public boolean hasRecord(int i) {
      return i >= this.recordOffset && i < this.recordOffset + this.recordCount;
   }

   public void locateBlock(BlockInfo blockInfo, long l) {
      assert l >= this.uncompressedOffset;

      l -= this.uncompressedOffset;

      assert l < this.uncompressedSum;

      int var4 = 0;
      int var5 = this.unpadded.length - 1;

      while (var4 < var5) {
         int var6 = var4 + (var5 - var4) / 2;
         if (this.uncompressed[var6] <= l) {
            var4 = var6 + 1;
         } else {
            var5 = var6;
         }
      }

      this.setBlockInfo(blockInfo, this.recordOffset + var4);
   }

   public void setBlockInfo(BlockInfo blockInfo, int i) {
      assert i >= this.recordOffset;

      assert (long)(i - this.recordOffset) < this.recordCount;

      blockInfo.index = this;
      blockInfo.blockNumber = i;
      int var3 = i - this.recordOffset;
      if (var3 == 0) {
         blockInfo.compressedOffset = 0L;
         blockInfo.uncompressedOffset = 0L;
      } else {
         blockInfo.compressedOffset = this.unpadded[var3 - 1] + 3L & -4L;
         blockInfo.uncompressedOffset = this.uncompressed[var3 - 1];
      }

      blockInfo.unpaddedSize = this.unpadded[var3] - blockInfo.compressedOffset;
      blockInfo.uncompressedSize = this.uncompressed[var3] - blockInfo.uncompressedOffset;
      blockInfo.compressedOffset = blockInfo.compressedOffset + this.compressedOffset + 12L;
      blockInfo.uncompressedOffset = blockInfo.uncompressedOffset + this.uncompressedOffset;
   }
}
