package dhcomgithubluben.zstd;

import java.nio.ByteBuffer;

public class NoPool implements BufferPool {
   public static final BufferPool INSTANCE = new NoPool();

   private NoPool() {
   }

   @Override
   public ByteBuffer get(int i) {
      return ByteBuffer.allocate(i);
   }

   @Override
   public void release(ByteBuffer byteBuffer) {
   }
}
