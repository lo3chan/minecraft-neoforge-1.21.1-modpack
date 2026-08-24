package org.tukaani.xz.common;

public class Util {
   public static final int STREAM_HEADER_SIZE = 12;
   public static final long BACKWARD_SIZE_MAX = 17179869184L;
   public static final int BLOCK_HEADER_SIZE_MAX = 1024;
   public static final long VLI_MAX = 9223372036854775807L;
   public static final int VLI_SIZE_MAX = 9;

   public static int getVLISize(long l) {
      int var2 = 0;

      do {
         var2++;
         l >>= 7;
      } while (l != 0L);

      return var2;
   }
}
