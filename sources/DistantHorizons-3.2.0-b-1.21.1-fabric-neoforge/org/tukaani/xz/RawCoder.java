package org.tukaani.xz;

class RawCoder {
   static void validate(FilterCoder[] filterCoders) throws UnsupportedOptionsException {
      for (int var1 = 0; var1 < filterCoders.length - 1; var1++) {
         if (!filterCoders[var1].nonLastOK()) {
            throw new UnsupportedOptionsException("Unsupported XZ filter chain");
         }
      }

      if (!filterCoders[filterCoders.length - 1].lastOK()) {
         throw new UnsupportedOptionsException("Unsupported XZ filter chain");
      } else {
         int var3 = 0;

         for (int var2 = 0; var2 < filterCoders.length; var2++) {
            if (filterCoders[var2].changesSize()) {
               var3++;
            }
         }

         if (var3 > 3) {
            throw new UnsupportedOptionsException("Unsupported XZ filter chain");
         }
      }
   }
}
