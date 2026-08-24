package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ZstdDirectBufferCompressingStreamNoFinalizer implements Closeable, Flushable {
   private ByteBuffer target;
   private final long stream;
   private int consumed = 0;
   private int produced = 0;
   private boolean closed = false;
   private boolean initialized = false;
   private int level = Zstd.defaultCompressionLevel();
   private byte[] dict = null;
   private ZstdDictCompress fastDict = null;

   protected ByteBuffer flushBuffer(ByteBuffer byteBuffer) throws IOException {
      return byteBuffer;
   }

   public ZstdDirectBufferCompressingStreamNoFinalizer(ByteBuffer byteBuffer, int i) throws IOException {
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("Target buffer should be a direct buffer");
      } else {
         this.target = byteBuffer;
         this.level = i;
         this.stream = createCStream();
      }
   }

   public static int recommendedOutputBufferSize() {
      return (int)recommendedCOutSize();
   }

   private static native long recommendedCOutSize();

   private static native long createCStream();

   private static native long freeCStream(long l);

   private native long initCStream(long l, int i);

   private native long initCStreamWithDict(long l, byte[] bs, int i, int j);

   private native long initCStreamWithFastDict(long l, ZstdDictCompress zstdDictCompress);

   private native long compressDirectByteBuffer(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m);

   private native long flushStream(long l, ByteBuffer byteBuffer, int i, int j);

   private native long endStream(long l, ByteBuffer byteBuffer, int i, int j);

   public ZstdDirectBufferCompressingStreamNoFinalizer setDict(byte[] bs) {
      if (this.initialized) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         this.dict = bs;
         this.fastDict = null;
         return this;
      }
   }

   public ZstdDirectBufferCompressingStreamNoFinalizer setDict(ZstdDictCompress zstdDictCompress) {
      if (this.initialized) {
         throw new IllegalStateException("Change of parameter on initialized stream");
      } else {
         this.dict = null;
         this.fastDict = zstdDictCompress;
         return this;
      }
   }

   public void compress(ByteBuffer byteBuffer) throws IOException {
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("Source buffer should be a direct buffer");
      } else if (this.closed) {
         throw new IOException("Stream closed");
      } else {
         if (!this.initialized) {
            long var2 = 0L;
            ZstdDictCompress var4 = this.fastDict;
            if (var4 != null) {
               var4.acquireSharedLock();

               try {
                  var2 = this.initCStreamWithFastDict(this.stream, var4);
               } finally {
                  var4.releaseSharedLock();
               }
            } else if (this.dict != null) {
               var2 = this.initCStreamWithDict(this.stream, this.dict, this.dict.length, this.level);
            } else {
               var2 = this.initCStream(this.stream, this.level);
            }

            if (Zstd.isError(var2)) {
               throw new ZstdIOException(var2);
            }

            this.initialized = true;
         }

         while (byteBuffer.hasRemaining()) {
            if (!this.target.hasRemaining()) {
               this.target = this.flushBuffer(this.target);
               if (!this.target.isDirect()) {
                  throw new IllegalArgumentException("Target buffer should be a direct buffer");
               }

               if (!this.target.hasRemaining()) {
                  throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
               }
            }

            long var9 = this.compressDirectByteBuffer(
               this.stream, this.target, this.target.position(), this.target.remaining(), byteBuffer, byteBuffer.position(), byteBuffer.remaining()
            );
            if (Zstd.isError(var9)) {
               throw new ZstdIOException(var9);
            }

            ((Buffer)this.target).position(this.target.position() + this.produced);
            ((Buffer)byteBuffer).position(byteBuffer.position() + this.consumed);
         }
      }
   }

   @Override
   public void flush() throws IOException {
      if (this.closed) {
         throw new IOException("Already closed");
      } else {
         long var1;
         if (this.initialized) {
            do {
               var1 = this.flushStream(this.stream, this.target, this.target.position(), this.target.remaining());
               if (Zstd.isError(var1)) {
                  throw new ZstdIOException(var1);
               }

               ((Buffer)this.target).position(this.target.position() + this.produced);
               this.target = this.flushBuffer(this.target);
               if (!this.target.isDirect()) {
                  throw new IllegalArgumentException("Target buffer should be a direct buffer");
               }

               if (var1 > 0L && !this.target.hasRemaining()) {
                  throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
               }
            } while (var1 > 0L);
         }
      }
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         try {
            long var1;
            if (this.initialized) {
               do {
                  var1 = this.endStream(this.stream, this.target, this.target.position(), this.target.remaining());
                  if (Zstd.isError(var1)) {
                     throw new ZstdIOException(var1);
                  }

                  ((Buffer)this.target).position(this.target.position() + this.produced);
                  this.target = this.flushBuffer(this.target);
                  if (!this.target.isDirect()) {
                     throw new IllegalArgumentException("Target buffer should be a direct buffer");
                  }

                  if (var1 > 0L && !this.target.hasRemaining()) {
                     throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
                  }
               } while (var1 > 0L);
            }
         } finally {
            freeCStream(this.stream);
            this.closed = true;
            this.initialized = false;
            this.target = null;
         }
      }
   }

   static {
      Native.load();
   }
}
