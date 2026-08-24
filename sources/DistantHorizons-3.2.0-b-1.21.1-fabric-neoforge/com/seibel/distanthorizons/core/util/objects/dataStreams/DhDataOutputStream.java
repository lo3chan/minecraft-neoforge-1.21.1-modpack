package com.seibel.distanthorizons.core.util.objects.dataStreams;

import DistantHorizons.libraries.jpountz.lz4.LZ4Factory;
import DistantHorizons.libraries.jpountz.lz4.LZ4FrameOutputStream;
import DistantHorizons.libraries.jpountz.xxhash.XXHashFactory;
import com.seibel.distanthorizons.api.enums.config.EDhApiDataCompressionMode;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import dhcomgithubluben.zstd.Zstd;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.ResettableArrayCache;
import org.tukaani.xz.XZOutputStream;

public class DhDataOutputStream extends DataOutputStream {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ThreadLocal<ResettableArrayCache> LZMA_RESETTABLE_ARRAY_CACHE_GETTER = ThreadLocal.withInitial(
      () -> new ResettableArrayCache(new LzmaArrayCache())
   );
   private final ByteArrayList outputByteArray;
   private final ByteArrayOutputStream wrappedByteStream;
   private final EDhApiDataCompressionMode compressionMode;

   public static DhDataOutputStream create(EDhApiDataCompressionMode compressionMode, ByteArrayList outputByteArray) throws IOException {
      return new DhDataOutputStream(new ByteArrayOutputStream(), compressionMode, outputByteArray);
   }

   private DhDataOutputStream(ByteArrayOutputStream wrappedByteStream, EDhApiDataCompressionMode compressionMode, ByteArrayList outputByteArray) throws IOException {
      super(warpStream(wrappedByteStream, compressionMode));
      this.wrappedByteStream = wrappedByteStream;
      this.outputByteArray = outputByteArray;
      this.compressionMode = compressionMode;
   }

   private static OutputStream warpStream(ByteArrayOutputStream stream, EDhApiDataCompressionMode compressionMode) throws IOException {
      try {
         switch (compressionMode) {
            case UNCOMPRESSED:
               return stream;
            case LZ4:
               return new LZ4FrameOutputStream(
                  stream,
                  LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB,
                  -1L,
                  LZ4Factory.fastestInstance().fastCompressor(),
                  XXHashFactory.fastestInstance().hash32(),
                  LZ4FrameOutputStream.FLG.Bits.BLOCK_INDEPENDENCE
               );
            case Z_STD_BLOCK:
               return stream;
            case LZMA2:
               ResettableArrayCache arrayCache = LZMA_RESETTABLE_ARRAY_CACHE_GETTER.get();
               arrayCache.reset();
               return new XZOutputStream(stream, new LZMA2Options(3), 4, arrayCache);
            case Z_STD_STREAM:
               throw new UnsupportedOperationException(
                  "Z_STD_STREAM is deprecated and shouldn't be used for encoding. The faster block encoding format should be used instead."
               );
            default:
               throw new IllegalArgumentException("No compressor defined for [" + compressionMode + "]");
         }
      } catch (Error var3) {
         LOGGER.error("Unexpected error when wrapping DhDataInputStream, error: [" + var3.getMessage() + "].", var3);
         throw var3;
      }
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.outputByteArray.clear();
      if (this.compressionMode == EDhApiDataCompressionMode.Z_STD_BLOCK) {
         this.outputByteArray.addElements(0, Zstd.compress(this.wrappedByteStream.toByteArray(), 3));
      } else {
         this.outputByteArray.addElements(0, this.wrappedByteStream.toByteArray());
      }
   }
}
