package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ZstdOutputStreamNoFinalizer extends FilterOutputStream {
   private final long stream;
   private long srcPos = 0L;
   private long dstPos = 0L;
   private final BufferPool bufferPool;
   private final ByteBuffer dstByteBuffer;
   private final byte[] dst;
   private boolean isClosed = false;
   private static final int dstSize = (int)recommendedCOutSize();
   private boolean closeFrameOnFlush = false;
   private boolean frameClosed = true;
   private boolean frameStarted = false;

   public static native long recommendedCOutSize();

   private static native long createCStream();

   private static native int freeCStream(long l);

   private native int resetCStream(long l);

   private native int compressStream(long l, byte[] bs, int i, byte[] cs, int j);

   private native int flushStream(long l, byte[] bs, int i);

   private native int endStream(long l, byte[] bs, int i);

   public ZstdOutputStreamNoFinalizer(OutputStream outputStream, int i) throws IOException {
      this(outputStream, NoPool.INSTANCE);
      Zstd.setCompressionLevel(this.stream, i);
   }

   public ZstdOutputStreamNoFinalizer(OutputStream outputStream) throws IOException {
      this(outputStream, NoPool.INSTANCE);
   }

   public ZstdOutputStreamNoFinalizer(OutputStream outputStream, BufferPool bufferPool, int i) throws IOException {
      this(outputStream, bufferPool);
      Zstd.setCompressionLevel(this.stream, i);
   }

   public ZstdOutputStreamNoFinalizer(OutputStream outputStream, BufferPool bufferPool) throws IOException {
      super(outputStream);
      this.stream = createCStream();
      this.bufferPool = bufferPool;
      this.dstByteBuffer = Zstd.getArrayBackedBuffer(bufferPool, dstSize);
      this.dst = this.dstByteBuffer.array();
   }

   public synchronized ZstdOutputStreamNoFinalizer setChecksum(boolean bl) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionChecksums(this.stream, bl);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setLevel(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionLevel(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setLong(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionLong(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setWorkers(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionWorkers(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setOverlapLog(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionOverlapLog(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setJobSize(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionJobSize(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setTargetLength(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionTargetLength(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setMinMatch(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionMinMatch(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setSearchLog(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionSearchLog(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setChainLog(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionChainLog(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setHashLog(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionHashLog(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setWindowLog(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionWindowLog(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setStrategy(int i) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.setCompressionStrategy(this.stream, i);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setCloseFrameOnFlush(boolean bl) {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         this.closeFrameOnFlush = bl;
         return this;
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setDict(byte[] bs) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.loadDictCompress(this.stream, bs, bs.length);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   public synchronized ZstdOutputStreamNoFinalizer setDict(ZstdDictCompress zstdDictCompress) throws IOException {
      if (!this.frameClosed) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         int var2 = Zstd.loadFastDictCompress(this.stream, zstdDictCompress);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         } else {
            return this;
         }
      }
   }

   @Override
   public synchronized void write(byte[] bs, int i, int j) throws IOException {
      if (this.isClosed) {
         throw new IOException("StreamClosed");
      } else {
         if (this.frameClosed) {
            int var4 = this.resetCStream(this.stream);
            if (Zstd.isError(var4)) {
               throw new ZstdIOException(var4);
            }

            this.frameClosed = false;
            this.frameStarted = true;
         }

         int var6 = i + j;
         this.srcPos = i;

         while (this.srcPos < var6) {
            int var5 = this.compressStream(this.stream, this.dst, dstSize, bs, var6);
            if (Zstd.isError(var5)) {
               throw new ZstdIOException(var5);
            }

            if (this.dstPos > 0L) {
               this.out.write(this.dst, 0, (int)this.dstPos);
            }
         }
      }
   }

   @Override
   public void write(int i) throws IOException {
      byte[] var2 = new byte[]{(byte)i};
      this.write(var2, 0, 1);
   }

   @Override
   public synchronized void flush() throws IOException {
      if (this.isClosed) {
         throw new IOException("StreamClosed");
      } else {
         if (!this.frameClosed) {
            int var1;
            if (this.closeFrameOnFlush) {
               while (true) {
                  var1 = this.endStream(this.stream, this.dst, dstSize);
                  if (Zstd.isError(var1)) {
                     throw new ZstdIOException(var1);
                  }

                  this.out.write(this.dst, 0, (int)this.dstPos);
                  if (var1 <= 0) {
                     this.frameClosed = true;
                     break;
                  }
               }
            } else {
               do {
                  var1 = this.flushStream(this.stream, this.dst, dstSize);
                  if (Zstd.isError(var1)) {
                     throw new ZstdIOException(var1);
                  }

                  this.out.write(this.dst, 0, (int)this.dstPos);
               } while (var1 > 0);
            }

            this.out.flush();
         }
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.close(true);
   }

   public synchronized void closeWithoutClosingParentStream() throws IOException {
      this.close(false);
   }

   private void close(boolean bl) throws IOException {
      if (!this.isClosed) {
         try {
            if (!this.frameStarted) {
               int var2 = this.resetCStream(this.stream);
               if (Zstd.isError(var2)) {
                  throw new ZstdIOException(var2);
               }

               this.frameClosed = false;
            }

            int var6;
            if (!this.frameClosed) {
               do {
                  var6 = this.endStream(this.stream, this.dst, dstSize);
                  if (Zstd.isError(var6)) {
                     throw new ZstdIOException(var6);
                  }

                  this.out.write(this.dst, 0, (int)this.dstPos);
               } while (var6 > 0);
            }

            if (bl) {
               this.out.close();
            }
         } finally {
            this.isClosed = true;
            this.bufferPool.release(this.dstByteBuffer);
            freeCStream(this.stream);
         }
      }
   }

   static {
      Native.load();
   }
}
