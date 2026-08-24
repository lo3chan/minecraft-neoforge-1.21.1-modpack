package com.seibel.distanthorizons.core.util.objects.dataStreams;

import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import dhcomgithubluben.zstd.Zstd;
import dhcomgithubluben.zstd.ZstdDecompressCtx;
import dhcomgithubluben.zstd.ZstdException;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PooledZstdDecompressor {
   private static final ConcurrentLinkedQueue<ZstdDecompressCtx> ZSTD_CONTEXT_CACHE = new ConcurrentLinkedQueue<>();

   public static ByteArrayList decompressFrame(byte[] src, PhantomArrayListCheckout checkout) throws ZstdException {
      int compressedSize = (int)Zstd.findFrameCompressedSize(src, 0);
      int contentSize = (int)Zstd.getFrameContentSize(src, 0, compressedSize);
      if (Zstd.isError(contentSize)) {
         if (contentSize == -1) {
            throw new ZstdException(contentSize, "Content size is unknown");
         } else {
            throw new ZstdException(contentSize);
         }
      } else {
         ByteArrayList destination = checkout.getByteArray(0, contentSize);
         return decompress(src, compressedSize, contentSize, destination);
      }
   }

   private static ByteArrayList decompress(byte[] src, int srcSize, int originalSize, ByteArrayList destination) throws ZstdException {
      if (originalSize < 0) {
         throw new ZstdException(Zstd.errGeneric(), "Original size should not be negative");
      } else {
         ZstdDecompressCtx ctx = ZSTD_CONTEXT_CACHE.poll();
         if (ctx == null) {
            ctx = new ZstdDecompressCtx();
         }

         int size = ctx.decompressByteArray(destination.elements(), 0, destination.size(), src, 0, srcSize);
         if (size != originalSize) {
            destination.size(size);
         }

         ZSTD_CONTEXT_CACHE.add(ctx);
         return destination;
      }
   }
}
