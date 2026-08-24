package pl.skidam.automodpack_core.protocol.compression;

public class NoneCompression implements CompressionCodec {
   @Override
   public boolean isInitialized() {
      return true;
   }

   @Override
   public byte[] compress(byte[] input) {
      return input;
   }

   @Override
   public byte[] decompress(byte[] compressed, int originalLength) {
      return compressed;
   }

   @Override
   public byte getCompressionType() {
      return 0;
   }
}
