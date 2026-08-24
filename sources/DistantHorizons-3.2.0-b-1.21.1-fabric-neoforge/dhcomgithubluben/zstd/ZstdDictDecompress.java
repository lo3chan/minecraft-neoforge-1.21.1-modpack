package dhcomgithubluben.zstd;

import dhcomgithubluben.zstd.util.Native;
import java.nio.ByteBuffer;

public class ZstdDictDecompress extends SharedDictBase {
   private long nativePtr = 0L;
   private ByteBuffer sharedDict = null;

   private native void init(byte[] bs, int i, int j);

   private native void initDirect(ByteBuffer byteBuffer, int i, int j, int k);

   private native void free();

   public ByteBuffer getByReferenceBuffer() {
      return this.sharedDict;
   }

   public ZstdDictDecompress(byte[] bs) {
      this(bs, 0, bs.length);
   }

   public ZstdDictDecompress(byte[] bs, int i, int j) {
      this.init(bs, i, j);
      if (this.nativePtr == 0L) {
         throw new IllegalStateException("ZSTD_createDDict failed");
      } else {
         this.storeFence();
      }
   }

   public ZstdDictDecompress(ByteBuffer byteBuffer) {
      this(byteBuffer, false);
   }

   public ZstdDictDecompress(ByteBuffer byteBuffer, boolean bl) {
      int var3 = byteBuffer.limit() - byteBuffer.position();
      if (!byteBuffer.isDirect()) {
         throw new IllegalArgumentException("dict must be a direct buffer");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("dict cannot be empty.");
      } else {
         this.initDirect(byteBuffer, byteBuffer.position(), var3, bl ? 1 : 0);
         if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createDDict failed");
         } else {
            if (bl) {
               this.sharedDict = byteBuffer;
            }

            this.storeFence();
         }
      }
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
