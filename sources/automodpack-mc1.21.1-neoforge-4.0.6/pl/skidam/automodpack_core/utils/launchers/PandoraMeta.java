package pl.skidam.automodpack_core.utils.launchers;

import java.nio.file.Path;
import pl.skidam.automodpack_core.GlobalVariables;

public class PandoraMeta {
   public static final Path INFO_JSON_PATH = Path.of("../info_v1.json");

   public static boolean updateLoaderVersion(String newVersion) {
      return LauncherVersionSwapper.modifyJson(INFO_JSON_PATH, json -> {
         String currentVersion = json.has("preferred_loader_version") ? json.get("preferred_loader_version").getAsString() : null;
         if (currentVersion == null) {
            return false;
         } else if (!newVersion.equals(currentVersion)) {
            json.addProperty("preferred_loader_version", newVersion);
            GlobalVariables.LOGGER.info("Pandora: Updated loader version to {}", newVersion);
            return true;
         } else {
            return false;
         }
      });
   }
}
