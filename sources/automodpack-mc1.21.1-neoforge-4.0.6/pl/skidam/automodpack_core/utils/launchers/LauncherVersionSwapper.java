package pl.skidam.automodpack_core.utils.launchers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import pl.skidam.automodpack_core.GlobalVariables;

public class LauncherVersionSwapper {
   private static final Gson GSON = new GsonBuilder().create();

   public static boolean swapLoaderVersion(String serverLoaderType, String serverLoaderVersion) {
      if (!GlobalVariables.clientConfig.syncLoaderVersion) {
         return false;
      } else if (serverLoaderType == null || serverLoaderVersion == null) {
         return false;
      } else if (!serverLoaderType.equalsIgnoreCase(GlobalVariables.LOADER)) {
         return false;
      } else {
         boolean updated = false;
         if (MultiMCMeta.updateLoaderVersion(serverLoaderType, serverLoaderVersion)) {
            updated = true;
         }

         if (PandoraMeta.updateLoaderVersion(serverLoaderVersion)) {
            updated = true;
         }

         return updated;
      }
   }

   public static boolean modifyJson(Path path, Predicate<JsonObject> modifier) {
      if (!Files.exists(path)) {
         return false;
      } else {
         try {
            String content = Files.readString(path);
            JsonObject json = (JsonObject)GSON.fromJson(content, JsonObject.class);
            if (json == null) {
               return false;
            }

            if (modifier.test(json)) {
               Files.writeString(path, GSON.toJson(json));
               return true;
            }
         } catch (Exception var4) {
            GlobalVariables.LOGGER.error("Failed to update launcher metadata at: {}", path, var4);
         }

         return false;
      }
   }
}
