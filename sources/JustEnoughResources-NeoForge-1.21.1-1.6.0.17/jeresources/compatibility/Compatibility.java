package jeresources.compatibility;

import jeresources.compatibility.api.JERAPI;
import jeresources.compatibility.minecraft.MinecraftCompat;
import jeresources.config.Settings;
import jeresources.json.WorldGenAdapter;
import jeresources.registry.VillagerRegistry;
import jeresources.util.LogHelper;
import jeresources.util.VillagersHelper;

public class Compatibility {
   public static void init() {
      boolean initWorldGen = true;

      try {
         if (Settings.useDIYdata && WorldGenAdapter.hasWorldGenDIYData()) {
            WorldGenAdapter.readDIYData();
            initWorldGen = false;
         }
      } catch (Exception var3) {
         LogHelper.warn("Error during loading of DIY data", var3);
      }

      try {
         new MinecraftCompat().init(initWorldGen);
      } catch (Exception var2) {
         LogHelper.warn("Error during loading of default minecraft compat", var2);
      }

      VillagersHelper.initRegistry(VillagerRegistry.getInstance());
      JERAPI.commit(initWorldGen);
   }
}
