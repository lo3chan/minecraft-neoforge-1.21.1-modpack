package pl.skidam.automodpack_core.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import pl.skidam.automodpack_core.GlobalVariables;

public class ConfigUtils {
   public static void normalizeServerConfig(Jsons.ServerConfigFieldsV2 config, boolean saveAfter) {
      normalizeServerConfig(config);
      if (saveAfter) {
         ConfigTools.save(GlobalVariables.serverConfigFile, config);
      }
   }

   public static void normalizeServerConfig(Jsons.ServerConfigFieldsV2 config) {
      Set<String> fixedSyncedFiles = new HashSet<>(config.syncedFiles.size());
      Set<String> fixedAllowEditsInFiles = new HashSet<>(config.allowEditsInFiles.size());
      Set<String> fixedForceCopyFilesToStandardLocation = new HashSet<>(config.forceCopyFilesToStandardLocation.size());
      Map<String, String> fixedNonModpackFilesToDelete = new HashMap<>(config.nonModpackFilesToDelete.size());
      String prefixPattern = "^/?automodpack/host-modpack/[^/]+/";
      Pattern pattern = Pattern.compile(prefixPattern);

      for (String file : config.syncedFiles) {
         if (file == null) {
            GlobalVariables.LOGGER.warn("Ignored null entry in syncedFiles.");
         } else {
            String trimmed = file.trim();
            if (trimmed.isEmpty()) {
               GlobalVariables.LOGGER.warn("Ignored empty entry in syncedFiles.");
            } else if (pattern.matcher(trimmed).find()) {
               GlobalVariables.LOGGER.info("Removed redundant syncedFiles entry '{}': paths under '/automodpack/host-modpack/' are implicitly synced.", file);
            } else {
               fixedSyncedFiles.add(prefixSlash(file));
            }
         }
      }

      for (String filex : config.allowEditsInFiles) {
         if (filex == null) {
            GlobalVariables.LOGGER.warn("Ignored null entry in allowEditsInFiles.");
         } else {
            String trimmed = filex.trim();
            if (trimmed.isEmpty()) {
               GlobalVariables.LOGGER.warn("Ignored empty entry in allowEditsInFiles.");
            } else {
               String fixed = pattern.matcher(trimmed).replaceFirst("");
               if (!fixed.equals(trimmed)) {
                  GlobalVariables.LOGGER.info("Normalized allowEditsInFiles entry: '{}' -> '{}'. Removed '/automodpack/host-modpack/' prefix.", filex, fixed);
               }

               fixedAllowEditsInFiles.add(prefixSlash(fixed));
            }
         }
      }

      for (String filexx : config.forceCopyFilesToStandardLocation) {
         if (filexx == null) {
            GlobalVariables.LOGGER.warn("Ignored null entry in forceCopyFilesToStandardLocation.");
         } else {
            String trimmed = filexx.trim();
            if (trimmed.isEmpty()) {
               GlobalVariables.LOGGER.warn("Ignored empty entry in forceCopyFilesToStandardLocation.");
            } else {
               String fixed = pattern.matcher(trimmed).replaceFirst("");
               if (!fixed.equals(trimmed)) {
                  GlobalVariables.LOGGER
                     .info("Normalized forceCopyFilesToStandardLocation entry: '{}' -> '{}'. Removed '/automodpack/host-modpack/' prefix.", filexx, fixed);
               }

               fixedForceCopyFilesToStandardLocation.add(prefixSlash(fixed));
            }
         }
      }

      for (Entry<String, String> entry : config.nonModpackFilesToDelete.entrySet()) {
         String filexxx = entry.getKey();
         String hash = entry.getValue();
         if (filexxx == null) {
            GlobalVariables.LOGGER.warn("Ignored null key in nonModpackFilesToDelete.");
         } else {
            String trimmed = filexxx.trim();
            if (trimmed.isEmpty()) {
               GlobalVariables.LOGGER.warn("Ignored empty key in nonModpackFilesToDelete.");
            } else {
               String fixed = pattern.matcher(trimmed).replaceFirst("");
               if (!fixed.equals(trimmed)) {
                  GlobalVariables.LOGGER
                     .info("Normalized nonModpackFilesToDelete entry: '{}' -> '{}'. Removed '/automodpack/host-modpack/' prefix.", filexxx, fixed);
               }

               fixedNonModpackFilesToDelete.put(prefixSlash(fixed), hash);
            }
         }
      }

      config.syncedFiles = fixedSyncedFiles;
      config.allowEditsInFiles = fixedAllowEditsInFiles;
      config.forceCopyFilesToStandardLocation = fixedForceCopyFilesToStandardLocation;
      config.nonModpackFilesToDelete = fixedNonModpackFilesToDelete;
   }

   public static String prefixSlash(String path) {
      if (path == null) {
         return null;
      } else if (path.isEmpty()) {
         return path;
      } else if (path.startsWith("/!/")) {
         return path.substring(1);
      } else if (path.startsWith("/")) {
         return path;
      } else if (path.startsWith("!/")) {
         return path;
      } else {
         return path.charAt(0) == '!' ? "!/" + path.substring(1) : "/" + path;
      }
   }
}
