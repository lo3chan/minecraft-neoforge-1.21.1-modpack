package org.tukaani.xz;

import java.io.FilterInputStream;
import java.io.InputStream;

public class CloseIgnoringInputStream extends FilterInputStream {
   public CloseIgnoringInputStream(InputStream inputStream) {
      super(inputStream);
   }

   @Override
   public void close() {
   }
}
