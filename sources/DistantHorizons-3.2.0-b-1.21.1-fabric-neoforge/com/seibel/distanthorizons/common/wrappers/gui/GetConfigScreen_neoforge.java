package com.seibel.distanthorizons.common.wrappers.gui;

import com.seibel.distanthorizons.common.wrappers.gui.classicConfig.ClassicConfigGUI_neoforge;
import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.coreapi.ModInfo;
import net.minecraft.client.gui.screens.Screen;

public class GetConfigScreen_neoforge {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static Screen getScreen(Screen parent) {
      if (ModInfo.IS_DEV_BUILD) {
         String missingLangEntries = ConfigHandler.INSTANCE.generateLang(true, true);
         String trimmedMissingEntries = missingLangEntries.trim();
         if (!trimmedMissingEntries.isEmpty()) {
            LOGGER.warn("One or more language entries is missing:");
            LOGGER.warn(missingLangEntries);
         }
      }

      return ClassicConfigGUI_neoforge.getScreen(parent, "client");
   }
}
