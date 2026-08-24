package com.seibel.distanthorizons.core.util.objects.dataStreams;

import DistantHorizons.libraries.jpountz.lz4.LZ4FrameInputStream;
import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.objects.pooling.PhantomArrayList.PhantomArrayListCheckout;
import dhcomgithubluben.zstd.RecyclingBufferPool;
import dhcomgithubluben.zstd.ZstdInputStream;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.tukaani.xz.ResettableArrayCache;
import org.tukaani.xz.XZInputStream;

public class DhDataInputStream extends DataInputStream {
   private static final ThreadLocal<ResettableArrayCache> LZMA_RESETTABLE_ARRAY_CACHE_GETTER = ThreadLocal.withInitial(
      () -> new ResettableArrayCache(new LzmaArrayCache())
   );
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static DhDataInputStream create(ByteArrayList byteArrayList, EDhApiDataCompressionMode compressionMode, PhantomArrayListCheckout checkout) throws IOException {
      return create(byteArrayList.toByteArray(), compressionMode, checkout);
   }

   public static DhDataInputStream create(byte[] byteArray, EDhApiDataCompressionMode compressionMode, PhantomArrayListCheckout checkout) throws IOException {
      ByteArrayInputStream byteArrayInputStream;
      if (compressionMode == EDhApiDataCompressionMode.Z_STD_BLOCK) {
         ByteArrayList pooledByteArrayList = PooledZstdDecompressor.decompressFrame(byteArray, checkout);
         byteArrayInputStream = new ByteArrayInputStream(pooledByteArrayList.elements(), 0, pooledByteArrayList.size());
      } else {
         byteArrayInputStream = new ByteArrayInputStream(byteArray);
      }

      return new DhDataInputStream(byteArrayInputStream, compressionMode);
   }

   private DhDataInputStream(ByteArrayInputStream stream, EDhApiDataCompressionMode compressionMode) throws IOException {
      super(warpStream(stream, compressionMode));
   }

   private static InputStream warpStream(ByteArrayInputStream stream, EDhApiDataCompressionMode compressionMode) throws IOException {
      try {
         switch (compressionMode) {
            case UNCOMPRESSED:
               return stream;
            case LZ4:
               return new LZ4FrameInputStream(stream);
            case Z_STD_BLOCK:
               return stream;
            case LZMA2:
               ResettableArrayCache arrayCache = LZMA_RESETTABLE_ARRAY_CACHE_GETTER.get();
               arrayCache.reset();
               return new XZInputStream(stream, arrayCache);
            case Z_STD_STREAM:
               return new ZstdInputStream(stream, RecyclingBufferPool.INSTANCE);
            default:
               throw new IllegalArgumentException("No compressor defined for [" + compressionMode + "]");
         }
      } catch (Error var3) {
         LOGGER.error("Unexpected error when wrapping DhDataInputStream, error: [" + var3.getMessage() + "].", var3);
         throw var3;
      }
   }

   @Override
   public int read() throws IOException {
      try {
         return super.read();
      } catch (EOFException var2) {
         return -1;
      } catch (IOException var3) {
         if (var3.getMessage().equals("Stream ended prematurely")) {
            return -1;
         } else {
            throw var3;
         }
      }
   }
}
