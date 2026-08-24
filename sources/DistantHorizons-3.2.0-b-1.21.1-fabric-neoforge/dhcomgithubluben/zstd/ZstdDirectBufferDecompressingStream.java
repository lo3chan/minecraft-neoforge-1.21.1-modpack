package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferDecompressingStream implements Closeable {
   private ZstdDirectBufferDecompressingStreamNoFinalizer inner;
   private boolean finalize = true;

   protected ByteBuffer refill(ByteBuffer byteBuffer) {
      return byteBuffer;
   }

   public ZstdDirectBufferDecompressingStream(ByteBuffer byteBuffer) {
      this.inner = new ZstdDirectBufferDecompressingStreamNoFinalizer(byteBuffer) {
         @Override
         protected ByteBuffer refill(ByteBuffer byteBuffer) {
            return ZstdDirectBufferDecompressingStream.this.refill(byteBuffer);
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
      return ZstdDirectBufferDecompressingStreamNoFinalizer.recommendedTargetBufferSize();
   }

   public synchronized ZstdDirectBufferDecompressingStream setDict(byte[] bs) throws IOException {
      this.inner.setDict(bs);
      return this;
   }

   public synchronized ZstdDirectBufferDecompressingStream setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
      this.inner.setDict(zstdDictDecompress);
      return this;
   }

   public ZstdDirectBufferDecompressingStream setLongMax(int i) throws IOException {
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
