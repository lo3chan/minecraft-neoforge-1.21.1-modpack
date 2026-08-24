package DistantHorizons.libraries.jpountz.lz4;

import DistantHorizons.libraries.jpountz.util.Native;
import java.nio.ByteBuffer;

enum LZ4JNI {
   static native void init();

   static native int LZ4_compress_limitedOutput(byte[] bs, ByteBuffer byteBuffer, int i, int j, byte[] cs, ByteBuffer byteBuffer2, int k, int l);

   static native int LZ4_compressHC(byte[] bs, ByteBuffer byteBuffer, int i, int j, byte[] cs, ByteBuffer byteBuffer2, int k, int l, int m);

   static native int LZ4_decompress_fast(byte[] bs, ByteBuffer byteBuffer, int i, byte[] cs, ByteBuffer byteBuffer2, int j, int k);

   static native int LZ4_decompress_safe(byte[] bs, ByteBuffer byteBuffer, int i, int j, byte[] cs, ByteBuffer byteBuffer2, int k, int l);

   static native int LZ4_compressBound(int i);

   static {
      Native.load();
      init();
   }
}
