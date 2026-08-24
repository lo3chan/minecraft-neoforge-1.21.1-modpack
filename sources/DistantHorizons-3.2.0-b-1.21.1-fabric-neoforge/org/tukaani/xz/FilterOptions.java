package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class FilterOptions implements Cloneable {
   public static int getEncoderMemoryUsage(FilterOptions[] filterOptionss) {
      int var1 = 0;

      for (int var2 = 0; var2 < filterOptionss.length; var2++) {
         var1 += filterOptionss[var2].getEncoderMemoryUsage();
      }

      return var1;
   }

   public static int getDecoderMemoryUsage(FilterOptions[] filterOptionss) {
      int var1 = 0;

      for (int var2 = 0; var2 < filterOptionss.length; var2++) {
         var1 += filterOptionss[var2].getDecoderMemoryUsage();
      }

      return var1;
   }

   public abstract int getEncoderMemoryUsage();

   public FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream) {
      return this.getOutputStream(finishableOutputStream, ArrayCache.getDefaultCache());
   }

   public abstract FinishableOutputStream getOutputStream(FinishableOutputStream finishableOutputStream, ArrayCache arrayCache);

   public abstract int getDecoderMemoryUsage();

   public InputStream getInputStream(InputStream inputStream) throws IOException {
      return this.getInputStream(inputStream, ArrayCache.getDefaultCache());
   }

   public abstract InputStream getInputStream(InputStream inputStream, ArrayCache arrayCache) throws IOException;

   abstract FilterEncoder getFilterEncoder();

   FilterOptions() {
   }
}
