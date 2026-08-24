package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZstdDecompressCtx extends AutoCloseBase {
   private long nativePtr = 0L;
   private ZstdDictDecompress decompression_dict = null;

   private static native long init();

   private static native void free(long l);

   public ZstdDecompressCtx() {
      this.nativePtr = init();
      if (0L == this.nativePtr) {
         throw new IllegalStateException("ZSTD_createDeCompressCtx failed");
      } else {
         this.storeFence();
      }
   }

   @Override
   void doClose() {
      if (this.nativePtr != 0L) {
         free(this.nativePtr);
         this.nativePtr = 0L;
      }
   }

   public ZstdDecompressCtx setMagicless(boolean bl) {
      this.ensureOpen();
      this.acquireSharedLock();
      Zstd.setDecompressionMagicless(this.nativePtr, bl);
      this.releaseSharedLock();
      return this;
   }

   public ZstdDecompressCtx loadDict(ZstdDictDecompress zstdDictDecompress) {
      this.ensureOpen();
      this.acquireSharedLock();
      zstdDictDecompress.acquireSharedLock();

      try {
         long var2 = loadDDictFast0(this.nativePtr, zstdDictDecompress);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.decompression_dict = zstdDictDecompress;
      } finally {
         zstdDictDecompress.releaseSharedLock();
         this.releaseSharedLock();
      }

      return this;
   }

   private static native long loadDDictFast0(long l, ZstdDictDecompress zstdDictDecompress);

   public ZstdDecompressCtx loadDict(byte[] bs) {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var2 = loadDDict0(this.nativePtr, bs);
         if (Zstd.isError(var2)) {
            throw new ZstdException(var2);
         }

         this.decompression_dict = null;
      } finally {
         this.releaseSharedLock();
      }

      return this;
   }

   private static native long loadDDict0(long l, byte[] bs);

   public void reset() {
      this.ensureOpen();
      this.acquireSharedLock();

      try {
         long var1 = reset0(this.nativePtr);
         if (Zstd.isError(var1)) {
            throw new ZstdException(var1);
         }
      } finally {
         this.releaseSharedLock();
      }
   }

   private static native long reset0(long l);

   private void ensureOpen() {
      if (this.nativePtr == 0L) {
         throw new IllegalStateException("Decompression context is closed");
      }
   }

   public boolean decompressDirectByteBufferStream(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
      this.ensureOpen();
      this.acquireSharedLock();

      boolean var5;
      try {
         long var3 = decompressDirectByteBufferStream0(
            this.nativePtr, byteBuffer, byteBuffer.position(), byteBuffer.limit(), byteBuffer2, byteBuffer2.position(), byteBuffer2.limit()
         );
         if ((var3 & 2147483648L) != 0L) {
            long var10 = -(var3 & 255L);
            throw new ZstdException(var10, Zstd.getErrorName(var10));
         }

         ((Buffer)byteBuffer2).position((int)(var3 & 2147483647L));
         ((Buffer)byteBuffer).position((int)(var3 >>> 32) & 2147483647);
         var5 = var3 >>> 63 == 1L;
      } finally {
         this.releaseSharedLock();
      }

      return var5;
   }

   private static native long decompressDirectByteBufferStream0(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m);

   public int decompressDirectByteBuffer(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l) {
      this.ensureOpen();
      if (!byteBuffer2.isDirect()) {
         throw new IllegalArgumentException("srcBuff must be a direct buffer");
      } else if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("dstBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(k, l, byteBuffer2.limit());
         Objects.checkFromIndexSize(i, j, byteBuffer.limit());
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = decompressDirectByteBuffer0(this.nativePtr, byteBuffer, i, j, byteBuffer2, k, l);
            if (Zstd.isError(var7)) {
               throw new ZstdException(var7);
            }

            if (var7 > 2147483647L) {
               throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }

            var9 = (int)var7;
         } finally {
            this.releaseSharedLock();
         }

         return var9;
      }
   }

   private static native long decompressDirectByteBuffer0(long l, ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int m);

   public int decompressByteArray(byte[] bs, int i, int j, byte[] cs, int k, int l) {
      Objects.checkFromIndexSize(k, l, cs.length);
      Objects.checkFromIndexSize(i, j, bs.length);
      this.ensureOpen();
      this.acquireSharedLock();

      int var9;
      try {
         long var7 = decompressByteArray0(this.nativePtr, bs, i, j, cs, k, l);
         if (Zstd.isError(var7)) {
            throw new ZstdException(var7);
         }

         if (var7 > 2147483647L) {
            throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
         }

         var9 = (int)var7;
      } finally {
         this.releaseSharedLock();
      }

      return var9;
   }

   private static native long decompressByteArray0(long l, byte[] bs, int i, int j, byte[] cs, int k, int m);

   public int decompressByteArrayToDirectByteBuffer(ByteBuffer byteBuffer, int i, int j, byte[] bs, int k, int l) {
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("dstBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(k, l, bs.length);
         Objects.checkFromIndexSize(i, j, byteBuffer.limit());
         this.ensureOpen();
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = decompressByteArrayToDirectByteBuffer0(this.nativePtr, byteBuffer, i, j, bs, k, l);
            if (Zstd.isError(var7)) {
               throw new ZstdException(var7);
            }

            if (var7 > 2147483647L) {
               throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }

            var9 = (int)var7;
         } finally {
            this.releaseSharedLock();
         }

         return var9;
      }
   }

   private static native long decompressByteArrayToDirectByteBuffer0(long l, ByteBuffer byteBuffer, int i, int j, byte[] bs, int k, int m);

   public int decompressDirectByteBufferToByteArray(byte[] bs, int i, int j, ByteBuffer byteBuffer, int k, int l) {
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("srcBuff must be a direct buffer");
      } else {
         Objects.checkFromIndexSize(k, l, byteBuffer.limit());
         Objects.checkFromIndexSize(i, j, bs.length);
         this.ensureOpen();
         this.acquireSharedLock();

         int var9;
         try {
            long var7 = decompressDirectByteBufferToByteArray0(this.nativePtr, bs, i, j, byteBuffer, k, l);
            if (Zstd.isError(var7)) {
               throw new ZstdException(var7);
            }

            if (var7 > 2147483647L) {
               throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }

            var9 = (int)var7;
         } finally {
            this.releaseSharedLock();
         }

         return var9;
      }
   }

   private static native long decompressDirectByteBufferToByteArray0(long l, byte[] bs, int i, int j, ByteBuffer byteBuffer, int k, int m);

   public int decompress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws ZstdException {
      int var3 = this.decompressDirectByteBuffer(
         byteBuffer,
         byteBuffer.position(),
         byteBuffer.limit() - byteBuffer.position(),
         byteBuffer2,
         byteBuffer2.position(),
         byteBuffer2.limit() - byteBuffer2.position()
      );
      ((Buffer)byteBuffer2).position(byteBuffer2.limit());
      ((Buffer)byteBuffer).position(byteBuffer.position() + var3);
      return var3;
   }

   public int decompress(ByteBuffer byteBuffer, byte[] bs) throws ZstdException {
      int var3 = this.decompressByteArrayToDirectByteBuffer(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position(), bs, 0, bs.length);
      ((Buffer)byteBuffer).position(byteBuffer.position() + var3);
      return var3;
   }

   public int decompress(byte[] bs, ByteBuffer byteBuffer) throws ZstdException {
      int var3 = this.decompressDirectByteBufferToByteArray(bs, 0, bs.length, byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
      ((Buffer)byteBuffer).position(byteBuffer.limit());
      return var3;
   }

   public ByteBuffer decompress(ByteBuffer byteBuffer, int i) throws ZstdException {
      ByteBuffer var3 = ByteBuffer.allocateDirect(i);
      int var4 = this.decompressDirectByteBuffer(var3, 0, i, byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
      ((Buffer)byteBuffer).position(byteBuffer.limit());
      return var3;
   }

   public int decompress(byte[] bs, byte[] cs) {
      return this.decompressByteArray(bs, 0, bs.length, cs, 0, cs.length);
   }

   public byte[] decompress(byte[] bs, int i) throws ZstdException {
      return this.decompress(bs, 0, bs.length, i);
   }

   public byte[] decompress(byte[] bs, int i, int j, int k) throws ZstdException {
      if (k < 0) {
         throw new ZstdException(Zstd.errGeneric(), "Original size should not be negative");
      } else {
         byte[] var5 = new byte[k];
         int var6 = this.decompressByteArray(var5, 0, var5.length, bs, i, j);
         return var6 != k ? Arrays.copyOfRange(var5, 0, var6) : var5;
      }
   }

   static {
      Native.load();
   }
}
