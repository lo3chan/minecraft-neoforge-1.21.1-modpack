package traben.entity_texture_features.config;

import java.util.HashSet;
import java.util.Set;

public abstract class ETFConfigWarnings {
   private static final Set<ETFConfigWarning> REGISTERED_WARNINGS = new HashSet<>();

   public static void registerConfigWarning(ETFConfigWarning... warnings) {
      for (ETFConfigWarning warn : warnings) {
         if (warn != null) {
            REGISTERED_WARNINGS.add(warn);
         }
      }
   }

   public static Set<ETFConfigWarning> getRegisteredWarnings() {
      return REGISTERED_WARNINGS;
   }
}
