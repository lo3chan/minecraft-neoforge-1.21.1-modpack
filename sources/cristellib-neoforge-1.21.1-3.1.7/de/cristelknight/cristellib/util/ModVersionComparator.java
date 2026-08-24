package de.cristelknight.cristellib.util;

import de.cristelknight.cristellib.ModLoadingUtil;

public enum ModVersionComparator {
   GREATER_EQUAL(">=") {
      @Override
      public boolean test(String modId, String version) {
         return ModLoadingUtil.compare(modId, version).map(compareResult -> compareResult >= 0).orElse(false);
      }
   },
   LESS_EQUAL("<=") {
      @Override
      public boolean test(String modId, String version) {
         return ModLoadingUtil.compare(modId, version).map(compareResult -> compareResult <= 0).orElse(false);
      }
   },
   GREATER(">") {
      @Override
      public boolean test(String modId, String version) {
         return ModLoadingUtil.compare(modId, version).map(compareResult -> compareResult > 0).orElse(false);
      }
   },
   LESS("<") {
      @Override
      public boolean test(String modId, String version) {
         return ModLoadingUtil.compare(modId, version).map(compareResult -> compareResult < 0).orElse(false);
      }
   },
   EQUAL("=") {
      @Override
      public boolean test(String modId, String version) {
         return ModLoadingUtil.compare(modId, version).map(compareResult -> compareResult == 0).orElse(false);
      }
   };

   private final String serialized;

   private ModVersionComparator(String serialized) {
      this.serialized = serialized;
   }

   public final String getSerialized() {
      return this.serialized;
   }

   public abstract boolean test(String var1, String var2);
}
