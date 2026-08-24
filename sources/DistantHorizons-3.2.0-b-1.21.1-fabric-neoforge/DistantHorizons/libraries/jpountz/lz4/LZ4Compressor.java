package DistantHorizons.libraries.jpountz.lz4;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class LZ4Compressor {
   public final int maxCompressedLength(int length) {
      return LZ4Utils.maxCompressedLength(length);
   }

   public abstract int compress(byte[] bs, int i, int j, byte[] cs, int k, int l);

   public abstract int compress(ByteBuffer byteBuffer, int i, int j, ByteBuffer byteBuffer2, int k, int l);

   public final int compress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff) {
      return this.compress(src, srcOff, srcLen, dest, destOff, dest.length - destOff);
   }

   public final int compress(byte[] src, byte[] dest) {
      return this.compress(src, 0, src.length, dest, 0);
   }

   public final byte[] compress(byte[] src, int srcOff, int srcLen) {
      int maxCompressedLength = this.maxCompressedLength(srcLen);
      byte[] compressed = new byte[maxCompressedLength];
      int compressedLength = this.compress(src, srcOff, srcLen, compressed, 0);
      return Arrays.copyOf(compressed, compressedLength);
   }

   public final byte[] compress(byte[] src) {
      return this.compress(src, 0, src.length);
   }

   public final void compress(ByteBuffer src, ByteBuffer dest) {
      int cpLen = this.compress(src, src.position(), src.remaining(), dest, dest.position(), dest.remaining());
      ((Buffer)src).position(src.limit());
      ((Buffer)dest).position(dest.position() + cpLen);
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
