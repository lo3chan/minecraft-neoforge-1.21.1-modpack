package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.X86;

public class X86Options extends BCJOptions {
   private static final int ALIGNMENT = 1;

   public X86Options() {
      super(1);
   }

   @Override
   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache) {
      return new SimpleOutputStream(finishableOutputStream, new X86(true, this.startOffset));
   }

   @Override
   public InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) {
      return new SimpleInputStream(inputStream, new X86(false, this.startOffset));
   }

   @Override
   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 4L);
   }
}
