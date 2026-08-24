package net.conczin.immersive_gateways.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonConfig {
   public static final Logger LOGGER = LogManager.getLogger();
   public int version = 0;
   public final String name;

   int getVersion() {
      return 1;
   }

   public JsonConfig(String name) {
      this.name = name;
   }

   public static File getConfigFile(String id) {
      return new File("./config/" + id + ".json");
   }

   public void save() {
      try (FileWriter writer = new FileWriter(getConfigFile(this.name))) {
         this.version = this.getVersion();
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         gson.toJson(this, writer);
      } catch (IOException var6) {
         LOGGER.error(var6);
      }
   }

   public static <T extends JsonConfig> T loadOrCreate(T defaultConfig, Class<T> jsonClass) {
      String name = defaultConfig.name;
      if (getConfigFile(name).exists()) {
         try {
            JsonConfig var6;
            try (FileReader reader = new FileReader(getConfigFile(name))) {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               T config = (T)gson.fromJson(reader, jsonClass);
               if (config.version != config.getVersion()) {
                  config = defaultConfig;
               }

               config.save();
               var6 = config;
            }

            return (T)var6;
         } catch (Exception var9) {
            LOGGER.error("Failed to load config for '{}'! Default config is used for now. Delete the file to reset.", name);
            LOGGER.error(var9);
            return defaultConfig;
         }
      } else {
         defaultConfig.save();
         return defaultConfig;
      }
   }
}
