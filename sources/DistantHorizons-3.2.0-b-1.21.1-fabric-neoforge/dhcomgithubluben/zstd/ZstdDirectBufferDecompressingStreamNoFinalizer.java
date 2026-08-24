package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferDecompressingStreamNoFinalizer extends BaseZstdBufferDecompressingStreamNoFinalizer {
   public ZstdDirectBufferDecompressingStreamNoFinalizer(ByteBuffer byteBuffer) {
      super(byteBuffer);
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("Source buffer should be a direct buffer");
      } else {
         this.source = byteBuffer;
         this.stream = this.createDStream();
         this.initDStream(this.stream);
      }
   }

   @Override
   public int read(ByteBuffer byteBuffer) throws IOException {
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("Target buffer should be a direct buffer");
      } else {
         return this.readInternal(byteBuffer, true);
      }
   }

   @Override
   long createDStream() {
      return createDStreamNative();
   }

   @Override
   long freeDStream(long l) {
      return freeDStreamNative(l);
   }

   @Override
   long initDStream(long l) {
      return this.initDStreamNative(l);
   }

   @Override
   long decompressStream(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m) {
      return this.decompressStreamNative(l, byteBuffer, i, j, byteBuffer2, k, m);
   }

   public static int recommendedTargetBufferSize() {
      return (int)recommendedDOutSizeNative();
   }

   private static native long createDStreamNative();

   private static native long freeDStreamNative(long l);

   private native long initDStreamNative(long l);

   private native long decompressStreamNative(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m);

   private static native long recommendedDOutSizeNative();

   static {
      Native.load();
   }
}
