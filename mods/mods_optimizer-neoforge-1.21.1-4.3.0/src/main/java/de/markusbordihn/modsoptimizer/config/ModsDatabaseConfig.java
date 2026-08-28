/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package de.markusbordihn.modsoptimizer.config;

import com.google.gson.JsonObject;
import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.config.ModsDatabaseUpdater;
import de.markusbordihn.modsoptimizer.data.ModFileData;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Toml;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.TomlWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModsDatabaseConfig {
    public static final Path CONFIG_PATH = Paths.get("", new String[0]).toAbsolutePath().resolve("config").resolve("mods_optimizer");
    public static final String ALLOW_REMOTE_DATABASE = "allowRemoteDatabase";
    public static final String DEBUG_ENABLED = "debugEnabled";
    public static final String DEBUG_FORCE_SIDE = "debugForceSide";
    public static final String CONFIG_FILE_NAME = "config.toml";
    private static final Map<String, String> modsMap = new HashMap<String, String>();
    private static boolean allowRemoteDatabase = true;
    private static boolean debugEnabled = false;
    private static String debugForceSide = "default";

    protected ModsDatabaseConfig() {
    }

    public static String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }

    public static Map<String, String> getConfig() {
        return modsMap;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static String getDebugForceSide() {
        return debugForceSide;
    }

    public static boolean containsMod(String modId) {
        return modsMap.containsKey(modId);
    }

    public static ModFileData.ModEnvironment getModEnvironment(String modId) {
        String modType = modsMap.get(modId);
        if ("client".equals(modType)) {
            return ModFileData.ModEnvironment.CLIENT;
        }
        if ("server".equals(modType)) {
            return ModFileData.ModEnvironment.SERVER;
        }
        return ModFileData.ModEnvironment.BOTH;
    }

    public static String cleanTomlFileWithWarnings(File file) throws IOException {
        HashSet<String> seenKeys = new HashSet<String>();
        StringBuilder cleanedToml = new StringBuilder();
        int lineNumber = 0;
        for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
            ++lineNumber;
            String trimmed = line.trim();
            if (trimmed.startsWith("#") || !trimmed.contains("=")) {
                cleanedToml.append(line).append("\n");
                continue;
            }
            String[] keyValue = trimmed.split("=", 2);
            if (keyValue.length < 2) {
                cleanedToml.append(line).append("\n");
                continue;
            }
            String key = keyValue[0].trim();
            if (seenKeys.contains(key)) {
                Constants.LOG.warn("\u26a0 Duplicate key '{}' found on line {} in config file {}. This entry was ignored.", new Object[]{key, lineNumber, file.getName()});
                continue;
            }
            seenKeys.add(key);
            cleanedToml.append(line).append("\n");
        }
        return cleanedToml.toString();
    }

    private static void readConfigFile(File file) {
        if (file == null) {
            file = ModsDatabaseConfig.getConfigFile();
        }
        if (!(file != null && file.exists() && file.canWrite() && file.canRead())) {
            Constants.LOG.error("\u26a0 Unable to load config file {}!", (Object)file);
            return;
        }
        Constants.LOG.info("Loading Mods Database Config File from {}", (Object)file);
        try {
            Object debugObject;
            String stringValue;
            Map database;
            Object allowRemoteDatabaseValue;
            Object databaseObject;
            String cleanedToml = ModsDatabaseConfig.cleanTomlFileWithWarnings(file);
            Map<String, Object> config = new Toml().read(cleanedToml).toMap();
            if (config.containsKey("Database") && (databaseObject = config.get("Database")) instanceof Map && (allowRemoteDatabaseValue = (database = (Map)databaseObject).get(ALLOW_REMOTE_DATABASE)) instanceof String) {
                stringValue = (String)allowRemoteDatabaseValue;
                allowRemoteDatabase = Boolean.parseBoolean(stringValue);
            }
            if (config.containsKey("Debug") && (debugObject = config.get("Debug")) instanceof Map) {
                Object debugForceSideValue;
                Map debugMap = (Map)debugObject;
                Object debugEnabledValue = debugMap.get(DEBUG_ENABLED);
                if (debugEnabledValue instanceof String) {
                    stringValue = (String)debugEnabledValue;
                    debugEnabled = Boolean.parseBoolean(stringValue);
                }
                if ((debugForceSideValue = debugMap.get(DEBUG_FORCE_SIDE)) instanceof String) {
                    String stringValue2;
                    debugForceSide = stringValue2 = (String)debugForceSideValue;
                }
            }
        }
        catch (Exception exception) {
            Constants.LOG.error("There was an error, loading the config file {}:", (Object)file, (Object)exception);
        }
    }

    private static void appendFileHeader(StringBuilder stringBuilder) {
        stringBuilder.append("# This file was auto-generated by ").append("Mods Optimizer").append("\n").append("# Last update: ").append(LocalDateTime.now()).append("\n");
    }

    private static File createConfigFile(File file) {
        Constants.LOG.info("Creating Mods Database Config File under {}", (Object)file);
        StringBuilder textContent = new StringBuilder();
        ModsDatabaseConfig.appendFileHeader(textContent);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TomlWriter tomlWriter = new TomlWriter.Builder().build();
        HashMap<String, String> databaseOptions = new HashMap<String, String>();
        databaseOptions.put(ALLOW_REMOTE_DATABASE, allowRemoteDatabase ? "true" : "false");
        try {
            tomlWriter.write(databaseOptions, outputStream);
            textContent.append("\n[Database]\n").append(outputStream);
        }
        catch (Exception exception) {
            Constants.LOG.error("There was an error, adding the database options to the config file {}:", (Object)file, (Object)exception);
            return null;
        }
        HashMap<String, String> debugOptions = new HashMap<String, String>();
        debugOptions.put(DEBUG_ENABLED, debugEnabled ? "true" : "false");
        debugOptions.put(DEBUG_FORCE_SIDE, debugForceSide);
        outputStream = new ByteArrayOutputStream();
        try {
            tomlWriter.write(debugOptions, outputStream);
            textContent.append("\n[Debug]\n").append(outputStream);
        }
        catch (Exception exception) {
            Constants.LOG.error("There was an error, adding the debug options to the config file {}:", (Object)file, (Object)exception);
            return null;
        }
        try {
            Files.writeString(file.toPath(), (CharSequence)textContent, StandardOpenOption.CREATE_NEW);
        }
        catch (Exception exception) {
            Constants.LOG.error("There was an error, writing the config file to {}:", (Object)file, (Object)exception);
            return null;
        }
        return file;
    }

    public static File getConfigFile() {
        Path path = ModsDatabaseConfig.getConfigDirectory();
        if (path != null) {
            return path.resolve(ModsDatabaseConfig.getConfigFileName()).toFile();
        }
        return null;
    }

    private static Path getConfigDirectory() {
        Path resultPath = null;
        try {
            resultPath = Files.createDirectories(CONFIG_PATH, new FileAttribute[0]);
        }
        catch (Exception exception) {
            Constants.LOG.error("There was an error, creating the config directory {}:", (Object)CONFIG_PATH, (Object)exception);
        }
        return resultPath;
    }

    static {
        File configFile = ModsDatabaseConfig.getConfigFile();
        if (configFile == null || !configFile.exists()) {
            configFile = ModsDatabaseConfig.createConfigFile(configFile);
        }
        ModsDatabaseConfig.readConfigFile(configFile);
        if (allowRemoteDatabase) {
            ModsDatabaseUpdater.updateFromRemoteIfNeeded();
        }
        JsonObject json = ModsDatabaseUpdater.getModsDatabase();
        modsMap.putAll(ModsDatabaseUpdater.getSortedModDatabaseMap(json));
        JsonObject overrideJson = ModsDatabaseUpdater.getModsDatabaseOverride();
        Set<String> overrideKeys = ModsDatabaseUpdater.getSortedModDatabaseSet(overrideJson);
        if (!overrideKeys.isEmpty()) {
            Map<String, String> overrides = ModsDatabaseUpdater.getSortedModDatabaseMap(overrideJson);
            for (Map.Entry<String, String> entry : overrides.entrySet()) {
                String modId = entry.getKey();
                String newType = entry.getValue();
                String oldType = modsMap.get(modId);
                if (oldType != null && !oldType.equals(newType)) {
                    Constants.LOG.info("{} Override: {} changed from {} to {}", new Object[]{"Mods Optimizer", modId, oldType, newType});
                    continue;
                }
                if (oldType != null) continue;
                Constants.LOG.info("{} Override: {} set to {}", new Object[]{"Mods Optimizer", modId, newType});
            }
            modsMap.putAll(overrides);
        }
        Constants.LOG.info("{} Mods Database Config File loaded with {} mods client: {}, server: {}, default: {}{}", new Object[]{"Mods Optimizer", modsMap.size(), modsMap.values().stream().filter(modType -> modType.equals("client")).count(), modsMap.values().stream().filter(modType -> modType.equals("server")).count(), modsMap.values().stream().filter(modType -> modType.equals("default")).count(), overrideKeys.isEmpty() ? "." : " (" + overrideKeys.size() + " overrides)."});
    }
}

