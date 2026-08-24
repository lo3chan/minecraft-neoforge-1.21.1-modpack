package pl.skidam.automodpack_core.protocol.compression;

public class CompressionFactory {
   public static CompressionCodec getCodec(byte compressionType) {
      return (CompressionCodec)(switch (compressionType) {
         case 0 -> CompressionFactory.None.CODEC;
         case 1 -> CompressionFactory.Zstd.CODEC;
         case 2 -> CompressionFactory.Gzip.CODEC;
         default -> throw new IllegalArgumentException("Unsupported compression type: " + compressionType);
      });
   }

   private static class Gzip {
      private static final GzipCompression CODEC = new GzipCompression();
   }

   private static class None {
      private static final NoneCompression CODEC = new NoneCompression();
   }

   private static class Zstd {
      private static final ZstdCompression CODEC = new ZstdCompression();
   }
}
