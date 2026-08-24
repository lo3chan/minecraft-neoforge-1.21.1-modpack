package net.blay09.mods.inventoryessentials.data;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.inventoryessentials.InventoryEssentialsIgnores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigJsonCompatLoader {
   private static final Logger logger = LoggerFactory.getLogger(ConfigJsonCompatLoader.class);
   private static final Gson gson = new Gson();

   public static void load() {
      File configDir = new File(Balm.getConfig().getConfigDir(), "inventoryessentials/ignores");
      if (!configDir.exists() && !configDir.mkdirs()) {
         logger.error("Failed to create InventoryEssentials config directory {}", configDir);
      } else {
         File[] files = configDir.listFiles(it -> it.getName().endsWith(".json"));
         if (files != null) {
            for (File file : files) {
               try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                  IgnoredData ignoredData = (IgnoredData)gson.fromJson(reader, IgnoredData.class);
                  if (ignoredData != null) {
                     InventoryEssentialsIgnores.addIgnoredData(ignoredData);
                  }
               } catch (IOException var11) {
                  logger.error("Failed to load InventoryEssentials file {}", file, var11);
               }
            }
         }
      }
   }
}
