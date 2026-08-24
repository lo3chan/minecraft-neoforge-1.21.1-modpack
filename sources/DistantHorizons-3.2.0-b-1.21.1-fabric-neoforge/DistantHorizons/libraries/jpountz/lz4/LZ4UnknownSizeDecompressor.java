package DistantHorizons.libraries.jpountz.lz4;

@Deprecated
public interface LZ4UnknownSizeDecompressor {
   int decompress(byte[] bs, int i, int j, byte[] cs, int k, int l);

   int decompress(byte[] bs, int i, int j, byte[] cs, int k);
}
