package de.markusbordihn.modsoptimizer.data;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.config.ModsDatabaseConfig;
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
   private static final Map<String, Set<ModFileData>> duplicatedModsMap = new HashMap<>();
   private static final Map<String, ModFileData> knownModsMap = new HashMap<>();
   private static final Set<ModFileData> clientModsSet = new HashSet<>();
   private static final Set<ModFileData> dataPackModsSet = new HashSet<>();
   private static final Set<ModFileData> serverModsSet = new HashSet<>();
   private static final Set<ModFileData> serviceModsSet = new HashSet<>();
   private static final Set<ModFileData> libraryModsSet = new HashSet<>();
   private static final Set<ModFileData> defaultModsSet = new HashSet<>();
   private static final Set<ModFileData> languageProviderModsSet = new HashSet<>();

   protected ModData() {
   }

   public static void parseMods(File modPath) {
      parseMods(modPath, ".jar");
   }

   public static void parseMods(File modPath, String fileExtension) {
      if (modPath != null && modPath.exists()) {
         File[] modsFiles = modPath.listFiles();
         if (modsFiles == null) {
            Constants.LOG.error("{} ⚠ Unable to find valid mod files in path: {}", "[Mod Data]", modPath);
         } else {
            Constants.LOG.info("{} parsing ~{} mods in {} with file extension {} ...", new Object[]{"[Mod Data]", modsFiles.length, modPath, fileExtension});

            for (File modFile : modsFiles) {
               String modFileName = modFile.getName();
               if (!modFileName.endsWith(fileExtension)) {
                  Constants.LOG
                     .debug(
                        "{} ⚠ Ignore mod file {} in {} with file extension {}",
                        new Object[]{"[Mod Data]", modFileName, modFile.getAbsolutePath(), fileExtension}
                     );
               } else {
                  ModFileData modFileData = readModInfo(modFile);
                  if (modFileData != null && modFileData.id() != null && !modFileData.id().isEmpty()) {
                     if (!modFileData.id().equals("unknown_id")) {
                        if (knownModsMap.containsKey(modFileData.id())) {
                           Constants.LOG
                              .error(
                                 "{} ⚠ Duplicated mod {} found in {} and {}",
                                 new Object[]{"[Mod Data]", modFileData.id(), modFileData.path(), knownModsMap.get(modFileData.id()).path()}
                              );
                           duplicatedModsMap.computeIfAbsent(modFileData.id(), k -> new HashSet<>())
                              .addAll(Set.of(modFileData, knownModsMap.get(modFileData.id())));
                        } else {
                           knownModsMap.put(modFileData.id(), modFileData);
                        }
                     }

                     switch (modFileData.environment()) {
                        case CLIENT:
                           clientModsSet.add(modFileData);
                           break;
                        case SERVER:
                           serverModsSet.add(modFileData);
                           break;
                        case SERVICE:
                           serviceModsSet.add(modFileData);
                           break;
                        case LIBRARY:
                           libraryModsSet.add(modFileData);
                           break;
                        case LANGUAGE_PROVIDER:
                           languageProviderModsSet.add(modFileData);
                           break;
                        case DATA_PACK:
                           dataPackModsSet.add(modFileData);
                           break;
                        default:
                           defaultModsSet.add(modFileData);
                     }
                  } else {
                     Constants.LOG.error("{} ⚠ Unable to parse mod file {} in {}", new Object[]{"[Mod Data]", modFileName, modFile.getAbsolutePath()});
                  }
               }
            }

            showStats();
            showOverview();
         }
      } else {
         Constants.LOG.error("{} ⚠ Unable to find valid mod path: {}", "[Mod Data]", modPath);
      }
   }

   private static void showStats() {
      logStats("duplicated", duplicatedModsMap.size(), !duplicatedModsMap.isEmpty());
      logStats("language provider", languageProviderModsSet.size(), !languageProviderModsSet.isEmpty());
      logStats("library", libraryModsSet.size(), !libraryModsSet.isEmpty());
      logStats("data pack", dataPackModsSet.size(), !dataPackModsSet.isEmpty());
      logStats("client", clientModsSet.size(), !clientModsSet.isEmpty());
      logStats("server", serverModsSet.size(), !serverModsSet.isEmpty());
      logStats("service", serviceModsSet.size(), !serviceModsSet.isEmpty());
      logStats("default", defaultModsSet.size(), !defaultModsSet.isEmpty());
   }

   private static void logStats(String type, int size, boolean condition) {
      if (condition) {
         Constants.LOG.info("{} Found {} {} mods in {} mods.", new Object[]{"[Mod Data]", size, type, knownModsMap.size()});
      }
   }

   private static void showOverview() {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String overviewHeader = String.format("| %-34s | %-22s | %-8s | %-17s | %-19s |", "ID", "VERSION", "TYPE", "ENVIRONMENT", "TIMESTAMP");
      Constants.LOG.info(OVERVIEW_SEPARATOR);
      Constants.LOG.info(overviewHeader);
      Constants.LOG.info(OVERVIEW_SEPARATOR);

      for (ModFileData modFileData : knownModsMap.values()) {
         String modEntry = String.format(
            "| %-34s | %-22s | %-8s | %-17s | %-19s |",
            modFileData.id(),
            modFileData.version(),
            modFileData.modType(),
            modFileData.environment(),
            modFileData.timestamp().format(dateTimeFormatter)
         );
         Constants.LOG.info(modEntry);
      }

      Constants.LOG.info(OVERVIEW_SEPARATOR);
   }

   public static ModFileData readRawModInfo(File parent, String modFile) {
      return readModInfo(new File(parent, modFile).toPath(), false);
   }

   public static ModFileData readModInfo(File parent, String modFile) {
      return readModInfo(new File(parent, modFile));
   }

   public static ModFileData readModInfo(File modFile) {
      if (modFile != null && modFile.exists()) {
         return readModInfo(modFile.toPath());
      } else {
         Constants.LOG.error("{} ⚠ Unable to find mod file at: {}", "[Mod Data]", modFile);
         return null;
      }
   }

   public static Set<ModFileData> getKnownMods() {
      return new HashSet<>(knownModsMap.values());
   }

   public static Set<ModFileData> getClientMods() {
      return new HashSet<>(clientModsSet);
   }

   public static Map<String, Set<ModFileData>> getDuplicatedMods() {
      return new HashMap<>(duplicatedModsMap);
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
      return readModInfo(modFile, true);
   }

   public static ModFileData readModInfo(Path modFile, boolean useModsDatabaseConfig) {
      try {
         ModFileData var9;
         try (JarFile jarFile = new JarFile(modFile.toFile())) {
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
               Constants.LOG.warn("{} ⚠ Unable to read manifest from mod file {}, which is expected in some cases.", "[Mod Data]", modFile);
            }

            ModFileData modFileData = ModFileParser.parseModFile(manifest, modFile, jarFile);
            if (useModsDatabaseConfig && ModsDatabaseConfig.containsMod(modFileData.id())) {
               ModFileData.ModEnvironment modEnvironment = ModsDatabaseConfig.getModEnvironment(modFileData.id());
               if (modEnvironment != modFileData.environment()) {
                  Constants.LOG
                     .info(
                        "{} Overwrite mod environment for {} from {} to {}",
                        new Object[]{"[Mod Data]", modFileData.id(), modFileData.environment(), modEnvironment}
                     );
                  modFileData = new ModFileData(
                     modFileData.path(),
                     modFileData.id(),
                     modFileData.modType(),
                     modFileData.name(),
                     modFileData.version(),
                     modEnvironment,
                     modFileData.timestamp()
                  );
               }
            }

            if (ModsDatabaseConfig.isDebugEnabled()) {
               Constants.LOG.info("{} {}", "[Mod Data]", modFileData);
            }

            var9 = modFileData;
         }

         return var9;
      } catch (Exception var8) {
         Constants.LOG.error("{} ⚠ Unable to read mod file {}:", new Object[]{"[Mod Data]", modFile, var8});
         return null;
      }
   }
}
