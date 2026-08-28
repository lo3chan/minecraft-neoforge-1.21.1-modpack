/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.data;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.config.ModsDatabaseConfig;
import de.markusbordihn.modsoptimizer.data.ModFileData;
import de.markusbordihn.modsoptimizer.data.ModFileParser;
import java.io.File;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ModData {
    private static final String LOG_PREFIX = "[Mod Data]";
    private static final String FILE_EXTENSION = ".jar";
    private static final String OVERVIEW_SEPARATOR = "-".repeat(115);
    private static final Map<String, Set<ModFileData>> duplicatedModsMap = new HashMap<String, Set<ModFileData>>();
    private static final Map<String, ModFileData> knownModsMap = new HashMap<String, ModFileData>();
    private static final Set<ModFileData> clientModsSet = new HashSet<ModFileData>();
    private static final Set<ModFileData> dataPackModsSet = new HashSet<ModFileData>();
    private static final Set<ModFileData> serverModsSet = new HashSet<ModFileData>();
    private static final Set<ModFileData> serviceModsSet = new HashSet<ModFileData>();
    private static final Set<ModFileData> libraryModsSet = new HashSet<ModFileData>();
    private static final Set<ModFileData> defaultModsSet = new HashSet<ModFileData>();
    private static final Set<ModFileData> languageProviderModsSet = new HashSet<ModFileData>();

    protected ModData() {
    }

    public static void parseMods(File modPath) {
        ModData.parseMods(modPath, FILE_EXTENSION);
    }

    public static void parseMods(File modPath, String fileExtension) {
        if (modPath == null || !modPath.exists()) {
            Constants.LOG.error("{} \u26a0 Unable to find valid mod path: {}", (Object)LOG_PREFIX, (Object)modPath);
            return;
        }
        File[] modsFiles = modPath.listFiles();
        if (modsFiles == null) {
            Constants.LOG.error("{} \u26a0 Unable to find valid mod files in path: {}", (Object)LOG_PREFIX, (Object)modPath);
            return;
        }
        Constants.LOG.info("{} parsing ~{} mods in {} with file extension {} ...", new Object[]{LOG_PREFIX, modsFiles.length, modPath, fileExtension});
        block8: for (File modFile : modsFiles) {
            String modFileName = modFile.getName();
            if (!modFileName.endsWith(fileExtension)) {
                Constants.LOG.debug("{} \u26a0 Ignore mod file {} in {} with file extension {}", new Object[]{LOG_PREFIX, modFileName, modFile.getAbsolutePath(), fileExtension});
                continue;
            }
            ModFileData modFileData = ModData.readModInfo(modFile);
            if (modFileData == null || modFileData.id() == null || modFileData.id().isEmpty()) {
                Constants.LOG.error("{} \u26a0 Unable to parse mod file {} in {}", new Object[]{LOG_PREFIX, modFileName, modFile.getAbsolutePath()});
                continue;
            }
            if (!modFileData.id().equals("unknown_id")) {
                if (knownModsMap.containsKey(modFileData.id())) {
                    Constants.LOG.error("{} \u26a0 Duplicated mod {} found in {} and {}", new Object[]{LOG_PREFIX, modFileData.id(), modFileData.path(), knownModsMap.get(modFileData.id()).path()});
                    duplicatedModsMap.computeIfAbsent(modFileData.id(), k -> new HashSet()).addAll(Set.of(modFileData, knownModsMap.get(modFileData.id())));
                } else {
                    knownModsMap.put(modFileData.id(), modFileData);
                }
            }
            switch (modFileData.environment()) {
                case CLIENT: {
                    clientModsSet.add(modFileData);
                    continue block8;
                }
                case SERVER: {
                    serverModsSet.add(modFileData);
                    continue block8;
                }
                case SERVICE: {
                    serviceModsSet.add(modFileData);
                    continue block8;
                }
                case LIBRARY: {
                    libraryModsSet.add(modFileData);
                    continue block8;
                }
                case LANGUAGE_PROVIDER: {
                    languageProviderModsSet.add(modFileData);
                    continue block8;
                }
                case DATA_PACK: {
                    dataPackModsSet.add(modFileData);
                    continue block8;
                }
                default: {
                    defaultModsSet.add(modFileData);
                }
            }
        }
        ModData.showStats();
        ModData.showOverview();
    }

    private static void showStats() {
        ModData.logStats("duplicated", duplicatedModsMap.size(), !duplicatedModsMap.isEmpty());
        ModData.logStats("language provider", languageProviderModsSet.size(), !languageProviderModsSet.isEmpty());
        ModData.logStats("library", libraryModsSet.size(), !libraryModsSet.isEmpty());
        ModData.logStats("data pack", dataPackModsSet.size(), !dataPackModsSet.isEmpty());
        ModData.logStats("client", clientModsSet.size(), !clientModsSet.isEmpty());
        ModData.logStats("server", serverModsSet.size(), !serverModsSet.isEmpty());
        ModData.logStats("service", serviceModsSet.size(), !serviceModsSet.isEmpty());
        ModData.logStats("default", defaultModsSet.size(), !defaultModsSet.isEmpty());
    }

    private static void logStats(String type, int size, boolean condition) {
        if (condition) {
            Constants.LOG.info("{} Found {} {} mods in {} mods.", new Object[]{LOG_PREFIX, size, type, knownModsMap.size()});
        }
    }

    private static void showOverview() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String overviewHeader = String.format("| %-34s | %-22s | %-8s | %-17s | %-19s |", "ID", "VERSION", "TYPE", "ENVIRONMENT", "TIMESTAMP");
        Constants.LOG.info(OVERVIEW_SEPARATOR);
        Constants.LOG.info(overviewHeader);
        Constants.LOG.info(OVERVIEW_SEPARATOR);
        for (ModFileData modFileData : knownModsMap.values()) {
            String modEntry = String.format("| %-34s | %-22s | %-8s | %-17s | %-19s |", new Object[]{modFileData.id(), modFileData.version(), modFileData.modType(), modFileData.environment(), modFileData.timestamp().format(dateTimeFormatter)});
            Constants.LOG.info(modEntry);
        }
        Constants.LOG.info(OVERVIEW_SEPARATOR);
    }

    public static ModFileData readRawModInfo(File parent, String modFile) {
        return ModData.readModInfo(new File(parent, modFile).toPath(), false);
    }

    public static ModFileData readModInfo(File parent, String modFile) {
        return ModData.readModInfo(new File(parent, modFile));
    }

    public static ModFileData readModInfo(File modFile) {
        if (modFile == null || !modFile.exists()) {
            Constants.LOG.error("{} \u26a0 Unable to find mod file at: {}", (Object)LOG_PREFIX, (Object)modFile);
            return null;
        }
        return ModData.readModInfo(modFile.toPath());
    }

    public static Set<ModFileData> getKnownMods() {
        return new HashSet<ModFileData>(knownModsMap.values());
    }

    public static Set<ModFileData> getClientMods() {
        return new HashSet<ModFileData>(clientModsSet);
    }

    public static Map<String, Set<ModFileData>> getDuplicatedMods() {
        return new HashMap<String, Set<ModFileData>>(duplicatedModsMap);
    }

    public static void clear() {
        duplicatedModsMap.clear();
        knownModsMap.clear();
        clientModsSet.clear();
        dataPackModsSet.clear();
        serverModsSet.clear();
        serviceModsSet.clear();
        libraryModsSet.clear();
        defaultModsSet.clear();
        languageProviderModsSet.clear();
    }

    public static ModFileData readModInfo(Path modFile) {
        return ModData.readModInfo(modFile, true);
    }

    public static ModFileData readModInfo(Path modFile, boolean useModsDatabaseConfig) {
        ModFileData modFileData;
        JarFile jarFile = new JarFile(modFile.toFile());
        try {
            ModFileData.ModEnvironment modEnvironment;
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                Constants.LOG.warn("{} \u26a0 Unable to read manifest from mod file {}, which is expected in some cases.", (Object)LOG_PREFIX, (Object)modFile);
            }
            ModFileData modFileData2 = ModFileParser.parseModFile(manifest, modFile, jarFile);
            if (useModsDatabaseConfig && ModsDatabaseConfig.containsMod(modFileData2.id()) && (modEnvironment = ModsDatabaseConfig.getModEnvironment(modFileData2.id())) != modFileData2.environment()) {
                Constants.LOG.info("{} Overwrite mod environment for {} from {} to {}", new Object[]{LOG_PREFIX, modFileData2.id(), modFileData2.environment(), modEnvironment});
                modFileData2 = new ModFileData(modFileData2.path(), modFileData2.id(), modFileData2.modType(), modFileData2.name(), modFileData2.version(), modEnvironment, modFileData2.timestamp());
            }
            if (ModsDatabaseConfig.isDebugEnabled()) {
                Constants.LOG.info("{} {}", (Object)LOG_PREFIX, (Object)modFileData2);
            }
            modFileData = modFileData2;
        }
        catch (Throwable throwable) {
            try {
                try {
                    jarFile.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                Constants.LOG.error("{} \u26a0 Unable to read mod file {}:", new Object[]{LOG_PREFIX, modFile, e});
                return null;
            }
        }
        jarFile.close();
        return modFileData;
    }
}

