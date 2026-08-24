package com.seibel.distanthorizons.core.jar.wrapperInterfaces.config;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.json.JsonFormat;
import com.seibel.distanthorizons.core.jar.JarUtils;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.config.ILangWrapper;
import java.util.Locale;

public class LangWrapper implements ILangWrapper {
   public static final LangWrapper INSTANCE = new LangWrapper();
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final Config JSON_OBJECT = Config.inMemory();

   public static void init() {
      try {
         JsonFormat.fancyInstance()
            .createParser()
            .parse(
               JarUtils.convertInputStreamToString(JarUtils.accessFile("assets/lod/lang/" + Locale.getDefault().toString().toLowerCase() + ".json")),
               JSON_OBJECT,
               ParsingMode.REPLACE
            );
      } catch (Exception var1) {
         LOGGER.error("Failed to read lang file, error: [" + var1.getMessage() + "]", var1);
      }
   }

   @Override
   public boolean langExists(String str) {
      return JSON_OBJECT.get(str) != null;
   }

   @Override
   public String getLang(String str) {
      return JSON_OBJECT.get(str) != null ? JSON_OBJECT.get(str) : str;
   }
}
