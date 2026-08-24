package pl.skidam.automodpack_loader_core.platforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.utils.Json;

public record CurseForgeAPI(
   String requestUrl, String downloadUrl, String fileVersion, String fileName, String fileSize, String releaseType, String murmurHash, String sha1Hash
) {
   public static final String BASE_URL = "https://api.curseforge.com/v1";
   public static final String CDN_HOST = "edge.forgecdn.net";

   public static List<CurseForgeAPI> getModInfosFromFingerPrints(Map<String, String> hashes) {
      if (hashes != null && !hashes.isEmpty()) {
         String requestUrl = "https://api.curseforge.com/v1/fingerprints";
         List<CurseForgeAPI> curseForgeAPIList = new LinkedList<>();

         try {
            for (JsonElement match : Json.fromCurseForgeUrl(requestUrl, hashes.values().stream().toList())
               .get("data")
               .getAsJsonObject()
               .get("exactMatches")
               .getAsJsonArray()) {
               JsonObject JSONObject = match.getAsJsonObject();
               CurseForgeAPI curseForgeAPI = parseJsonObject(JSONObject, hashes);
               if (curseForgeAPI != null) {
                  curseForgeAPIList.add(curseForgeAPI);
               }
            }
         } catch (Exception var8) {
            GlobalVariables.LOGGER.error("Failed to fetch data from CurseForge API", var8);
         }

         return curseForgeAPIList;
      } else {
         return null;
      }
   }

   private static CurseForgeAPI parseJsonObject(JsonObject JSONObject, Map<String, String> hashes) {
      if (JSONObject == null) {
         GlobalVariables.LOGGER.error("CurseForgeAPI Can't parse null object");
         return null;
      } else {
         JsonObject fileJson = JSONObject.get("file").getAsJsonObject();
         int releaseTypeInt = fileJson.get("releaseType").getAsInt();

         String releaseType = switch (releaseTypeInt) {
            case 1 -> "release";
            case 2 -> "beta";
            case 3 -> "alpha";
            default -> null;
         };
         JsonArray fileHashes = fileJson.getAsJsonArray("hashes");
         String sha1 = null;
         boolean found = false;

         for (JsonElement hashElement : fileHashes) {
            JsonObject hashObject = hashElement.getAsJsonObject();
            if (hashObject.get("algo").getAsInt() == 1) {
               String hash = hashObject.get("value").getAsString();
               if (hashes.containsKey(hash)) {
                  sha1 = hash;
                  found = true;
                  break;
               }
            }
         }

         if (!found) {
            GlobalVariables.LOGGER.error("CurseForgeAPI Can't find file with SHA1 hash: {}", sha1);
            return null;
         } else {
            String downloadUrl = fileJson.get("downloadUrl").isJsonNull() ? null : fileJson.get("downloadUrl").getAsString();
            String fileName = fileJson.get("fileName").getAsString();
            String fileVersion = fileJson.get("displayName").getAsString();
            String fileSize = String.valueOf(fileJson.get("fileLength").getAsLong());
            String murmur = hashes.get(sha1);
            return new CurseForgeAPI(null, downloadUrl, fileVersion, fileName, fileSize, releaseType, murmur, sha1);
         }
      }
   }
}
