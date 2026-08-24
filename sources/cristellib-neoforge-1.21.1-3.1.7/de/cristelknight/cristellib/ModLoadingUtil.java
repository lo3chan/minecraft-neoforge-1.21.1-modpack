package de.cristelknight.cristellib;

import de.cristelknight.cristellib.neoforge.ModLoadingUtilImpl;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import java.util.List;
import java.util.Optional;

public class ModLoadingUtil {
   @ExpectPlatform
   @Transformed
   public static List<String> getModIds() {
      return ModLoadingUtilImpl.getModIds();
   }

   @ExpectPlatform
   @Transformed
   public static boolean isModLoaded(String modId) {
      return ModLoadingUtilImpl.isModLoaded(modId);
   }

   @ExpectPlatform
   @Transformed
   public static Optional<Integer> compare(String modId, String version) {
      return ModLoadingUtilImpl.compare(modId, version);
   }
}
