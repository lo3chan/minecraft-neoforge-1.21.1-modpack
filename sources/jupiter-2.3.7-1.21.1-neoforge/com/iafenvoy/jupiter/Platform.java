package com.iafenvoy.jupiter;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

public final class Platform {
   public static String resolveModName(String id) {
      return ModList.get().getModContainerById(id).map(ModContainer::getModInfo).<String>map(IModInfo::getDisplayName).orElse("%ERROR%");
   }

   public static boolean isModLoaded(String id) {
      return ModList.get().isLoaded(id);
   }
}
