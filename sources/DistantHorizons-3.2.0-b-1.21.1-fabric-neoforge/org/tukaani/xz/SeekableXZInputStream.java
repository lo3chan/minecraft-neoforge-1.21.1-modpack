package org.tukaani.xz;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.DecoderUtil;
import org.tukaani.xz.common.StreamFlags;
import org.tukaani.xz.index.BlockInfo;
import org.tukaani.xz.index.IndexDecoder;

public class SeekableXZInputStream extends SeekableInputStream {
   private final ArrayCache arrayCache;
   private SeekableInputStream in;
   private final int memoryLimit;
   private int indexMemoryUsage = 0;
   private final ArrayList<IndexDecoder> streams = new ArrayList<>();
   private int checkTypes = 0;
   private long uncompressedSize = 0L;
   private long largestBlockSize = 0L;
   private int blockCount = 0;
   private final BlockInfo curBlockInfo;
   private final BlockInfo queriedBlockInfo;
   private Check check;
   private final boolean verifyCheck;
   private BlockInputStream blockDecoder = null;
   private long curPos = 0L;
   private long seekPos;
   private boolean seekNeeded = false;
   private boolean endReached = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   public SeekableXZInputStream(SeekableInputStream seekableInputStream) throws IOException {
      this(seekableInputStream, -1);
   }

   public SeekableXZInputStream(SeekableInputStream seekableInputStream, ArrayCache arrayCache) throws IOException {
      this(seekableInputStream, -1, arrayCache);
   }

   public SeekableXZInputStream(SeekableInputStream seekableInputStream, int i) throws IOException {
      this(seekableInputStream, i, true);
   }

   public SeekableXZInputStream(SeekableInputStream seekableInputStream, int i, ArrayCache arrayCache) throws IOException {
      this(seekableInputStream, i, true, arrayCache);
   }

   public SeekableXZInputStream(SeekableInputStream seekableInputStream, int i, boolean bl) throws IOException {
      this(seekableInputStream, i, bl, ArrayCache.getDefaultCache());
   }

   public SeekableXZInputStream(SeekableInputStream seekableInputStream, int i, boolean bl, ArrayCache arrayCache) throws IOException {
      this.arrayCache = arrayCache;
      this.verifyCheck = bl;
      this.in = seekableInputStream;
      DataInputStream var5 = new DataInputStream(seekableInputStream);
      seekableInputStream.seek(0L);
      byte[] var6 = new byte[XZ.HEADER_MAGIC.length];
      var5.readFully(var6);
      if (!Arrays.equals(var6, XZ.HEADER_MAGIC)) {
         throw new XZFormatException();
      } else {
         long var17 = seekableInputStream.length();
         if ((var17 & 3L) != 0L) {
            throw new CorruptedInputException("XZ file size is not a multiple of 4 bytes");
         } else {
            byte[] var8 = new byte[12];
            long var9 = 0L;

            while (var17 > 0L) {
               if (var17 < 12L) {
                  throw new CorruptedInputException();
               }

               seekableInputStream.seek(var17 - 12L);
               var5.readFully(var8);
               if (var8[8] == 0 && var8[9] == 0 && var8[10] == 0 && var8[11] == 0) {
                  var9 += 4L;
                  var17 -= 4L;
               } else {
                  long var18 = var17 - 12L;
                  StreamFlags var11 = DecoderUtil.decodeStreamFooter(var8);
                  if (var11.backwardSize >= var18) {
                     throw new CorruptedInputException("Backward Size in XZ Stream Footer is too big");
                  }

                  this.check = Check.getInstance(var11.checkType);
                  this.checkTypes = this.checkTypes | 1 << var11.checkType;
                  seekableInputStream.seek(var18 - var11.backwardSize);

                  IndexDecoder var12;
                  try {
                     var12 = new IndexDecoder(seekableInputStream, var11, var9, i);
                  } catch (MemoryLimitException var16) {
                     assert i >= 0;

                     throw new MemoryLimitException(var16.getMemoryNeeded() + this.indexMemoryUsage, i + this.indexMemoryUsage);
                  }

                  this.indexMemoryUsage = this.indexMemoryUsage + var12.getMemoryUsage();
                  if (i >= 0) {
                     i -= var12.getMemoryUsage();

                     assert i >= 0;
                  }

                  if (this.largestBlockSize < var12.getLargestBlockSize()) {
                     this.largestBlockSize = var12.getLargestBlockSize();
                  }

                  long var13 = var12.getStreamSize() - 12L;
                  if (var18 < var13) {
                     throw new CorruptedInputException("XZ Index indicates too big compressed size for the XZ Stream");
                  }

                  var17 = var18 - var13;
                  seekableInputStream.seek(var17);
                  var5.readFully(var8);
                  StreamFlags var15 = DecoderUtil.decodeStreamHeader(var8);
                  if (!DecoderUtil.areStreamFlagsEqual(var15, var11)) {
                     throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
                  }

                  this.uncompressedSize = this.uncompressedSize + var12.getUncompressedSize();
                  if (this.uncompressedSize < 0L) {
                     throw new UnsupportedOptionsException("XZ file is too big");
                  }

                  this.blockCount = this.blockCount + var12.getRecordCount();
                  if (this.blockCount < 0) {
                     throw new UnsupportedOptionsException("XZ file has over 2147483647 Blocks");
                  }

                  this.streams.add(var12);
                  var9 = 0L;
               }
            }

            assert var17 == 0L;

            this.memoryLimit = i;
            IndexDecoder var19 = this.streams.get(this.streams.size() - 1);

            for (int var20 = this.streams.size() - 2; var20 >= 0; var20--) {
               IndexDecoder var22 = this.streams.get(var20);
               var22.setOffsets(var19);
               var19 = var22;
            }

            IndexDecoder var21 = this.streams.get(this.streams.size() - 1);
            this.curBlockInfo = new BlockInfo(var21);
            this.queriedBlockInfo = new BlockInfo(var21);
         }
      }
   }

   public int getCheckTypes() {
      return this.checkTypes;
   }

   public int getIndexMemoryUsage() {
      return this.indexMemoryUsage;
   }

   public long getLargestBlockSize() {
      return this.largestBlockSize;
   }

   public int getStreamCount() {
      return this.streams.size();
   }

   public int getBlockCount() {
      return this.blockCount;
   }

   public long getBlockPos(int i) {
      this.locateBlockByNumber(this.queriedBlockInfo, i);
      return this.queriedBlockInfo.uncompressedOffset;
   }

   public long getBlockSize(int i) {
      this.locateBlockByNumber(this.queriedBlockInfo, i);
      return this.queriedBlockInfo.uncompressedSize;
   }

   public long getBlockCompPos(int i) {
      this.locateBlockByNumber(this.queriedBlockInfo, i);
      return this.queriedBlockInfo.compressedOffset;
   }

   public long getBlockCompSize(int i) {
      this.locateBlockByNumber(this.queriedBlockInfo, i);
      return this.queriedBlockInfo.unpaddedSize + 3L & -4L;
   }

   public int getBlockCheckType(int i) {
      this.locateBlockByNumber(this.queriedBlockInfo, i);
      return this.queriedBlockInfo.getCheckType();
   }

   public int getBlockNumber(long l) {
      this.locateBlockByPos(this.queriedBlockInfo, l);
      return this.queriedBlockInfo.blockNumber;
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
         int var4 = 0;

         try {
            if (this.seekNeeded) {
               this.seek();
            }

            if (this.endReached) {
               return -1;
            }

            while (j > 0) {
               if (this.blockDecoder == null) {
                  this.seek();
                  if (this.endReached) {
                     break;
                  }
               }

               int var7 = this.blockDecoder.read(bs, i, j);
               if (var7 > 0) {
                  this.curPos += var7;
                  var4 += var7;
                  i += var7;
                  j -= var7;
               } else if (var7 == -1) {
                  this.blockDecoder = null;
               }
            }
         } catch (IOException var6) {
            Object var5 = var6;
            if (var6 instanceof EOFException) {
               var5 = new CorruptedInputException();
            }

            this.exception = (IOException)var5;
            if (var4 == 0) {
               throw var5;
            }
         }

         return var4;
      }
   }

   @Override
   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return !this.endReached && !this.seekNeeded && this.blockDecoder != null ? this.blockDecoder.available() : 0;
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

   @Override
   public long length() {
      return this.uncompressedSize;
   }

   @Override
   public long position() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else {
         return this.seekNeeded ? this.seekPos : this.curPos;
      }
   }

   @Override
   public void seek(long l) throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (l < 0L) {
         throw new XZIOException("Negative seek position: " + l);
      } else {
         this.seekPos = l;
         this.seekNeeded = true;
      }
   }

   public void seekToBlock(int i) throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (i >= 0 && i < this.blockCount) {
         this.seekPos = this.getBlockPos(i);
         this.seekNeeded = true;
      } else {
         throw new XZIOException("Invalid XZ Block number: " + i);
      }
   }

   private void seek() throws IOException {
      if (!this.seekNeeded) {
         if (this.curBlockInfo.hasNext()) {
            this.curBlockInfo.setNext();
            this.initBlockDecoder();
            return;
         }

         this.seekPos = this.curPos;
      }

      this.seekNeeded = false;
      if (this.seekPos >= this.uncompressedSize) {
         this.curPos = this.seekPos;
         if (this.blockDecoder != null) {
            this.blockDecoder.close();
            this.blockDecoder = null;
         }

         this.endReached = true;
      } else {
         this.endReached = false;
         this.locateBlockByPos(this.curBlockInfo, this.seekPos);
         if (this.curPos <= this.curBlockInfo.uncompressedOffset || this.curPos > this.seekPos) {
            this.in.seek(this.curBlockInfo.compressedOffset);
            this.check = Check.getInstance(this.curBlockInfo.getCheckType());
            this.initBlockDecoder();
            this.curPos = this.curBlockInfo.uncompressedOffset;
         }

         if (this.seekPos > this.curPos) {
            long var1 = this.seekPos - this.curPos;
            if (this.blockDecoder.skip(var1) != var1) {
               throw new CorruptedInputException();
            }

            this.curPos = this.seekPos;
         }
      }
   }

   private void locateBlockByPos(BlockInfo blockInfo, long l) {
      if (l >= 0L && l < this.uncompressedSize) {
         int var5 = 0;

         while (true) {
            IndexDecoder var4 = this.streams.get(var5);
            if (var4.hasUncompressedOffset(l)) {
               var4.locateBlock(blockInfo, l);

               assert (blockInfo.compressedOffset & 3L) == 0L;

               assert blockInfo.uncompressedSize > 0L;

               assert l >= blockInfo.uncompressedOffset;

               assert l < blockInfo.uncompressedOffset + blockInfo.uncompressedSize;

               return;
            }

            var5++;
         }
      } else {
         throw new IndexOutOfBoundsException("Invalid uncompressed position: " + l);
      }
   }

   private void locateBlockByNumber(BlockInfo blockInfo, int i) {
      if (i < 0 || i >= this.blockCount) {
         throw new IndexOutOfBoundsException("Invalid XZ Block number: " + i);
      } else if (blockInfo.blockNumber != i) {
         int var3 = 0;

         while (true) {
            IndexDecoder var4 = this.streams.get(var3);
            if (var4.hasRecord(i)) {
               var4.setBlockInfo(blockInfo, i);
               return;
            }

            var3++;
         }
      }
   }

   private void initBlockDecoder() throws IOException {
      try {
         if (this.blockDecoder != null) {
            this.blockDecoder.close();
            this.blockDecoder = null;
         }

         this.blockDecoder = new BlockInputStream(
            this.in, this.check, this.verifyCheck, this.memoryLimit, this.curBlockInfo.unpaddedSize, this.curBlockInfo.uncompressedSize, this.arrayCache
         );
      } catch (MemoryLimitException var2) {
         assert this.memoryLimit >= 0;

         throw new MemoryLimitException(var2.getMemoryNeeded() + this.indexMemoryUsage, this.memoryLimit + this.indexMemoryUsage);
      } catch (IndexIndicatorException var3) {
         throw new CorruptedInputException();
      }
   }
}
