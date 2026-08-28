/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package de.markusbordihn.modsoptimizer.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
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
    private static final Path DEFAULT_CONFIG_DIR = Paths.get("", new String[0]).toAbsolutePath().resolve("config").resolve("mods_optimizer");
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
        return configDir.resolve(MODS_DATABASE_FILE);
    }

    private static Path getOverrideFile() {
        return configDir.resolve("mods-database-override.json");
    }

    public static void updateFromRemoteIfNeeded() {
        Path localFile = ModsDatabaseUpdater.getLocalFile();
        try {
            Files.createDirectories(configDir, new FileAttribute[0]);
            if (Files.exists(localFile, new LinkOption[0])) {
                Instant lastModified = Files.getLastModifiedTime(localFile, new LinkOption[0]).toInstant();
                if (ModsDatabaseUpdater.isManuallyModified(localFile)) {
                    Constants.LOG.info("{} \ud83d\uded1 Local mods-database.json was modified by user, skipping remote update.", (Object)LOG_PREFIX);
                    String currentHash = ModsDatabaseUpdater.calculateSha256(localFile);
                    Files.writeString(localFile.resolveSibling("mods-database.json.sha256"), (CharSequence)currentHash, StandardCharsets.UTF_8, new OpenOption[0]);
                    return;
                }
                if (lastModified.plus(Duration.ofHours(48L)).isAfter(Instant.now())) {
                    Constants.LOG.info("{} \u23f3 Local mods-database.json is still fresh (last update: {}).", (Object)LOG_PREFIX, (Object)lastModified);
                    return;
                }
                Constants.LOG.info("{} \ud83d\udd04 Local mods-database.json is outdated, fetching from remote \u2026", (Object)LOG_PREFIX);
            }
            try (InputStream inputStream = new URL(REMOTE_URL).openStream();){
                Files.copy(inputStream, localFile, StandardCopyOption.REPLACE_EXISTING);
                String newHash = ModsDatabaseUpdater.calculateSha256(localFile);
                Files.writeString(localFile.resolveSibling("mods-database.json.sha256"), (CharSequence)newHash, StandardCharsets.UTF_8, new OpenOption[0]);
                Constants.LOG.info("{} \u2705 Fetched remote mods-database.json and saved SHA-256 hash.", (Object)LOG_PREFIX);
            }
        }
        catch (IOException e) {
            Constants.LOG.warn("{} \u26a0 Failed to update mods-database.json: {}", (Object)LOG_PREFIX, (Object)e.getMessage());
        }
    }

    public static JsonObject getModsDatabase() {
        JsonObject jsonObject;
        InputStream inputStream;
        Path localFile = ModsDatabaseUpdater.getLocalFile();
        JsonObject baseDatabase = new JsonObject();
        if (Files.exists(localFile, new LinkOption[0])) {
            try {
                inputStream = Files.newInputStream(localFile, new OpenOption[0]);
                try {
                    jsonObject = ModsDatabaseUpdater.parseAndValidate(inputStream);
                    if (jsonObject != null) {
                        baseDatabase = jsonObject;
                    }
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (IOException e) {
                Constants.LOG.warn("{} \u26a0 Failed to read local mods-database.json: {}", (Object)LOG_PREFIX, (Object)e.getMessage());
            }
        }
        if (baseDatabase.size() == 0) {
            try {
                inputStream = ModsDatabaseUpdater.class.getClassLoader().getResourceAsStream(MODS_DATABASE_FILE);
                try {
                    if (inputStream != null && (jsonObject = ModsDatabaseUpdater.parseAndValidate(inputStream)) != null) {
                        baseDatabase = jsonObject;
                    }
                }
                finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
            catch (IOException e) {
                Constants.LOG.warn("{} \u26a0 Failed to read fallback mods-database.json: {}", (Object)LOG_PREFIX, (Object)e.getMessage());
            }
        }
        if (baseDatabase.size() == 0) {
            Constants.LOG.error("{} \u274c Could not load any valid mods-database.json!", (Object)LOG_PREFIX);
            return new JsonObject();
        }
        ModsDatabaseUpdater.validateDuplicateModIds(baseDatabase, MODS_DATABASE_FILE);
        JsonObject overrideDatabase = ModsDatabaseUpdater.getModsDatabaseOverride();
        if (overrideDatabase.size() > 0) {
            ModsDatabaseUpdater.validateDuplicateModIds(overrideDatabase, "mods-database-override.json");
            baseDatabase = ModsDatabaseUpdater.applyOverrides(baseDatabase, overrideDatabase);
        }
        return baseDatabase;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static JsonObject getModsDatabaseOverride() {
        Path overrideFile = ModsDatabaseUpdater.getOverrideFile();
        if (!Files.exists(overrideFile, new LinkOption[0])) {
            ModsDatabaseUpdater.createOverrideTemplate();
            return new JsonObject();
        }
        try (InputStream inputStream = Files.newInputStream(overrideFile, new OpenOption[0]);){
            JsonObject jsonObject2 = ModsDatabaseUpdater.parseAndValidate(inputStream);
            if (jsonObject2 == null) return new JsonObject();
            JsonObject jsonObject = jsonObject2;
            return jsonObject;
        }
        catch (IOException e) {
            Constants.LOG.warn("{} \u26a0 Failed to read mods-database-override.json: {}", (Object)LOG_PREFIX, (Object)e.getMessage());
        }
        return new JsonObject();
    }

    private static void createOverrideTemplate() {
        try {
            Files.createDirectories(configDir, new FileAttribute[0]);
            Path overrideFile = ModsDatabaseUpdater.getOverrideFile();
            String template = "{\n  \"description\": \"Override entries from mods-database.json. This file is never modified automatically.\",\n  \"server\": [\n    \"server-override-mod-id\"\n  ],\n  \"client\": [\n    \"client-override-mod-id\"\n  ],\n  \"both\": [\n    \"both-override-mod-id\"\n  ]\n}\n";
            Files.writeString(overrideFile, (CharSequence)template, StandardCharsets.UTF_8, new OpenOption[0]);
            Constants.LOG.info("{} \u2705 Created mods-database-override.json template.", (Object)LOG_PREFIX);
        }
        catch (IOException e) {
            Constants.LOG.warn("{} \u26a0 Failed to create mods-database-override.json template: {}", (Object)LOG_PREFIX, (Object)e.getMessage());
        }
    }

    public static Map<String, String> getSortedModDatabaseMap(JsonObject jsonObject) {
        TreeMap<String, String> modIdMap = new TreeMap<String, String>();
        ModsDatabaseUpdater.addEntriesToMap(jsonObject, KEY_CLIENT, KEY_CLIENT, modIdMap);
        ModsDatabaseUpdater.addEntriesToMap(jsonObject, KEY_SERVER, KEY_SERVER, modIdMap);
        ModsDatabaseUpdater.addEntriesToMap(jsonObject, KEY_BOTH, "default", modIdMap);
        return modIdMap;
    }

    public static Set<String> getSortedModDatabaseSet(JsonObject jsonObject) {
        TreeSet<String> modIdSet = new TreeSet<String>();
        if (jsonObject.has(KEY_CLIENT)) {
            jsonObject.getAsJsonArray(KEY_CLIENT).forEach(e -> modIdSet.add(e.getAsString()));
        }
        if (jsonObject.has(KEY_SERVER)) {
            jsonObject.getAsJsonArray(KEY_SERVER).forEach(e -> modIdSet.add(e.getAsString()));
        }
        if (jsonObject.has(KEY_BOTH)) {
            jsonObject.getAsJsonArray(KEY_BOTH).forEach(e -> modIdSet.add(e.getAsString()));
        }
        return modIdSet;
    }

    private static void addEntriesToMap(JsonObject jsonObject, String key, String value, Map<String, String> map) {
        if (jsonObject.has(key)) {
            jsonObject.getAsJsonArray(key).forEach(jsonElement -> map.put(jsonElement.getAsString(), value));
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static JsonObject parseAndValidate(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
            JsonObject jsonObject = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            for (String key : VALID_KEYS) {
                if (jsonObject.has(key) && jsonObject.get(key).isJsonArray()) continue;
                Constants.LOG.warn("{} \u26a0 Key '{}' missing or invalid in mods database JSON", (Object)LOG_PREFIX, (Object)key);
                JsonObject jsonObject2 = null;
                return jsonObject2;
            }
            JsonObject jsonObject3 = jsonObject;
            return jsonObject3;
        }
        catch (Exception e) {
            Constants.LOG.warn("{} \u26a0 Failed to parse mods database JSON: {}", (Object)LOG_PREFIX, (Object)e.getMessage());
            return null;
        }
    }

    private static void validateDuplicateModIds(JsonObject jsonObject, String fileName) {
        HashMap modIdToCategory = new HashMap();
        for (String category : VALID_KEYS) {
            if (!jsonObject.has(category) || !jsonObject.get(category).isJsonArray()) continue;
            jsonObject.getAsJsonArray(category).forEach(element -> {
                String modId = element.getAsString();
                if (modIdToCategory.containsKey(modId)) {
                    Constants.LOG.error("{} \u274c Duplicate mod ID '{}' found in both '{}' and '{}' in {}", new Object[]{LOG_PREFIX, modId, modIdToCategory.get(modId), category, fileName});
                } else {
                    modIdToCategory.put(modId, category);
                }
            });
        }
    }

    private static JsonObject applyOverrides(JsonObject baseDatabase, JsonObject overrideDatabase) {
        JsonObject mergedDatabase = new JsonObject();
        HashSet overrideModIds = new HashSet();
        for (String category : VALID_KEYS) {
            if (!overrideDatabase.has(category) || !overrideDatabase.get(category).isJsonArray()) continue;
            overrideDatabase.getAsJsonArray(category).forEach(element -> overrideModIds.add(element.getAsString()));
        }
        for (String category : VALID_KEYS) {
            JsonArray mergedArray = new JsonArray();
            if (baseDatabase.has(category) && baseDatabase.get(category).isJsonArray()) {
                baseDatabase.getAsJsonArray(category).forEach(element -> {
                    String modId = element.getAsString();
                    if (!overrideModIds.contains(modId)) {
                        mergedArray.add(modId);
                    }
                });
            }
            if (overrideDatabase.has(category) && overrideDatabase.get(category).isJsonArray()) {
                overrideDatabase.getAsJsonArray(category).forEach(element -> mergedArray.add(element.getAsString()));
            }
            mergedDatabase.add(category, (JsonElement)mergedArray);
        }
        return mergedDatabase;
    }

    private static boolean isManuallyModified(Path path) {
        Path hashFile = path.resolveSibling(String.valueOf(path.getFileName()) + ".sha256");
        if (!Files.exists(path, new LinkOption[0]) || !Files.exists(hashFile, new LinkOption[0])) {
            return false;
        }
        try {
            boolean modified;
            String expectedHash = Files.readString(hashFile, StandardCharsets.UTF_8).trim();
            String actualHash = ModsDatabaseUpdater.calculateSha256(path);
            boolean bl = modified = !expectedHash.equals(actualHash);
            if (modified) {
                Constants.LOG.info("{} \ud83d\uded1 mods-database.json was modified (SHA-256 hash mismatch)", (Object)LOG_PREFIX);
            }
            return modified;
        }
        catch (IOException e) {
            Constants.LOG.warn("{} \u26a0 Failed to check hash for {}: {}", new Object[]{LOG_PREFIX, path.getFileName(), e.getMessage()});
            return false;
        }
    }

    private static String calculateSha256(Path file) throws IOException {
        String string;
        block10: {
            InputStream inputStream = Files.newInputStream(file, new OpenOption[0]);
            try {
                int bytesRead;
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] buffer = new byte[8192];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
                byte[] hashBytes = digest.digest();
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : hashBytes) {
                    stringBuilder.append(String.format("%02x", b));
                }
                string = stringBuilder.toString();
                if (inputStream == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    throw new IOException("[Mods Database Updater] Unable to calculate SHA-256 hash for " + String.valueOf(file), e);
                }
            }
            inputStream.close();
        }
        return string;
    }
}

