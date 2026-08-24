package net.blay09.mods.inventoryessentials.data;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.inventoryessentials.InventoryEssentialsExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigJsonExtensionLoader {
   private static final Logger logger = LoggerFactory.getLogger(ConfigJsonExtensionLoader.class);
   private static final Gson gson = new Gson();

   public static void load() {
      File configDir = new File(Balm.getConfig().getConfigDir(), "inventoryessentials/extensions");
      if (!configDir.exists() && !configDir.mkdirs()) {
         logger.error("Failed to create InventoryEssentials config directory {}", configDir);
      } else {
         File[] files = configDir.listFiles(it -> it.getName().endsWith(".json"));
         if (files != null) {
            for (File file : files) {
               try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                  ExtensionData extensionData = (ExtensionData)gson.fromJson(reader, ExtensionData.class);
                  if (extensionData != null) {
                     InventoryEssentialsExtensions.addExtensionData(extensionData);
                  }
               } catch (IOException var11) {
                  logger.error("Failed to load InventoryEssentials extension file {}", file, var11);
               }
            }
         }
      }
   }
}
