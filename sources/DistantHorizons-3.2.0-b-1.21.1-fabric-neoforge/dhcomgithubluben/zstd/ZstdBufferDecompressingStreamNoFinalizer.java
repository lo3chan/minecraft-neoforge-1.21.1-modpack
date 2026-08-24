package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdBufferDecompressingStreamNoFinalizer extends BaseZstdBufferDecompressingStreamNoFinalizer {
   public ZstdBufferDecompressingStreamNoFinalizer(ByteBuffer byteBuffer) {
      super(byteBuffer);
      if (byteBuffer.isDirect()) {
         throw new IllegalArgumentException("Source buffer should be a non-direct buffer");
      } else {
         this.stream = this.createDStream();
         this.initDStream(this.stream);
      }
   }

   @Override
   public int read(ByteBuffer byteBuffer) throws IOException {
      if (byteBuffer.isDirect()) {
         throw new IllegalArgumentException("Target buffer should be a non-direct buffer");
      } else {
         return this.readInternal(byteBuffer, false);
      }
   }

   @Override
   long createDStream() {
      return this.createDStreamNative();
   }

   @Override
   long freeDStream(long l) {
      return this.freeDStreamNative(l);
   }

   @Override
   long initDStream(long l) {
      return this.initDStreamNative(l);
   }

   @Override
   long decompressStream(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m) {
      if (!byteBuffer2.hasArray()) {
         throw new IllegalArgumentException("provided source ByteBuffer lacks array");
      } else if (!byteBuffer.hasArray()) {
         throw new IllegalArgumentException("provided destination ByteBuffer lacks array");
      } else {
         byte[] var9 = byteBuffer.array();
         byte[] var10 = byteBuffer2.array();
         return this.decompressStreamNative(l, var9, i + byteBuffer.arrayOffset(), j, var10, k + byteBuffer2.arrayOffset(), m);
      }
   }

   public static int recommendedTargetBufferSize() {
      return (int)recommendedDOutSizeNative();
   }

   private native long createDStreamNative();

   private native long freeDStreamNative(long l);

   private native long initDStreamNative(long l);

   private native long decompressStreamNative(long l, byte[] bs, int i, int j, byte[] cs, int k, int m);

   private static native long recommendedDOutSizeNative();

   static {
      Native.load();
   }
}
