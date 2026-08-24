package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.ARM;
import org.tukaani.xz.simple.ARMThumb;
import org.tukaani.xz.simple.IA64;
import org.tukaani.xz.simple.PowerPC;
import org.tukaani.xz.simple.SPARC;
import org.tukaani.xz.simple.SimpleFilter;
import org.tukaani.xz.simple.X86;

class BCJDecoder extends BCJCoder implements FilterDecoder {
   private final long filterID;
   private final int startOffset;

   BCJDecoder(long l, byte[] bs) throws UnsupportedOptionsException {
      assert isBCJFilterID(l);

      this.filterID = l;
      if (bs.length == 0) {
         this.startOffset = 0;
      } else {
         if (bs.length != 4) {
            throw new UnsupportedOptionsException("Unsupported BCJ filter properties");
         }

         int var4 = 0;

         for (int var5 = 0; var5 < 4; var5++) {
            var4 |= (bs[var5] & 255) << var5 * 8;
         }

         this.startOffset = var4;
      }
   }

   @Override
   public int getMemoryUsage() {
      return SimpleInputStream.getMemoryUsage();
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      Object var3 = null;
      if (this.filterID == 4L) {
         var3 = new X86(false, this.startOffset);
      } else if (this.filterID == 5L) {
         var3 = new PowerPC(false, this.startOffset);
      } else if (this.filterID == 6L) {
         var3 = new IA64(false, this.startOffset);
      } else if (this.filterID == 7L) {
         var3 = new ARM(false, this.startOffset);
      } else if (this.filterID == 8L) {
         var3 = new ARMThumb(false, this.startOffset);
      } else if (this.filterID == 9L) {
         var3 = new SPARC(false, this.startOffset);
      } else {
         assert false;
      }

      return new SimpleInputStream(inputStream, (SimpleFilter)var3);
   }
}
