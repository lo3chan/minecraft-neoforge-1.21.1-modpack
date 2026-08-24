package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdBufferDecompressingStream implements Closeable {
   private final ZstdBufferDecompressingStreamNoFinalizer inner;
   private boolean finalize = true;

   protected ByteBuffer refill(ByteBuffer byteBuffer) {
      return byteBuffer;
   }

   public ZstdBufferDecompressingStream(ByteBuffer byteBuffer) {
      this.inner = new ZstdBufferDecompressingStreamNoFinalizer(byteBuffer) {
         @Override
         protected ByteBuffer refill(ByteBuffer byteBuffer) {
            return ZstdBufferDecompressingStream.this.refill(byteBuffer);
         }
      };
   }

   public void setFinalize(boolean bl) {
      this.finalize = bl;
   }

   public synchronized boolean hasRemaining() {
      return this.inner.hasRemaining();
   }

   public static int recommendedTargetBufferSize() {
      return ZstdBufferDecompressingStreamNoFinalizer.recommendedTargetBufferSize();
   }

   public synchronized ZstdBufferDecompressingStream setDict(byte[] bs) throws IOException {
      this.inner.setDict(bs);
      return this;
   }

   public synchronized ZstdBufferDecompressingStream setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
      this.inner.setDict(zstdDictDecompress);
      return this;
   }

   public ZstdBufferDecompressingStream setLongMax(int i) throws IOException {
      this.inner.setLongMax(i);
      return this;
   }

   public synchronized int read(ByteBuffer byteBuffer) throws IOException {
      return this.inner.read(byteBuffer);
   }

   @Override
   public synchronized void close() throws IOException {
      this.inner.close();
   }

   @Override
   protected void finalize() throws Throwable {
      if (this.finalize) {
         this.close();
      }
   }

   static {
      Native.load();
   }
}
