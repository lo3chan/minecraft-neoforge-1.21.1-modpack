package dhcomgithubluben.zstd;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ZstdOutputStream extends FilterOutputStream {
   private ZstdOutputStreamNoFinalizer inner;

   @Deprecated
   public ZstdOutputStream(OutputStream outputStream, int i, boolean bl, boolean bl2) throws IOException {
      super(outputStream);
      this.inner = new ZstdOutputStreamNoFinalizer(outputStream, i);
      this.inner.setCloseFrameOnFlush(bl);
      this.inner.setChecksum(bl2);
   }

   @Deprecated
   public ZstdOutputStream(OutputStream outputStream, int i, boolean bl) throws IOException {
      super(outputStream);
      this.inner = new ZstdOutputStreamNoFinalizer(outputStream, i);
      this.inner.setCloseFrameOnFlush(bl);
   }

   public ZstdOutputStream(OutputStream outputStream, int i) throws IOException {
      this(outputStream, NoPool.INSTANCE);
      this.inner.setLevel(i);
   }

   public ZstdOutputStream(OutputStream outputStream) throws IOException {
      this(outputStream, NoPool.INSTANCE);
   }

   public ZstdOutputStream(OutputStream outputStream, BufferPool bufferPool, int i) throws IOException {
      this(outputStream, bufferPool);
      this.inner.setLevel(i);
   }

   public ZstdOutputStream(OutputStream outputStream, BufferPool bufferPool) throws IOException {
      super(outputStream);
      this.inner = new ZstdOutputStreamNoFinalizer(outputStream, bufferPool);
   }

   @Deprecated
   public void setFinalize(boolean bl) {
   }

   @Override
   protected void finalize() throws Throwable {
      this.close();
   }

   public static long recommendedCOutSize() {
      return ZstdOutputStreamNoFinalizer.recommendedCOutSize();
   }

   public ZstdOutputStream setChecksum(boolean bl) throws IOException {
      this.inner.setChecksum(bl);
      return this;
   }

   public ZstdOutputStream setLevel(int i) throws IOException {
      this.inner.setLevel(i);
      return this;
   }

   public ZstdOutputStream setLong(int i) throws IOException {
      this.inner.setLong(i);
      return this;
   }

   public ZstdOutputStream setWorkers(int i) throws IOException {
      this.inner.setWorkers(i);
      return this;
   }

   public ZstdOutputStream setOverlapLog(int i) throws IOException {
      this.inner.setOverlapLog(i);
      return this;
   }

   public ZstdOutputStream setJobSize(int i) throws IOException {
      this.inner.setJobSize(i);
      return this;
   }

   public ZstdOutputStream setTargetLength(int i) throws IOException {
      this.inner.setTargetLength(i);
      return this;
   }

   public ZstdOutputStream setMinMatch(int i) throws IOException {
      this.inner.setMinMatch(i);
      return this;
   }

   public ZstdOutputStream setSearchLog(int i) throws IOException {
      this.inner.setSearchLog(i);
      return this;
   }

   public ZstdOutputStream setChainLog(int i) throws IOException {
      this.inner.setChainLog(i);
      return this;
   }

   public ZstdOutputStream setHashLog(int i) throws IOException {
      this.inner.setHashLog(i);
      return this;
   }

   public ZstdOutputStream setWindowLog(int i) throws IOException {
      this.inner.setWindowLog(i);
      return this;
   }

   public ZstdOutputStream setStrategy(int i) throws IOException {
      this.inner.setStrategy(i);
      return this;
   }

   public ZstdOutputStream setCloseFrameOnFlush(boolean bl) {
      this.inner.setCloseFrameOnFlush(bl);
      return this;
   }

   public ZstdOutputStream setDict(byte[] bs) throws IOException {
      this.inner.setDict(bs);
      return this;
   }

   public ZstdOutputStream setDict(ZstdDictCompress zstdDictCompress) throws IOException {
      this.inner.setDict(zstdDictCompress);
      return this;
   }

   @Override
   public void write(byte[] bs, int i, int j) throws IOException {
      this.inner.write(bs, i, j);
   }

   @Override
   public void write(int i) throws IOException {
      this.inner.write(i);
   }

   @Override
   public void flush() throws IOException {
      this.inner.flush();
   }

   @Override
   public void close() throws IOException {
      this.inner.close();
   }
}
