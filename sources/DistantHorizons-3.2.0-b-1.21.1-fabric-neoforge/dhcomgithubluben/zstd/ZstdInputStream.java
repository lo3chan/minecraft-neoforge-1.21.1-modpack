package dhcomgithubluben.zstd;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZstdInputStream extends FilterInputStream {
   private ZstdInputStreamNoFinalizer inner;

   public ZstdInputStream(InputStream inputStream) throws IOException {
      super(inputStream);
      this.inner = new ZstdInputStreamNoFinalizer(inputStream);
   }

   public ZstdInputStream(InputStream inputStream, BufferPool bufferPool) throws IOException {
      super(inputStream);
      this.inner = new ZstdInputStreamNoFinalizer(inputStream, bufferPool);
   }

   @Deprecated
   public void setFinalize(boolean bl) {
   }

   @Override
   protected void finalize() throws Throwable {
      this.close();
   }

   public static long recommendedDInSize() {
      return ZstdInputStreamNoFinalizer.recommendedDInSize();
   }

   public static long recommendedDOutSize() {
      return ZstdInputStreamNoFinalizer.recommendedDOutSize();
   }

   public ZstdInputStream setContinuous(boolean bl) {
      this.inner.setContinuous(bl);
      return this;
   }

   public boolean getContinuous() {
      return this.inner.getContinuous();
   }

   public ZstdInputStream setDict(byte[] bs) throws IOException {
      this.inner.setDict(bs);
      return this;
   }

   public ZstdInputStream setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
      this.inner.setDict(zstdDictDecompress);
      return this;
   }

   public ZstdInputStream setLongMax(int i) throws IOException {
      this.inner.setLongMax(i);
      return this;
   }

   public ZstdInputStream setRefMultipleDDicts(boolean bl) throws IOException {
      this.inner.setRefMultipleDDicts(bl);
      return this;
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      return this.inner.read(bs, i, j);
   }

   @Override
   public int read() throws IOException {
      return this.inner.read();
   }

   @Override
   public int available() throws IOException {
      return this.inner.available();
   }

   @Override
   public long skip(long l) throws IOException {
      return this.inner.skip(l);
   }

   @Override
   public boolean markSupported() {
      return this.inner.markSupported();
   }

   @Override
   public void close() throws IOException {
      this.inner.close();
   }
}
