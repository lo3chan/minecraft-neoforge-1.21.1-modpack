package DistantHorizons.libraries.jpountz.xxhash;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class XXHash32 {
   public abstract int hash(byte[] bs, int i, int j, int k);

   public abstract int hash(ByteBuffer byteBuffer, int i, int j, int k);

   public final int hash(ByteBuffer buf, int seed) {
      int hash = this.hash(buf, buf.position(), buf.remaining(), seed);
      ((Buffer)buf).position(buf.limit());
      return hash;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
