package de.markusbordihn.modsoptimizer.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.markusbordihn.modsoptimizer.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ModsDatabaseUpdater {
   private static final String LOG_PREFIX = "[Mods Database Updater]";
   private static final Path DEFAULT_CONFIG_DIR = Paths.get("").toAbsolutePath().resolve("config").resolve("mods_optimizer");
   private static final String REMOTE_URL = "https://raw.githubusercontent.com/MarkusBordihn/BOs-Mods-Optimizer/1.18.2/Common/src/main/resources/mods-database.json";
   private static final int CACHE_MAX_AGE_HOURS = 48;
   private static final String MODS_DATABASE_FILE = "mods-database.json";
   private static final String KEY_CLIENT = "client";
   private static final String KEY_SERVER = "server";
   private static final String KEY_BOTH = "both";
   private static final Set<String> VALID_KEYS = Set.of("client", "server", "both");
   private static Path configDir = DEFAULT_CONFIG_DIR;

   public static Path getConfigDir() {
      return configDir;
   }

   public static void setConfigDir(Path customConfigDir) {
      configDir = customConfigDir != null ? customConfigDir : DEFAULT_CONFIG_DIR;
   }

   public static void resetConfigDir() {
      configDir = DEFAULT_CONFIG_DIR;
   }

   private static Path getLocalFile() {
      return configDir.resolve("mods-database.json");
   }

   private static Path getOverrideFile() {
      return configDir.resolve("mods-database-override.json");
   }

   public static void updateFromRemoteIfNeeded() {
      Path localFile = getLocalFile();

      try {
         Files.createDirectories(configDir);
         if (Files.exists(localFile)) {
            Instant lastModified = Files.getLastModifiedTime(localFile).toInstant();
            if (isManuallyModified(localFile)) {
               Constants.LOG.info("{} \ud83d\uded1 Local mods-database.json was modified by user, skipping remote update.", "[Mods Database Updater]");
               String currentHash = calculateSha256(localFile);
               Files.writeString(localFile.resolveSibling("mods-database.json.sha256"), currentHash, StandardCharsets.UTF_8);
               return;
            }

            if (lastModified.plus(Duration.ofHours(48L)).isAfter(Instant.now())) {
               Constants.LOG.info("{} ⏳ Local mods-database.json is still fresh (last update: {}).", "[Mods Database Updater]", lastModified);
               return;
            }

            Constants.LOG.info("{} \ud83d\udd04 Local mods-database.json is outdated, fetching from remote …", "[Mods Database Updater]");
         }

         try (InputStream inputStream = new URL(
                  "https://raw.githubusercontent.com/MarkusBordihn/BOs-Mods-Optimizer/1.18.2/Common/src/main/resources/mods-database.json"
               )
               .openStream()) {
            Files.copy(inputStream, localFile, StandardCopyOption.REPLACE_EXISTING);
            String newHash = calculateSha256(localFile);
            Files.writeString(localFile.resolveSibling("mods-database.json.sha256"), newHash, StandardCharsets.UTF_8);
            Constants.LOG.info("{} ✅ Fetched remote mods-database.json and saved SHA-256 hash.", "[Mods Database Updater]");
         }
      } catch (IOException var6) {
         Constants.LOG.warn("{} ⚠ Failed to update mods-database.json: {}", "[Mods Database Updater]", var6.getMessage());
      }
   }

   public static JsonObject getModsDatabase() {
      Path localFile = getLocalFile();
      JsonObject baseDatabase = new JsonObject();
      if (Files.exists(localFile)) {
         try (InputStream inputStream = Files.newInputStream(localFile)) {
            JsonObject jsonObject = parseAndValidate(inputStream);
            if (jsonObject != null) {
               baseDatabase = jsonObject;
            }
         } catch (IOException var10) {
            Constants.LOG.warn("{} ⚠ Failed to read local mods-database.json: {}", "[Mods Database Updater]", var10.getMessage());
         }
      }

      if (baseDatabase.size() == 0) {
         try (InputStream inputStreamx = ModsDatabaseUpdater.class.getClassLoader().getResourceAsStream("mods-database.json")) {
            if (inputStreamx != null) {
               JsonObject jsonObject = parseAndValidate(inputStreamx);
               if (jsonObject != null) {
                  baseDatabase = jsonObject;
               }
            }
         } catch (IOException var8) {
            Constants.LOG.warn("{} ⚠ Failed to read fallback mods-database.json: {}", "[Mods Database Updater]", var8.getMessage());
         }
      }

      if (baseDatabase.size() == 0) {
         Constants.LOG.error("{} ❌ Could not load any valid mods-database.json!", "[Mods Database Updater]");
         return new JsonObject();
      } else {
         validateDuplicateModIds(baseDatabase, "mods-database.json");
         JsonObject overrideDatabase = getModsDatabaseOverride();
         if (overrideDatabase.size() > 0) {
            validateDuplicateModIds(overrideDatabase, "mods-database-override.json");
            baseDatabase = applyOverrides(baseDatabase, overrideDatabase);
         }

         return baseDatabase;
      }
   }

   public static JsonObject getModsDatabaseOverride() {
      Path overrideFile = getOverrideFile();
      if (!Files.exists(overrideFile)) {
         createOverrideTemplate();
         return new JsonObject();
      } else {
         try (InputStream inputStream = Files.newInputStream(overrideFile)) {
            JsonObject jsonObject = parseAndValidate(inputStream);
            return jsonObject != null ? jsonObject : new JsonObject();
         } catch (IOException var6) {
            Constants.LOG.warn("{} ⚠ Failed to read mods-database-override.json: {}", "[Mods Database Updater]", var6.getMessage());
            return new JsonObject();
         }
      }
   }

   private static void createOverrideTemplate() {
      try {
         Files.createDirectories(configDir);
         Path overrideFile = getOverrideFile();
         String template = "{\n  \"description\": \"Override entries from mods-database.json. This file is never modified automatically.\",\n  \"server\": [\n    \"server-override-mod-id\"\n  ],\n  \"client\": [\n    \"client-override-mod-id\"\n  ],\n  \"both\": [\n    \"both-override-mod-id\"\n  ]\n}\n";
         Files.writeString(overrideFile, template, StandardCharsets.UTF_8);
         Constants.LOG.info("{} ✅ Created mods-database-override.json template.", "[Mods Database Updater]");
      } catch (IOException var2) {
         Constants.LOG.warn("{} ⚠ Failed to create mods-database-override.json template: {}", "[Mods Database Updater]", var2.getMessage());
      }
   }

   public static Map<String, String> getSortedModDatabaseMap(JsonObject jsonObject) {
      Map<String, String> modIdMap = new TreeMap<>();
      addEntriesToMap(jsonObject, "client", "client", modIdMap);
      addEntriesToMap(jsonObject, "server", "server", modIdMap);
      addEntriesToMap(jsonObject, "both", "default", modIdMap);
      return modIdMap;
   }

   public static Set<String> getSortedModDatabaseSet(JsonObject jsonObject) {
      Set<String> modIdSet = new TreeSet<>();
      if (jsonObject.has("client")) {
         jsonObject.getAsJsonArray("client").forEach(e -> modIdSet.add(e.getAsString()));
      }

      if (jsonObject.has("server")) {
         jsonObject.getAsJsonArray("server").forEach(e -> modIdSet.add(e.getAsString()));
      }

      if (jsonObject.has("both")) {
         jsonObject.getAsJsonArray("both").forEach(e -> modIdSet.add(e.getAsString()));
      }

      return modIdSet;
   }

   private static void addEntriesToMap(JsonObject jsonObject, String key, String value, Map<String, String> map) {
      if (jsonObject.has(key)) {
         jsonObject.getAsJsonArray(key).forEach(jsonElement -> map.put(jsonElement.getAsString(), value));
      }
   }

   private static JsonObject parseAndValidate(InputStream inputStream) {
      try {
         JsonObject var9;
         try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            for (String key : VALID_KEYS) {
               if (!jsonObject.has(key) || !jsonObject.get(key).isJsonArray()) {
                  Constants.LOG.warn("{} ⚠ Key '{}' missing or invalid in mods database JSON", "[Mods Database Updater]", key);
                  return null;
               }
            }

            var9 = jsonObject;
         }

         return var9;
      } catch (Exception var8) {
         Constants.LOG.warn("{} ⚠ Failed to parse mods database JSON: {}", "[Mods Database Updater]", var8.getMessage());
         return null;
      }
   }

   private static void validateDuplicateModIds(JsonObject jsonObject, String fileName) {
      Map<String, String> modIdToCategory = new HashMap<>();

      for (String category : VALID_KEYS) {
         if (jsonObject.has(category) && jsonObject.get(category).isJsonArray()) {
            jsonObject.getAsJsonArray(category)
               .forEach(
                  element -> {
                     String modId = element.getAsString();
                     if (modIdToCategory.containsKey(modId)) {
                        Constants.LOG
                           .error(
                              "{} ❌ Duplicate mod ID '{}' found in both '{}' and '{}' in {}",
                              new Object[]{"[Mods Database Updater]", modId, modIdToCategory.get(modId), category, fileName}
                           );
                     } else {
                        modIdToCategory.put(modId, category);
                     }
                  }
               );
         }
      }
   }

   private static JsonObject applyOverrides(JsonObject baseDatabase, JsonObject overrideDatabase) {
      JsonObject mergedDatabase = new JsonObject();
      Set<String> overrideModIds = new HashSet<>();

      for (String category : VALID_KEYS) {
         if (overrideDatabase.has(category) && overrideDatabase.get(category).isJsonArray()) {
            overrideDatabase.getAsJsonArray(category).forEach(element -> overrideModIds.add(element.getAsString()));
         }
      }

      for (String categoryx : VALID_KEYS) {
         JsonArray mergedArray = new JsonArray();
         if (baseDatabase.has(categoryx) && baseDatabase.get(categoryx).isJsonArray()) {
            baseDatabase.getAsJsonArray(categoryx).forEach(element -> {
               String modId = element.getAsString();
               if (!overrideModIds.contains(modId)) {
                  mergedArray.add(modId);
               }
            });
         }

         if (overrideDatabase.has(categoryx) && overrideDatabase.get(categoryx).isJsonArray()) {
            overrideDatabase.getAsJsonArray(categoryx).forEach(element -> mergedArray.add(element.getAsString()));
         }

         mergedDatabase.add(categoryx, mergedArray);
      }

      return mergedDatabase;
   }

   private static boolean isManuallyModified(Path path) {
      Path hashFile = path.resolveSibling(path.getFileName() + ".sha256");
      if (Files.exists(path) && Files.exists(hashFile)) {
         try {
            String expectedHash = Files.readString(hashFile, StandardCharsets.UTF_8).trim();
            String actualHash = calculateSha256(path);
            boolean modified = !expectedHash.equals(actualHash);
            if (modified) {
               Constants.LOG.info("{} \ud83d\uded1 mods-database.json was modified (SHA-256 hash mismatch)", "[Mods Database Updater]");
            }

            return modified;
         } catch (IOException var5) {
            Constants.LOG.warn("{} ⚠ Failed to check hash for {}: {}", new Object[]{"[Mods Database Updater]", path.getFileName(), var5.getMessage()});
            return false;
         }
      } else {
         return false;
      }
   }

   private static String calculateSha256(Path file) throws IOException {
      try {
         String var14;
         try (InputStream inputStream = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
               digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder stringBuilder = new StringBuilder();

            for (byte b : hashBytes) {
               stringBuilder.append(String.format("%02x", b));
            }

            var14 = stringBuilder.toString();
         }

         return var14;
      } catch (Exception var13) {
         throw new IOException("[Mods Database Updater] Unable to calculate SHA-256 hash for " + file, var13);
      }
   }
}
