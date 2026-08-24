package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.nio.ByteBuffer;

public class ZstdDictCompress extends SharedDictBase {
   private long nativePtr = 0L;
   private ByteBuffer sharedDict = null;
   private int level = Zstd.defaultCompressionLevel();

   private native void init(byte[] bs, int i, int j, int k);

   private native void initDirect(ByteBuffer byteBuffer, int i, int j, int k, int l);

   private native void free();

   public ByteBuffer getByReferenceBuffer() {
      return this.sharedDict;
   }

   public ZstdDictCompress(byte[] bs, int i) {
      this(bs, 0, bs.length, i);
   }

   public ZstdDictCompress(byte[] bs, int i, int j, int k) {
      this.level = k;
      if (bs.length - i < 0) {
         throw new IllegalArgumentException("Dictionary buffer is to short");
      } else {
         this.init(bs, i, j, k);
         if (0L == this.nativePtr) {
            throw new IllegalStateException("ZSTD_createCDict failed");
         } else {
            this.storeFence();
         }
      }
   }

   public ZstdDictCompress(ByteBuffer byteBuffer, int i) {
      this(byteBuffer, i, false);
   }

   public ZstdDictCompress(ByteBuffer byteBuffer, int i, boolean bl) {
      this.level = i;
      int var4 = byteBuffer.limit() - byteBuffer.position();
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("dict must be a direct buffer");
      } else if (var4 < 0) {
         throw new IllegalArgumentException("dict cannot be empty.");
      } else {
         this.initDirect(byteBuffer, byteBuffer.position(), var4, i, bl ? 1 : 0);
         if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createCDict failed");
         } else {
            if (bl) {
               this.sharedDict = byteBuffer;
            }

            this.storeFence();
         }
      }
   }

   int level() {
      return this.level;
   }

   @Override
   void doClose() {
      if (this.nativePtr != 0L) {
         this.free();
         this.nativePtr = 0L;
         this.sharedDict = null;
      }
   }

   static {
      Native.load();
   }
}
