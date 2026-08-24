package de.markusbordihn.modsoptimizer.utils;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.data.ModFileData;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DuplicatedModsUtils {
   protected DuplicatedModsUtils() {
   }

   public static void optimize(Map<String, Set<ModFileData>> duplicatedMods) {
      if (duplicatedMods != null && !duplicatedMods.isEmpty()) {
         Constants.LOG.info("♻ Optimizing Duplicated Mods ...");

         for (Entry<String, Set<ModFileData>> duplicatedMod : duplicatedMods.entrySet()) {
            String modName = duplicatedMod.getKey();
            Set<ModFileData> modFiles = duplicatedMod.getValue();
            ModFileData latestModFile = null;

            for (ModFileData modFile : modFiles) {
               if (latestModFile == null || modFile.version().greaterThan(latestModFile.version())) {
                  latestModFile = modFile;
               } else if (modFile.version().equals(latestModFile.version())) {
                  String modFileName = modFile.path().getFileName().toString().toLowerCase(Locale.ROOT);
                  String latestModFileName = latestModFile.path().getFileName().toString().toLowerCase(Locale.ROOT);
                  if (latestModFileName.contains("copy") && !modFileName.contains("copy")
                     || latestModFileName.contains("kopie") && !modFileName.contains("kopie")
                     || latestModFileName.length() > modFileName.length()) {
                     latestModFile = modFile;
                  }
               }
            }

            Constants.LOG.warn("⚠ Found {} duplicated Mods with mod id {}: {}", new Object[]{modFiles.size(), modName, modFiles});
            Constants.LOG.info("✔ Will keep most recent Mod: {}", latestModFile);

            for (ModFileData modFilex : modFiles) {
               if (modFilex != latestModFile && !ModFileUtils.deleteModFile(modFilex.path())) {
                  Constants.LOG.error("⚠ Was unable to remove outdated mod {}!", modFilex);
               }
            }
         }
      }
   }
}
