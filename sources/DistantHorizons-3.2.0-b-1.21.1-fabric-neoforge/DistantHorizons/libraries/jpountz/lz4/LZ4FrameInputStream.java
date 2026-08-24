package DistantHorizons.libraries.jpountz.lz4;

import DistantHorizons.libraries.jpountz.xxhash.XXHash32;
import DistantHorizons.libraries.jpountz.xxhash.XXHashFactory;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class LZ4FrameInputStream extends FilterInputStream {
   static final String PREMATURE_EOS = "Stream ended prematurely";
   static final String NOT_SUPPORTED = "Stream unsupported";
   static final String BLOCK_HASH_MISMATCH = "Block checksum mismatch";
   static final String DESCRIPTOR_HASH_MISMATCH = "Stream frame descriptor corrupted";
   static final int MAGIC_SKIPPABLE_BASE = 407710288;
   private final LZ4SafeDecompressor decompressor;
   private final XXHash32 checksum;
   private final byte[] headerArray = new byte[15];
   private final ByteBuffer headerBuffer = ByteBuffer.wrap(this.headerArray).order(ByteOrder.LITTLE_ENDIAN);
   private final boolean readSingleFrame;
   private byte[] compressedBuffer;
   private ByteBuffer buffer = null;
   private byte[] rawBuffer = null;
   private int maxBlockSize = -1;
   private long expectedContentSize = -1L;
   private long totalContentSize = 0L;
   private boolean firstFrameHeaderRead = false;
   private LZ4FrameOutputStream.FrameInfo frameInfo = null;
   private final ByteBuffer readNumberBuff = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);

   public LZ4FrameInputStream(InputStream in) throws IOException {
      this(in, LZ4Factory.fastestInstance().safeDecompressor(), XXHashFactory.fastestInstance().hash32());
   }

   public LZ4FrameInputStream(InputStream in, boolean readSingleFrame) throws IOException {
      this(in, LZ4Factory.fastestInstance().safeDecompressor(), XXHashFactory.fastestInstance().hash32(), readSingleFrame);
   }

   public LZ4FrameInputStream(InputStream in, LZ4SafeDecompressor decompressor, XXHash32 checksum) throws IOException {
      this(in, decompressor, checksum, false);
   }

   public LZ4FrameInputStream(InputStream in, LZ4SafeDecompressor decompressor, XXHash32 checksum, boolean readSingleFrame) throws IOException {
      super(in);
      this.decompressor = decompressor;
      this.checksum = checksum;
      this.readSingleFrame = readSingleFrame;
   }

   private boolean nextFrameInfo() throws IOException {
      while (true) {
         int size = 0;

         do {
            int mySize = this.in.read(this.readNumberBuff.array(), size, 4 - size);
            if (mySize < 0) {
               if (this.firstFrameHeaderRead) {
                  if (size > 0) {
                     throw new IOException("Stream ended prematurely");
                  }

                  return false;
               }

               throw new IOException("Stream ended prematurely");
            }

            size += mySize;
         } while (size < 4);

         int magic = this.readNumberBuff.getInt(0);
         if (magic == 407708164) {
            this.readHeader();
            return true;
         }

         if (magic >>> 4 != 25481893) {
            throw new IOException("Stream unsupported");
         }

         this.skippableFrame();
      }
   }

   private void skippableFrame() throws IOException {
      int skipSize = this.readInt(this.in);
      byte[] skipBuffer = new byte[1024];

      while (skipSize > 0) {
         int mySize = this.in.read(skipBuffer, 0, Math.min(skipSize, skipBuffer.length));
         if (mySize < 0) {
            throw new IOException("Stream ended prematurely");
         }

         skipSize -= mySize;
      }

      this.firstFrameHeaderRead = true;
   }

   private void readHeader() throws IOException {
      ((Buffer)this.headerBuffer).rewind();
      int flgRead = this.in.read();
      if (flgRead < 0) {
         throw new IOException("Stream ended prematurely");
      } else {
         int bdRead = this.in.read();
         if (bdRead < 0) {
            throw new IOException("Stream ended prematurely");
         } else {
            byte flgByte = (byte)(flgRead & 0xFF);
            LZ4FrameOutputStream.FLG flg = LZ4FrameOutputStream.FLG.fromByte(flgByte);
            this.headerBuffer.put(flgByte);
            byte bdByte = (byte)(bdRead & 0xFF);
            LZ4FrameOutputStream.BD bd = LZ4FrameOutputStream.BD.fromByte(bdByte);
            this.headerBuffer.put(bdByte);
            this.frameInfo = new LZ4FrameOutputStream.FrameInfo(flg, bd);
            if (flg.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_SIZE)) {
               this.expectedContentSize = this.readLong(this.in);
               this.headerBuffer.putLong(this.expectedContentSize);
            }

            this.totalContentSize = 0L;
            byte hash = (byte)(this.checksum.hash(this.headerArray, 0, this.headerBuffer.position(), 0) >> 8 & 0xFF);
            int expectedHash = this.in.read();
            if (expectedHash < 0) {
               throw new IOException("Stream ended prematurely");
            } else if (hash != (byte)(expectedHash & 0xFF)) {
               throw new IOException("Stream frame descriptor corrupted");
            } else {
               this.maxBlockSize = this.frameInfo.getBD().getBlockMaximumSize();
               this.compressedBuffer = new byte[this.maxBlockSize];
               this.rawBuffer = new byte[this.maxBlockSize];
               this.buffer = ByteBuffer.wrap(this.rawBuffer);
               ((Buffer)this.buffer).limit(0);
               this.firstFrameHeaderRead = true;
            }
         }
      }
   }

   private long readLong(InputStream stream) throws IOException {
      int offset = 0;

      do {
         int mySize = stream.read(this.readNumberBuff.array(), offset, 8 - offset);
         if (mySize < 0) {
            throw new IOException("Stream ended prematurely");
         }

         offset += mySize;
      } while (offset < 8);

      return this.readNumberBuff.getLong(0);
   }

   private int readInt(InputStream stream) throws IOException {
      int offset = 0;

      do {
         int mySize = stream.read(this.readNumberBuff.array(), offset, 4 - offset);
         if (mySize < 0) {
            throw new IOException("Stream ended prematurely");
         }

         offset += mySize;
      } while (offset < 4);

      return this.readNumberBuff.getInt(0);
   }

   private void readBlock() throws IOException {
      int blockSize = this.readInt(this.in);
      boolean compressed = (blockSize & -2147483648) == 0;
      blockSize &= 2147483647;
      if (blockSize == 0) {
         if (this.frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_CHECKSUM)) {
            int contentChecksum = this.readInt(this.in);
            if (contentChecksum != this.frameInfo.currentStreamHash()) {
               throw new IOException("Content checksum mismatch");
            }
         }

         if (this.frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_SIZE) && this.expectedContentSize != this.totalContentSize) {
            throw new IOException("Size check mismatch");
         } else {
            this.frameInfo.finish();
         }
      } else {
         byte[] tmpBuffer;
         if (compressed) {
            tmpBuffer = this.compressedBuffer;
         } else {
            tmpBuffer = this.rawBuffer;
         }

         if (blockSize > this.maxBlockSize) {
            throw new IOException(String.format(Locale.ROOT, "Block size %s exceeded max: %s", blockSize, this.maxBlockSize));
         } else {
            int offset = 0;

            while (offset < blockSize) {
               int lastRead = this.in.read(tmpBuffer, offset, blockSize - offset);
               if (lastRead < 0) {
                  throw new IOException("Stream ended prematurely");
               }

               offset += lastRead;
            }

            if (this.frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.BLOCK_CHECKSUM)) {
               int hashCheck = this.readInt(this.in);
               if (hashCheck != this.checksum.hash(tmpBuffer, 0, blockSize, 0)) {
                  throw new IOException("Block checksum mismatch");
               }
            }

            int currentBufferSize;
            if (compressed) {
               try {
                  currentBufferSize = this.decompressor.decompress(tmpBuffer, 0, blockSize, this.rawBuffer, 0, this.rawBuffer.length);
               } catch (LZ4Exception var7) {
                  throw new IOException(var7);
               }
            } else {
               currentBufferSize = blockSize;
            }

            if (this.frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_CHECKSUM)) {
               this.frameInfo.updateStreamHash(this.rawBuffer, 0, currentBufferSize);
            }

            this.totalContentSize += currentBufferSize;
            ((Buffer)this.buffer).limit(currentBufferSize);
            ((Buffer)this.buffer).rewind();
         }
      }
   }

   @Override
   public int read() throws IOException {
      while (!this.firstFrameHeaderRead || this.buffer.remaining() == 0) {
         if (!this.firstFrameHeaderRead || this.frameInfo.isFinished()) {
            if (this.firstFrameHeaderRead && this.readSingleFrame) {
               return -1;
            }

            if (!this.nextFrameInfo()) {
               return -1;
            }
         }

         this.readBlock();
      }

      return this.buffer.get() & 0xFF;
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= b.length) {
         while (!this.firstFrameHeaderRead || this.buffer.remaining() == 0) {
            if (!this.firstFrameHeaderRead || this.frameInfo.isFinished()) {
               if (this.firstFrameHeaderRead && this.readSingleFrame) {
                  return -1;
               }

               if (!this.nextFrameInfo()) {
                  return -1;
               }
            }

            this.readBlock();
         }

         len = Math.min(len, this.buffer.remaining());
         this.buffer.get(b, off, len);
         return len;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public long skip(long n) throws IOException {
      if (n <= 0L) {
         return 0L;
      } else {
         while (!this.firstFrameHeaderRead || this.buffer.remaining() == 0) {
            if (!this.firstFrameHeaderRead || this.frameInfo.isFinished()) {
               if (this.firstFrameHeaderRead && this.readSingleFrame) {
                  return 0L;
               }

               if (!this.nextFrameInfo()) {
                  return 0L;
               }
            }

            this.readBlock();
         }

         n = Math.min(n, (long)this.buffer.remaining());
         ((Buffer)this.buffer).position(this.buffer.position() + (int)n);
         return n;
      }
   }

   @Override
   public int available() throws IOException {
      return this.buffer.remaining();
   }

   @Override
   public void close() throws IOException {
      super.close();
   }

   @Override
   public synchronized void mark(int readlimit) {
      throw new UnsupportedOperationException("mark not supported");
   }

   @Override
   public synchronized void reset() throws IOException {
      throw new UnsupportedOperationException("reset not supported");
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   public long getExpectedContentSize() throws IOException {
      if (!this.readSingleFrame) {
         throw new UnsupportedOperationException("Operation not permitted when multiple frames can be read");
      } else {
         return !this.firstFrameHeaderRead && !this.nextFrameInfo() ? -1L : this.expectedContentSize;
      }
   }

   public boolean isExpectedContentSizeDefined() throws IOException {
      if (this.readSingleFrame) {
         return !this.firstFrameHeaderRead && !this.nextFrameInfo() ? false : this.expectedContentSize >= 0L;
      } else {
         return false;
      }
   }
}
