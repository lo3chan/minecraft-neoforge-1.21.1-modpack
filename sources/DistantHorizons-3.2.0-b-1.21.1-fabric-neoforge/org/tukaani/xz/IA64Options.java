package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.IA64;

public class IA64Options extends BCJOptions {
   private static final int ALIGNMENT = 16;

   public IA64Options() {
      super(16);
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return new SimpleOutputStream(finishableOutputStream, new IA64(true, this.startOffset));
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      return new SimpleInputStream(inputStream, new IA64(false, this.startOffset));
   }

   @Override
   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 6L);
   }
}
