package pl.skidam.automodpack_core.utils.launchers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Map;
import pl.skidam.automodpack_core.GlobalVariables;

public class MultiMCMeta {
   public static final Path MMC_PACK_PATH = Path.of("../mmc-pack.json");
   private static final Map<String, String> LOADER_UID_MAP = Map.of(
      "fabric", "net.fabricmc.fabric-loader", "quilt", "org.quiltmc.quilt-loader", "forge", "net.minecraftforge", "neoforge", "net.neoforged"
   );

   public static boolean updateLoaderVersion(String loaderType, String newVersion) {
      String targetUid = LOADER_UID_MAP.get(loaderType.toLowerCase());
      return targetUid == null ? false : LauncherVersionSwapper.modifyJson(MMC_PACK_PATH, json -> {
         if (json.has("formatVersion") && json.get("formatVersion").getAsInt() == 1) {
            JsonArray components = json.getAsJsonArray("components");
            if (components == null) {
               return false;
            } else {
               boolean changed = false;

               for (JsonElement element : components) {
                  JsonObject component = element.getAsJsonObject();
                  if (component.has("uid") && component.get("uid").getAsString().equals(targetUid)) {
                     String currentVersion = component.has("version") ? component.get("version").getAsString() : null;
                     if (currentVersion != null && !newVersion.equals(currentVersion)) {
                        component.addProperty("version", newVersion);
                        if (component.has("cachedVersion")) {
                           component.addProperty("cachedVersion", newVersion);
                        }

                        changed = true;
                     }
                  }
               }

               if (changed) {
                  try {
                     GlobalVariables.LOGGER.info("Simulating a 5 sec delay to avoid MultiMC/Prism overwrite issue...");
                     Thread.sleep(5000L);
                  } catch (InterruptedException var9) {
                     GlobalVariables.LOGGER.error("Interrupted while simulating delay", var9);
                  }

                  json.add("components", components);
                  GlobalVariables.LOGGER.info("MultiMC/Prism: Updated loader version to {}", newVersion);
               }

               return changed;
            }
         } else {
            return false;
         }
      });
   }
}
