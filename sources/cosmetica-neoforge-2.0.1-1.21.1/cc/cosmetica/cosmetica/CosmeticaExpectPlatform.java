package cc.cosmetica.cosmetica;

import cc.cosmetica.cosmetica.neoforge.CosmeticaExpectPlatformImpl;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;

public class CosmeticaExpectPlatform {
   @ExpectPlatform
   @Transformed
   public static boolean isModLoaded(String mod) {
      return CosmeticaExpectPlatformImpl.isModLoaded(mod);
   }
}
