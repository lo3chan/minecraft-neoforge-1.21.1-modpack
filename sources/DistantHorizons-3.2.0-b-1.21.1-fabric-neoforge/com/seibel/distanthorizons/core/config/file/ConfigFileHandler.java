package com.seibel.distanthorizons.core.config.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigFileHandler {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public final Path configPath;
   private final CommentedFileConfig nightConfig;
   private final ReentrantLock readWriteLock = new ReentrantLock();

   public ConfigFileHandler(Path configPath) {
      this.configPath = configPath;
      this.nightConfig = CommentedFileConfig.builder(this.configPath.toFile()).sync().build();
   }

   public void saveToFile() {
      this.saveToFile(this.nightConfig);
   }

   public void saveToFile(CommentedFileConfig nightConfig) {
      try {
         this.readWriteLock.lock();
         if (!Files.exists(this.configPath)) {
            reCreateFile(this.configPath);
         }

         this.loadNightConfig(nightConfig);

         for (AbstractConfigBase<?> entry : ConfigHandler.INSTANCE.configBaseList) {
            if (ConfigEntry.class.isAssignableFrom(entry.getClass())) {
               this.createComment((ConfigEntry<?>)entry, nightConfig);
               this.saveEntry((ConfigEntry<?>)entry, nightConfig);
            }
         }

         try {
            nightConfig.save();
         } catch (Exception var7) {
            SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class).crashMinecraft("Failed to save config at [" + this.configPath + "]", var7);
         }
      } finally {
         this.readWriteLock.unlock();
      }
   }

   public void loadFromFile() {
      try {
         this.readWriteLock.lock();
         int currentCfgVersion = 4;

         try {
            CommentedFileConfig tmpNightConfig = CommentedFileConfig.builder(this.configPath.toFile()).build();
            tmpNightConfig.load();
            currentCfgVersion = tmpNightConfig.<Integer>get("_version");
            tmpNightConfig.close();
         } catch (Exception var8) {
         }

         if (currentCfgVersion != 4) {
            if (currentCfgVersion > 4) {
               LOGGER.warn(
                  "Found config version ["
                     + currentCfgVersion
                     + "] which is newer than current mods config version of ["
                     + 4
                     + "]. You may have downgraded the mod and items may have been moved, you have been warned"
               );
            } else {
               LOGGER.warn("DistantHorizons config is of an older version, currently there is no config updater... so resetting config");

               try {
                  Files.delete(this.configPath);
               } catch (Exception var7) {
                  LOGGER.error("Unable to delete outdated config file at: [" + this.configPath + "], error: [" + var7.getMessage() + "].", var7);
               }
            }
         }

         this.loadFromFile(this.nightConfig);
         this.nightConfig.set("_version", 4);
      } finally {
         this.readWriteLock.unlock();
      }
   }

   private void loadFromFile(CommentedFileConfig nightConfig) {
      if (Files.exists(this.configPath)) {
         this.loadNightConfig(nightConfig);
      } else {
         reCreateFile(this.configPath);
      }

      for (AbstractConfigBase<?> entry : ConfigHandler.INSTANCE.configBaseList) {
         if (ConfigEntry.class.isAssignableFrom(entry.getClass()) && entry.getAppearance().showInFile) {
            this.createComment((ConfigEntry<?>)entry, nightConfig);
            this.loadEntry((ConfigEntry<?>)entry, nightConfig);
         }
      }

      try {
         nightConfig.save();
      } catch (Exception var4) {
         SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class).crashMinecraft("Failed to save config at [" + this.configPath + "]", var4);
      }
   }

   public void saveEntry(ConfigEntry<?> entry) {
      this.saveEntry(entry, this.nightConfig);
      this.nightConfig.save();
   }

   public void saveEntry(ConfigEntry<?> entry, CommentedFileConfig workConfig) {
      if (entry.getAppearance().showInFile) {
         if (entry.getTrueValue() == null) {
            throw new IllegalArgumentException("ConfigEntry [" + entry.getNameAndCategory() + "] is null, how did this happen?");
         } else {
            workConfig.set(entry.getNameAndCategory(), ConfigTypeConverters.attemptToConvertToString(entry.getType(), entry.getTrueValue()));
         }
      }
   }

   public void loadEntry(ConfigEntry<?> entry) {
      this.loadEntry(entry, this.nightConfig);
   }

   public <T> void loadEntry(ConfigEntry<T> entry, CommentedFileConfig nightConfig) {
      if (entry.getAppearance().showInFile) {
         if (!nightConfig.contains(entry.getNameAndCategory())) {
            this.saveEntry(entry, nightConfig);
         } else {
            try {
               if (entry.getType().isEnum()) {
                  entry.setWithoutFiringEvents(nightConfig.getEnum(entry.getNameAndCategory(), (Class<T>)entry.getType()));
                  return;
               }

               Class<?> expectedValueClass = entry.getType();
               Object value = nightConfig.get(entry.getNameAndCategory());
               Object convertedValue = ConfigTypeConverters.attemptToConvertFromString(expectedValueClass, value);
               if (!convertedValue.getClass().equals(expectedValueClass)) {
                  LOGGER.error(
                     "Unable to convert config value ["
                        + value
                        + "] from ["
                        + (value != null ? value.getClass() : "NULL")
                        + "] to ["
                        + expectedValueClass
                        + "] for config ["
                        + entry.name
                        + "], the default config value will be used instead ["
                        + entry.getDefaultValue()
                        + "]. Make sure a converter is defined in ["
                        + ConfigTypeConverters.class.getSimpleName()
                        + "]."
                  );
                  convertedValue = entry.getDefaultValue();
               }

               entry.setWithoutFiringEvents((T)convertedValue);
               if (entry.getTrueValue() == null) {
                  LOGGER.warn("BlockBiomeWrapperPair [" + entry.getNameAndCategory() + "] returned as null from the config. Using default value.");
                  entry.setWithoutFiringEvents(entry.getDefaultValue());
               }
            } catch (Exception var6) {
               LOGGER.warn("BlockBiomeWrapperPair [" + entry.getNameAndCategory() + "] had an invalid value when loading the config. Using default value.");
               entry.setWithoutFiringEvents(entry.getDefaultValue());
            }
         }
      }
   }

   public void createComment(ConfigEntry<?> entry) {
      this.createComment(entry, this.nightConfig);
   }

   public void createComment(ConfigEntry<?> entry, CommentedFileConfig nightConfig) {
      if (entry.getAppearance().showInFile && entry.getComment() != null) {
         String comment = entry.getComment().replaceAll("\n", "\n ").trim();
         comment = "\n " + comment;
         nightConfig.setComment(entry.getNameAndCategory(), comment);
      }
   }

   public void loadNightConfig() {
      this.loadNightConfig(this.nightConfig);
   }

   public void loadNightConfig(CommentedFileConfig nightConfig) {
      try {
         try {
            if (!Files.exists(this.configPath)) {
               Files.createFile(this.configPath);
            }

            nightConfig.load();
         } catch (Exception var4) {
            LOGGER.warn("Loading file failed because of this expectation:\n" + var4);
            reCreateFile(this.configPath);
            nightConfig.load();
         }
      } catch (Exception var5) {
         LOGGER.error("File creation failed at [" + this.configPath + "], error: [" + var5.getMessage() + "].", var5);
         IMinecraftClientWrapper mc = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
         mc.crashMinecraft(
            "Loading file and resetting config file failed at path [" + this.configPath + "]. Please check the file is ok and you have the permissions", var5
         );
      }
   }

   public static void reCreateFile(Path path) {
      try {
         Files.deleteIfExists(path);
         if (!path.getParent().toFile().exists()) {
            Files.createDirectory(path.getParent());
         }

         Files.createFile(path);
      } catch (IOException var2) {
         LOGGER.error("Unable to recreate config file, error: [" + var2.getMessage() + "].", var2);
      }
   }
}
