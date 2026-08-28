/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.utils;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.data.ModFileData;
import de.markusbordihn.modsoptimizer.utils.ModFileUtils;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DuplicatedModsUtils {
    protected DuplicatedModsUtils() {
    }

    public static void optimize(Map<String, Set<ModFileData>> duplicatedMods) {
        if (duplicatedMods == null || duplicatedMods.isEmpty()) {
            return;
        }
        Constants.LOG.info("\u267b Optimizing Duplicated Mods ...");
        for (Map.Entry<String, Set<ModFileData>> duplicatedMod : duplicatedMods.entrySet()) {
            String modName = duplicatedMod.getKey();
            Set<ModFileData> modFiles = duplicatedMod.getValue();
            ModFileData latestModFile = null;
            for (ModFileData modFile : modFiles) {
                if (latestModFile == null || modFile.version().greaterThan(latestModFile.version())) {
                    latestModFile = modFile;
                    continue;
                }
                if (!modFile.version().equals(latestModFile.version())) continue;
                String modFileName = modFile.path().getFileName().toString().toLowerCase(Locale.ROOT);
                String latestModFileName = latestModFile.path().getFileName().toString().toLowerCase(Locale.ROOT);
                if (!(latestModFileName.contains("copy") && !modFileName.contains("copy") || latestModFileName.contains("kopie") && !modFileName.contains("kopie")) && latestModFileName.length() <= modFileName.length()) continue;
                latestModFile = modFile;
            }
            Constants.LOG.warn("\u26a0 Found {} duplicated Mods with mod id {}: {}", new Object[]{modFiles.size(), modName, modFiles});
            Constants.LOG.info("\u2714 Will keep most recent Mod: {}", (Object)latestModFile);
            for (ModFileData modFile : modFiles) {
                if (modFile == latestModFile || ModFileUtils.deleteModFile(modFile.path())) continue;
                Constants.LOG.error("\u26a0 Was unable to remove outdated mod {}!", (Object)modFile);
            }
        }
    }
}

