package dhcomgithubluben.zstd;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class BaseZstdBufferDecompressingStreamNoFinalizer implements Closeable {
   protected long stream;
   protected ByteBuffer source;
   protected boolean closed = false;
   private boolean finishedFrame = false;
   private boolean streamEnd = false;
   private int consumed;
   private int produced;

   BaseZstdBufferDecompressingStreamNoFinalizer(ByteBuffer byteBuffer) {
      this.source = byteBuffer;
   }

   protected ByteBuffer refill(ByteBuffer byteBuffer) {
      return byteBuffer;
   }

   public boolean hasRemaining() {
      return !this.streamEnd && (this.source.hasRemaining() || !this.finishedFrame);
   }

   public BaseZstdBufferDecompressingStreamNoFinalizer setDict(byte[] bs) throws IOException {
      long var2 = Zstd.loadDictDecompress(this.stream, bs, bs.length);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   public BaseZstdBufferDecompressingStreamNoFinalizer setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
      zstdDictDecompress.acquireSharedLock();

      try {
         long var2 = Zstd.loadFastDictDecompress(this.stream, zstdDictDecompress);
         if (Zstd.isError(var2)) {
            throw new ZstdIOException(var2);
         }
      } finally {
         zstdDictDecompress.releaseSharedLock();
      }

      return this;
   }

   public BaseZstdBufferDecompressingStreamNoFinalizer setLongMax(int i) throws IOException {
      long var2 = Zstd.setDecompressionLongMax(this.stream, i);
      if (Zstd.isError(var2)) {
         throw new ZstdIOException(var2);
      } else {
         return this;
      }
   }

   int readInternal(ByteBuffer byteBuffer, boolean bl) throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      } else if (this.streamEnd) {
         return 0;
      } else {
         long var3 = this.decompressStream(
            this.stream, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), this.source, this.source.position(), this.source.remaining()
         );
         if (Zstd.isError(var3)) {
            throw new ZstdIOException(var3);
         } else {
            ((Buffer)this.source).position(this.source.position() + this.consumed);
            ((Buffer)byteBuffer).position(byteBuffer.position() + this.produced);
            if (!this.source.hasRemaining()) {
               this.source = this.refill(this.source);
               if (!bl && this.source.isDirect()) {
                  throw new IllegalArgumentException("Source buffer should be a non-direct buffer");
               }

               if (bl && !this.source.isDirect()) {
                  throw new IllegalArgumentException("Source buffer should be a direct buffer");
               }
            }

            this.finishedFrame = var3 == 0L;
            if (this.finishedFrame) {
               this.streamEnd = !this.source.hasRemaining();
            }

            return this.produced;
         }
      }
   }

   @Override
   public void close() {
      if (!this.closed) {
         try {
            this.freeDStream(this.stream);
         } finally {
            this.closed = true;
            this.source = null;
         }
      }
   }

   public abstract int read(ByteBuffer byteBuffer) throws IOException;

   abstract long createDStream();

   abstract long freeDStream(long l);

   abstract long initDStream(long l);

   abstract long decompressStream(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m);
}
