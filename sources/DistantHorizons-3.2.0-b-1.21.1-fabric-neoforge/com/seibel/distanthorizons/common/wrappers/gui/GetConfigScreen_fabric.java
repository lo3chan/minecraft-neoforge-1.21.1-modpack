package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.common.wrappers.gui.classicConfig.ClassicConfigGUI_fabric;
import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.ModInfo;
import net.minecraft.class_437;

public class GetConfigScreen_fabric {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static class_437 getScreen(class_437 parent) {
      if (ModInfo.IS_DEV_BUILD) {
         String missingLangEntries = ConfigHandler.INSTANCE.generateLang(true, true);
         String trimmedMissingEntries = missingLangEntries.trim();
         if (!trimmedMissingEntries.isEmpty()) {
            LOGGER.warn("One or more language entries is missing:");
            LOGGER.warn(missingLangEntries);
         }
      }

      return ClassicConfigGUI_fabric.getScreen(parent, "client");
   }
}
