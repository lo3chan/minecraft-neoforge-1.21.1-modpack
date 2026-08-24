package cc.cosmetica.cosmetica.neoforge;

import net.neoforged.fml.ModList;

public class CosmeticaExpectPlatformImpl {
   public static boolean isModLoaded(String mod) {
      return ModList.get().isLoaded(mod);
   }
}
