package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.ARMThumb;

public class ARMThumbOptions extends BCJOptions {
   private static final int ALIGNMENT = 2;

   public ARMThumbOptions() {
      super(2);
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return new SimpleOutputStream(finishableOutputStream, new ARMThumb(true, this.startOffset));
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      return new SimpleInputStream(inputStream, new ARMThumb(false, this.startOffset));
   }

   @Override
   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 8L);
   }
}
