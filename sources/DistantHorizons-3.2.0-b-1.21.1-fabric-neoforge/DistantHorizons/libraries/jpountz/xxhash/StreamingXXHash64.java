package DistantHorizons.libraries.jpountz.xxhash;

import java.io.Closeable;
import java.util.zip.Checksum;

public abstract class StreamingXXHash64 implements Closeable {
   final long seed;

   StreamingXXHash64(long seed) {
      this.seed = seed;
   }

   public abstract long getValue();

   public abstract void update(byte[] bs, int i, int j);

   public abstract void reset();

   @Override
   public void close() {
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "(seed=" + this.seed + ")";
   }

   public final Checksum asChecksum() {
      return new Checksum() {
         @Override
         public long getValue() {
            return StreamingXXHash64.this.getValue();
         }

         @Override
         public void reset() {
            StreamingXXHash64.this.reset();
         }

         @Override
         public void update(int b) {
            StreamingXXHash64.this.update(new byte[]{(byte)b}, 0, 1);
         }

         @Override
         public void update(byte[] b, int off, int len) {
            StreamingXXHash64.this.update(b, off, len);
         }

         @Override
         public String toString() {
            return StreamingXXHash64.this.toString();
         }
      };
   }

   interface Factory {
      StreamingXXHash64 newStreamingHash(long l);
   }
}
