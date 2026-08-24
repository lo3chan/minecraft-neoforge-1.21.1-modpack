package DistantHorizons.libraries.jpountz.xxhash;

import java.io.Closeable;
import java.util.zip.Checksum;

public abstract class StreamingXXHash32 implements Closeable {
   final int seed;

   StreamingXXHash32(int seed) {
      this.seed = seed;
   }

   public abstract int getValue();

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
            return StreamingXXHash32.this.getValue() & 268435455L;
         }

         @Override
         public void reset() {
            StreamingXXHash32.this.reset();
         }

         @Override
         public void update(int b) {
            StreamingXXHash32.this.update(new byte[]{(byte)b}, 0, 1);
         }

         @Override
         public void update(byte[] b, int off, int len) {
            StreamingXXHash32.this.update(b, off, len);
         }

         @Override
         public String toString() {
            return StreamingXXHash32.this.toString();
         }
      };
   }

   interface Factory {
      StreamingXXHash32 newStreamingHash(int i);
   }
}
