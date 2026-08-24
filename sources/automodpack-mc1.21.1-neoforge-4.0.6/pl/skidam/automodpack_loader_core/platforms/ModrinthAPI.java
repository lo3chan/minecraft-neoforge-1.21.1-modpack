package pl.skidam.automodpack_loader_core.platforms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.utils.Json;

public record ModrinthAPI(
   String modrinthID, String requestUrl, String downloadUrl, String fileVersion, String fileName, long fileSize, String releaseType, String SHA1Hash
) {
   private static final String BASE_URL = "https://api.modrinth.com/v2";

   public static List<ModrinthAPI> getModInfosFromID(String modrinthID) {
      if (modrinthID == null) {
         return null;
      } else if (modrinthID.isBlank()) {
         return null;
      } else {
         String modLoader = GlobalVariables.LOADER_MANAGER.getPlatformType().toString().toLowerCase();
         String requestUrl = "https://api.modrinth.com/v2/project/"
            + modrinthID
            + "/version?loaders=[\""
            + modLoader
            + "\"]&game_versions=[\""
            + GlobalVariables.MC_VERSION
            + "\"]";
         requestUrl = requestUrl.replaceAll("\"", "%22");
         List<ModrinthAPI> modrinthAPIList = new ArrayList<>();

         try {
            JsonArray JSONArray = Json.fromUrlAsArray(requestUrl);
            if (JSONArray == null) {
               GlobalVariables.LOGGER.warn("Can't find mod for your client, tried link " + requestUrl);
               return null;
            }

            for (JsonElement jsonElement : JSONArray) {
               JsonObject JSONObject = jsonElement.getAsJsonObject();
               String fileVersion = JSONObject.get("version_number").getAsString();
               String releaseType = JSONObject.get("version_type").getAsString();
               JsonObject JSONObjectFiles = JSONObject.getAsJsonArray("files").get(0).getAsJsonObject();
               String downloadUrl = JSONObjectFiles.get("url").getAsString();
               String fileName = JSONObjectFiles.get("filename").getAsString();
               long fileSize = JSONObjectFiles.get("size").getAsLong();
               String SHA1Hash = JSONObjectFiles.get("hashes").getAsJsonObject().get("sha1").getAsString();
               modrinthAPIList.add(new ModrinthAPI(modrinthID, requestUrl, downloadUrl, fileVersion, fileName, fileSize, releaseType, SHA1Hash));
            }
         } catch (IndexOutOfBoundsException var16) {
            GlobalVariables.LOGGER.warn("Can't find mod for your client, tried link " + requestUrl);
         } catch (Exception var17) {
            var17.printStackTrace();
         }

         return modrinthAPIList;
      }
   }

   public static ModrinthAPI getModSpecificVersion(String modrinthID, String modVersion, String mcVersion) {
      if (modrinthID == null || modVersion == null || mcVersion == null) {
         return null;
      } else if (!modrinthID.isBlank() && !modVersion.isBlank() && !mcVersion.isBlank()) {
         String modLoader = GlobalVariables.LOADER_MANAGER.getPlatformType().toString().toLowerCase();
         String requestUrl = "https://api.modrinth.com/v2/project/"
            + modrinthID
            + "/version?loaders=[\""
            + modLoader
            + "\"]&game_versions=[\""
            + mcVersion
            + "\"]";
         requestUrl = requestUrl.replaceAll("\"", "%22");

         try {
            JsonArray JSONArray = Json.fromUrlAsArray(requestUrl);
            if (JSONArray == null) {
               GlobalVariables.LOGGER.warn("Can't find mod for your client, tried link " + requestUrl);
               return null;
            }

            for (JsonElement jsonElement : JSONArray) {
               JsonObject JSONObject = jsonElement.getAsJsonObject();
               String fileVersion = JSONObject.get("version_number").getAsString();
               if (fileVersion.equals(modVersion)) {
                  String releaseType = JSONObject.get("version_type").getAsString();
                  JsonObject JSONObjectFiles = JSONObject.getAsJsonArray("files").get(0).getAsJsonObject();
                  String downloadUrl = JSONObjectFiles.get("url").getAsString();
                  String fileName = JSONObjectFiles.get("filename").getAsString();
                  long fileSize = JSONObjectFiles.get("size").getAsLong();
                  String SHA1Hash = JSONObjectFiles.get("hashes").getAsJsonObject().get("sha1").getAsString();
                  return new ModrinthAPI(modrinthID, requestUrl, downloadUrl, fileVersion, fileName, fileSize, releaseType, SHA1Hash);
               }
            }
         } catch (Exception var17) {
            var17.printStackTrace();
         }

         return null;
      } else {
         return null;
      }
   }

   public static List<ModrinthAPI> getModsInfosFromListOfSHA1(List<String> listOfSha1) {
      if (listOfSha1 != null && !listOfSha1.isEmpty()) {
         String requestUrl = "https://api.modrinth.com/v2/version_files";
         List<ModrinthAPI> modrinthAPIList = new LinkedList<>();

         try {
            JsonObject JSONObjects = Json.fromModrinthUrl(requestUrl, listOfSha1);

            for (String key : JSONObjects.keySet()) {
               JsonObject JSONObject = JSONObjects.getAsJsonObject(key);
               ModrinthAPI modrinthAPI = parseJsonObject(JSONObject, listOfSha1);
               if (modrinthAPI != null) {
                  modrinthAPIList.add(modrinthAPI);
               }
            }
         } catch (Exception var8) {
            GlobalVariables.LOGGER.error("Failed to fetch data from Modrinth API", var8);
         }

         return modrinthAPIList;
      } else {
         return null;
      }
   }

   private static ModrinthAPI parseJsonObject(JsonObject JSONObject, List<String> listOfSha1) {
      if (JSONObject == null) {
         return null;
      } else {
         String modrinthID = JSONObject.get("project_id").getAsString();
         String fileVersion = JSONObject.get("version_number").getAsString();
         String releaseType = JSONObject.get("version_type").getAsString();
         JsonArray filesArray = JSONObject.getAsJsonArray("files");
         JsonObject JSONObjectFile = null;
         String sha1 = listOfSha1.size() == 1 ? listOfSha1.get(0) : null;

         for (JsonElement fileElement : filesArray) {
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonObject hashesObject = fileObject.getAsJsonObject("hashes");
            String sha1Hash = hashesObject.get("sha1").getAsString();
            if (sha1 != null && sha1.equals(sha1Hash)) {
               JSONObjectFile = fileObject;
               break;
            }

            if (listOfSha1.contains(sha1Hash)) {
               JSONObjectFile = fileObject;
               break;
            }
         }

         if (JSONObjectFile == null) {
            if (sha1 != null) {
               GlobalVariables.LOGGER.error("Can't find file with SHA1 hash: " + sha1);
            }

            return null;
         } else {
            String downloadUrl = JSONObjectFile.get("url").getAsString();
            String fileName = JSONObjectFile.get("filename").getAsString();
            long fileSize = JSONObjectFile.get("size").getAsLong();
            if (sha1 == null) {
               sha1 = JSONObjectFile.get("hashes").getAsJsonObject().get("sha1").getAsString();
            }

            return new ModrinthAPI(modrinthID, null, downloadUrl, fileVersion, fileName, fileSize, releaseType, sha1);
         }
      }
   }

   public static String getMainPageUrl(String modrinthID, String fileType) {
      return "https://modrinth.com/" + fileType + "/" + modrinthID;
   }
}
