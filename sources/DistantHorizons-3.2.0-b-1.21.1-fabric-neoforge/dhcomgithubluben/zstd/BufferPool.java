package dhcomgithubluben.zstd;

import java.nio.ByteBuffer;

public interface BufferPool {
   ByteBuffer get(int i);

   void release(ByteBuffer byteBuffer);
}
