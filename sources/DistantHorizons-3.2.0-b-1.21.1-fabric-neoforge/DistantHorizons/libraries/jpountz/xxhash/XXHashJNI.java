package DistantHorizons.libraries.jpountz.xxhash;

import DistantHorizons.libraries.jpountz.util.Native;
import java.nio.ByteBuffer;

enum XXHashJNI {
   private static native void init();

   static native int XXH32(byte[] bs, int i, int j, int k);

   static native int XXH32BB(ByteBuffer byteBuffer, int i, int j, int k);

   static native long XXH32_init(int i);

   static native void XXH32_update(long l, byte[] bs, int i, int j);

   static native int XXH32_digest(long l);

   static native void XXH32_free(long l);

   static native long XXH64(byte[] bs, int i, int j, long l);

   static native long XXH64BB(ByteBuffer byteBuffer, int i, int j, long l);

   static native long XXH64_init(long l);

   static native void XXH64_update(long l, byte[] bs, int i, int j);

   static native long XXH64_digest(long l);

   static native void XXH64_free(long l);

   static {
      Native.load();
      init();
   }
}
