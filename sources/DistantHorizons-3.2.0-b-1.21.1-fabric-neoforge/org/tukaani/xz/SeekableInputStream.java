package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class SeekableInputStream extends InputStream {
   @Override
   public long skip(long l) throws IOException {
      if (l <= 0L) {
         return 0L;
      } else {
         long var3 = this.length();
         long var5 = this.position();
         if (var5 >= var3) {
            return 0L;
         } else {
            if (var3 - var5 < l) {
               l = var3 - var5;
            }

            this.seek(var5 + l);
            return l;
         }
      }
   }

   public abstract long length() throws IOException;

   public abstract long position() throws IOException;

   public abstract void seek(long l) throws IOException;
}
