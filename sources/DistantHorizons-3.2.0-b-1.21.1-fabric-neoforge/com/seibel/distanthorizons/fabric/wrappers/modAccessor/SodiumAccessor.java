package com.seibel.distanthorizons.fabric.wrappers.modAccessor;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.ISodiumAccessor;

public class SodiumAccessor implements ISodiumAccessor {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public static final boolean isSodiumV5OrLess = !classPresent("net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer");

   @Override
   public String getModName() {
      return "Sodium-Fabric";
   }

   private static boolean classPresent(String className) {
      try {
         Class.forName(className);
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }
}
