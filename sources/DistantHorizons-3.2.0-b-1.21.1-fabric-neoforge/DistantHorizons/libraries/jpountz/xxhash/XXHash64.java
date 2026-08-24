package DistantHorizons.libraries.jpountz.xxhash;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class XXHash64 {
   public abstract long hash(byte[] bs, int i, int j, long l);

   public abstract long hash(ByteBuffer byteBuffer, int i, int j, long l);

   public final long hash(ByteBuffer buf, long seed) {
      long hash = this.hash(buf, buf.position(), buf.remaining(), seed);
      ((Buffer)buf).position(buf.limit());
      return hash;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
