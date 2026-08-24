package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ZstdInputStreamNoFinalizer extends FilterInputStream {
   private final long stream;
   private long dstPos = 0L;
   private long srcPos = 0L;
   private long srcSize = 0L;
   private boolean needRead = true;
   private final BufferPool bufferPool;
   private final ByteBuffer srcByteBuffer;
   private final byte[] src;
   private static final int srcBuffSize = (int)recommendedDInSize();
   private boolean isContinuous = false;
   private boolean frameFinished = true;
   private boolean isClosed = false;

   public static native long recommendedDInSize();

   public static native long recommendedDOutSize();

   private static native long createDStream();

   private static native int freeDStream(long l);

   private native int initDStream(long l);

   private native int decompressStream(long l, byte[] bs, int i, byte[] cs, int j);

   public ZstdInputStreamNoFinalizer(InputStream inputStream) throws IOException {
      this(inputStream, NoPool.INSTANCE);
   }

   public ZstdInputStreamNoFinalizer(InputStream inputStream, BufferPool bufferPool) throws IOException {
      super(inputStream);
      this.bufferPool = bufferPool;
      this.srcByteBuffer = Zstd.getArrayBackedBuffer(bufferPool, srcBuffSize);
      this.src = this.srcByteBuffer.array();
      synchronized (this) {
         this.stream = createDStream();
         this.initDStream(this.stream);
      }
   }

   public synchronized ZstdInputStreamNoFinalizer setContinuous(boolean bl) {
      this.isContinuous = bl;
      return this;
   }

   public synchronized boolean getContinuous() {
      return this.isContinuous;
   }

   public synchronized ZstdInputStreamNoFinalizer setDict(byte[] bs) throws IOException {
      int var2 = Zstd.loadDictDecompress(this.stream, bs, bs.length);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   public synchronized ZstdInputStreamNoFinalizer setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
      zstdDictDecompress.acquireSharedLock();

      try {
         int var2 = Zstd.loadFastDictDecompress(this.stream, zstdDictDecompress);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         }
      } finally {
         zstdDictDecompress.releaseSharedLock();
      }

      return this;
   }

   public synchronized ZstdInputStreamNoFinalizer setLongMax(int i) throws IOException {
      int var2 = Zstd.setDecompressionLongMax(this.stream, i);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   public synchronized ZstdInputStreamNoFinalizer setRefMultipleDDicts(boolean bl) throws IOException {
      int var2 = Zstd.setRefMultipleDDicts(this.stream, bl);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   @Override
   public synchronized int read(byte[] bs, int i, int j) throws IOException {
      if (i < 0 || j > bs.length - i) {
         throw new IndexOutOfBoundsException("Requested length " + j + " from offset " + i + " in buffer of size " + bs.length);
      } else if (j == 0) {
         return 0;
      } else {
         int var4 = 0;

         while (var4 == 0) {
            var4 = this.readInternal(bs, i, j);
         }

         return var4;
      }
   }

   int readInternal(byte[] bs, int i, int j) throws IOException {
      if (this.isClosed) {
         throw new IOException("Stream closed");
      } else if (i >= 0 && j <= bs.length - i) {
         int var4 = i + j;
         this.dstPos = i;

         for (long var5 = -1L; this.dstPos < var4 && var5 < this.dstPos; this.needRead = this.dstPos < var4) {
            if (this.needRead && (this.in.available() > 0 || this.dstPos == i)) {
               this.srcSize = this.in.read(this.src, 0, srcBuffSize);
               this.srcPos = 0L;
               if (this.srcSize < 0L) {
                  this.srcSize = 0L;
                  if (this.frameFinished) {
                     return -1;
                  }

                  if (this.isContinuous) {
                     this.srcSize = (int)(this.dstPos - i);
                     if (this.srcSize > 0L) {
                        return (int)this.srcSize;
                     }

                     return -1;
                  }

                  throw new ZstdIOException(Zstd.errCorruptionDetected(), "Truncated source");
               }

               this.frameFinished = false;
            }

            var5 = this.dstPos;
            int var7 = this.decompressStream(this.stream, bs, var4, this.src, (int)this.srcSize);
            if (Zstd.isError(var7)) {
               throw new ZstdIOException(var7);
            }

            if (var7 == 0) {
               this.frameFinished = true;
               this.needRead = this.srcPos == this.srcSize;
               return (int)(this.dstPos - i);
            }
         }

         return (int)(this.dstPos - i);
      } else {
         throw new IndexOutOfBoundsException("Requested length " + j + " from offset " + i + " in buffer of size " + bs.length);
      }
   }

   @Override
   public synchronized int read() throws IOException {
      byte[] var1 = new byte[1];
      int var2 = 0;

      while (var2 == 0) {
         var2 = this.readInternal(var1, 0, 1);
      }

      return var2 == 1 ? var1[0] & 0xFF : -1;
   }

   @Override
   public synchronized int available() throws IOException {
      if (this.isClosed) {
         throw new IOException("Stream closed");
      } else {
         return !this.needRead ? 1 : this.in.available();
      }
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public synchronized long skip(long l) throws IOException {
      if (this.isClosed) {
         throw new IOException("Stream closed");
      } else if (l <= 0L) {
         return 0L;
      } else {
         int var3 = (int)recommendedDOutSize();
         if (var3 > l) {
            var3 = (int)l;
         }

         ByteBuffer var4 = Zstd.getArrayBackedBuffer(this.bufferPool, var3);
         long var5 = l;

         try {
            byte[] var7 = var4.array();

            while (var5 > 0L) {
               int var8 = this.read(var7, 0, (int)Math.min((long)var3, var5));
               if (var8 < 0) {
                  break;
               }

               var5 -= var8;
            }
         } finally {
            this.bufferPool.release(var4);
         }

         return l - var5;
      }
   }

   @Override
   public synchronized void close() throws IOException {
      if (!this.isClosed) {
         this.isClosed = true;
         this.bufferPool.release(this.srcByteBuffer);
         freeDStream(this.stream);
         this.in.close();
      }
   }

   static {
      Native.load();
   }
}
