package com.seibel.distanthorizons.core.config;

import com.seibel.distanthorizons.core.config.file.ConfigFileHandler;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.config.types.ConfigCategory;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.config.types.ConfigUIComment;
import com.seibel.distanthorizons.core.config.types.ConfigUISpacer;
import com.seibel.distanthorizons.core.config.types.ConfigUiLinkedEntry;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.config.ILangWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final IMinecraftSharedWrapper MC_SHARED = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
   private static final List<Class<?>> ACCEPTABLE_INPUTS = new ArrayList<Class<?>>() {
      {
         this.add(Boolean.class);
         this.add(Byte.class);
         this.add(Integer.class);
         this.add(Double.class);
         this.add(Short.class);
         this.add(Long.class);
         this.add(Float.class);
         this.add(String.class);
         this.add(List.class);
         this.add(ArrayList.class);
         this.add(Map.class);
         this.add(HashMap.class);
      }
   };
   public static final ConfigHandler INSTANCE = new ConfigHandler();
   public final ConfigFileHandler configFileHandler = new ConfigFileHandler(getConfigPath());
   public final List<AbstractConfigBase<?>> configBaseList = new ArrayList<>();
   public boolean isLoaded = false;
   public boolean runMinMaxValidation = true;

   public static void tryRunFirstTimeSetup() {
      if (INSTANCE.isLoaded) {
         LOGGER.debug("ConfigHandler setup already run, ignoring.");
      } else {
         INSTANCE.runFirstTimeSetup();
      }
   }

   private void runFirstTimeSetup() {
      LOGGER.info("Initialising config for [DistantHorizons]");
      this.initNestedClass(Config.class, "");
      this.configFileHandler.loadFromFile();
      this.runMinMaxValidation = !Config.Client.Advanced.Debugging.allowUnsafeValues.get();
      this.isLoaded = true;
      LOGGER.info("[DistantHorizons] Config initialised");
   }

   private static Path getConfigPath() {
      return MC_SHARED.getInstallationDirectory().toPath().resolve("config").resolve("DistantHorizons.toml");
   }

   private void initNestedClass(Class<?> configClass, String category) {
      Field[] fields = configClass.getFields();

      for (Field field : fields) {
         if (AbstractConfigBase.class.isAssignableFrom(field.getType())) {
            try {
               this.configBaseList.add((AbstractConfigBase<?>)field.get(field.getType()));
            } catch (IllegalAccessException var10) {
               LOGGER.warn("Unable to add config [" + field.getType().getName() + "], error: [" + var10.getMessage() + "].", var10);
               continue;
            }

            AbstractConfigBase<?> configBase = this.configBaseList.get(this.configBaseList.size() - 1);
            configBase.category = category;
            configBase.name = field.getName();
            if (ConfigEntry.class.isAssignableFrom(field.getType()) && !isAcceptableType(configBase.getType())) {
               LOGGER.error("Invalid variable type at [" + (category.isEmpty() ? "" : category + ".") + field.getName() + "].");
               LOGGER.error("Type [" + configBase.getType() + "] is not one of these types [" + ACCEPTABLE_INPUTS.toString() + "]");
               this.configBaseList.remove(this.configBaseList.size() - 1);
            }

            if (ConfigCategory.class.isAssignableFrom(field.getType())) {
               ConfigCategory configCategory = (ConfigCategory)configBase;
               if (configCategory.getDestination() == null) {
                  configCategory.destination = configBase.getNameAndCategory();
               }

               if (configBase.get() != null) {
                  this.initNestedClass(configCategory.get(), configCategory.getDestination());
               }
            }
         }
      }
   }

   private static boolean isAcceptableType(Class<?> inputClass) {
      return inputClass.isEnum() ? true : ACCEPTABLE_INPUTS.contains(inputClass);
   }

   public String generateLang(boolean onlyShowMissing, boolean checkEnums) {
      ILangWrapper langWrapper = SingletonInjector.INSTANCE.get(ILangWrapper.class);
      List<Class<? extends Enum<?>>> enumList = new ArrayList<>();
      String generatedLang = "";
      String starter = "  \"";
      String separator = "\":\n    \"";
      String ending = "\",\n";

      for (AbstractConfigBase<?> entry : this.configBaseList) {
         String entryPrefix = "distanthorizons.config." + entry.getNameAndCategory();
         if (checkEnums && entry.getType().isEnum() && !enumList.contains(entry.getType())) {
            enumList.add((Class<? extends Enum<?>>)entry.getType());
         }

         if (entry.getAppearance().showInGui
            && !ConfigUiLinkedEntry.class.isAssignableFrom(entry.getClass())
            && !ConfigUISpacer.class.isAssignableFrom(entry.getClass())) {
            if (ConfigUIComment.class.isAssignableFrom(entry.getClass()) && ((ConfigUIComment)entry).parentConfigPath != null) {
               entryPrefix = "distanthorizons.config." + ((ConfigUIComment)entry).parentConfigPath;
            }

            if (!langWrapper.langExists(entryPrefix) || !onlyShowMissing) {
               generatedLang = generatedLang + starter + entryPrefix + separator + langWrapper.getLang(entryPrefix) + ending;
               if (!langWrapper.langExists(entryPrefix + ".@tooltip") || !onlyShowMissing) {
                  generatedLang = generatedLang
                     + starter
                     + entryPrefix
                     + ".@tooltip"
                     + separator
                     + langWrapper.getLang(entryPrefix + ".@tooltip").replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"")
                     + ending;
               }
            }
         }
      }

      if (!enumList.isEmpty()) {
         generatedLang = generatedLang + "\n";

         for (Class<? extends Enum> anEnum : enumList) {
            for (Object enumStr : new ArrayList(EnumSet.allOf(anEnum))) {
               String enumPrefix = "distanthorizons.config.enum." + anEnum.getSimpleName() + "." + enumStr.toString();
               if (!langWrapper.langExists(enumPrefix) || !onlyShowMissing) {
                  generatedLang = generatedLang + starter + enumPrefix + separator + langWrapper.getLang(enumPrefix) + ending;
               }
            }
         }
      }

      return generatedLang;
   }
}
