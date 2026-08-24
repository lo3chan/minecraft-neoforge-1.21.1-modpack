package net.blay09.mods.inventoryessentials.data;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.inventoryessentials.InventoryEssentialsExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModFileJsonExtensionLoader {
   private static final Logger logger = LoggerFactory.getLogger(ModFileJsonExtensionLoader.class);
   private static final Gson gson = new Gson();

   public static void load() {
      Map<String, Path> modPaths = Balm.lookupAllModPaths("inventoryessentials/extensions");
      modPaths.forEach((key, value) -> {
         try (Stream<Path> walker = Files.walk(value)) {
            walker.forEach(file -> {
               if (file.toString().endsWith(".json")) {
                  try (BufferedReader reader = Files.newBufferedReader(file)) {
                     ExtensionData extensionData = (ExtensionData)gson.fromJson(reader, ExtensionData.class);
                     if (extensionData != null) {
                        InventoryEssentialsExtensions.addExtensionData(extensionData);
                     }
                  } catch (IOException var6) {
                     logger.error("Failed to load InventoryEssentials extension file {}", file, var6);
                  }
               }
            });
         } catch (IOException var7) {
            logger.error("Failed to load InventoryEssentials extension files from mod {}", key, var7);
         }
      });
   }
}
