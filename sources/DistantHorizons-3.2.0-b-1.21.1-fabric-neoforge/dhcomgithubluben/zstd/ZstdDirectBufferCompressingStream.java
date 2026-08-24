package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferCompressingStream implements Closeable, Flushable {
   ZstdDirectBufferCompressingStreamNoFinalizer inner;
   private boolean finalize;

   protected ByteBuffer flushBuffer(ByteBuffer byteBuffer) throws IOException {
      return byteBuffer;
   }

   public ZstdDirectBufferCompressingStream(ByteBuffer byteBuffer, int i) throws IOException {
      this.inner = new ZstdDirectBufferCompressingStreamNoFinalizer(byteBuffer, i) {
         @Override
         protected ByteBuffer flushBuffer(ByteBuffer byteBuffer) throws IOException {
            return ZstdDirectBufferCompressingStream.this.flushBuffer(byteBuffer);
         }
      };
   }

   public static int recommendedOutputBufferSize() {
      return ZstdDirectBufferCompressingStreamNoFinalizer.recommendedOutputBufferSize();
   }

   public synchronized ZstdDirectBufferCompressingStream setDict(byte[] bs) throws IOException {
      this.inner.setDict(bs);
      return this;
   }

   public synchronized ZstdDirectBufferCompressingStream setDict(ZstdDictCompress zstdDictCompress) throws IOException {
      this.inner.setDict(zstdDictCompress);
      return this;
   }

   public void setFinalize(boolean bl) {
      this.finalize = bl;
   }

   public synchronized void compress(ByteBuffer byteBuffer) throws IOException {
      this.inner.compress(byteBuffer);
   }

   @Override
   public synchronized void flush() throws IOException {
      this.inner.flush();
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
