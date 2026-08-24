package com.alonie.recipebookispain_extended.neoforge;

import com.alonie.recipebookispain_extended.PlatformAbstractions;
import java.nio.file.Path;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

public class NeoForgePlatform implements PlatformAbstractions {
   @Override
   public boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   @Override
   public Path getConfigDir() {
      return FMLPaths.CONFIGDIR.get();
   }
}
