package pl.skidam.automodpack_core.protocol.compression;

import java.io.IOException;

public interface CompressionCodec {
   boolean isInitialized();

   byte[] compress(byte[] var1) throws IOException;

   byte[] decompress(byte[] var1, int var2) throws IOException;

   byte getCompressionType();
}
