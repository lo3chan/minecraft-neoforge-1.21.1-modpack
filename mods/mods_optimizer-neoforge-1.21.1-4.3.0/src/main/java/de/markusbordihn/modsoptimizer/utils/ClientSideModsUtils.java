/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.utils;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.data.ModFileData;
import de.markusbordihn.modsoptimizer.utils.ModFileUtils;
import java.io.File;
import java.util.Set;

public class ClientSideModsUtils {
    public static final String CLIENT_MOD_EXTENSION = ".client";
    private static final String LOG_PREFIX = "[Client Side Mod]";

    protected ClientSideModsUtils() {
    }

    public static int enable(File modPath) {
        int result = 0;
        if (modPath == null || !modPath.exists()) {
            Constants.LOG.error("{} unable to find valid mod path: {}", (Object)LOG_PREFIX, (Object)modPath);
            return result;
        }
        File[] modsFiles = modPath.listFiles();
        if (modsFiles == null || modsFiles.length == 0) {
            Constants.LOG.error("{} unable to find valid mod files in path: {}", (Object)LOG_PREFIX, (Object)modPath);
            return result;
        }
        for (File modFile : modsFiles) {
            String modFileName = modFile.getName();
            if (!modFileName.endsWith(CLIENT_MOD_EXTENSION)) continue;
            File clientFile = new File(modFile.getAbsoluteFile().toString().replace(".jar.client", ".jar"));
            Constants.LOG.info("{} \u2714 Try to enable client side mod {} ...", (Object)LOG_PREFIX, (Object)modFileName);
            if (clientFile.exists()) {
                if (!ModFileUtils.deleteModFile(modFile)) {
                    Constants.LOG.error("{} \u26a0 Was unable to remove duplicated client side mod {}!", (Object)LOG_PREFIX, (Object)modFile);
                    continue;
                }
                Constants.LOG.info("{} \u2714 Removed duplicated client side mod {}!", (Object)LOG_PREFIX, (Object)modFile);
                ++result;
                continue;
            }
            if (!modFile.renameTo(clientFile)) {
                Constants.LOG.error("{} \u26a0 Was unable to enable client side mod {}!", (Object)LOG_PREFIX, (Object)modFile);
                continue;
            }
            Constants.LOG.info("{} \u2714 Enabled client side mod {}!", (Object)LOG_PREFIX, (Object)modFileName);
            ++result;
        }
        return result;
    }

    public static int disable(Set<ModFileData> modFiles) {
        int result = 0;
        if (modFiles == null || modFiles.isEmpty()) {
            return result;
        }
        for (ModFileData modFileData : modFiles) {
            if (modFileData.environment() == ModFileData.ModEnvironment.CLIENT) {
                File modFile = modFileData.path().toFile();
                File clientFile = new File(String.valueOf(modFile.getAbsoluteFile()) + CLIENT_MOD_EXTENSION);
                Constants.LOG.info("{} \u274c Try to disable client side mod {} ...", (Object)LOG_PREFIX, (Object)modFileData.id());
                if (clientFile.exists()) {
                    if (!ModFileUtils.deleteModFile(clientFile)) {
                        Constants.LOG.error("{} \u26a0 Was unable to remove client side mod {} with {}!", new Object[]{LOG_PREFIX, modFileData.id(), clientFile});
                        continue;
                    }
                    Constants.LOG.info("{} \u2714 Removed duplicated client side mod {} with {}!", new Object[]{LOG_PREFIX, modFileData.id(), clientFile});
                    ++result;
                    continue;
                }
                if (!modFile.renameTo(clientFile)) {
                    Constants.LOG.error("{} \u26a0 Was unable to disable client side mod {}!", (Object)LOG_PREFIX, (Object)modFile);
                    continue;
                }
                Constants.LOG.info("{} \u2714 Disabled client side mod {}!", (Object)LOG_PREFIX, (Object)modFileData.id());
                ++result;
                continue;
            }
            Constants.LOG.info("{} \u274c Skip wrongly client side mod {} with {}!", new Object[]{LOG_PREFIX, modFileData.id(), modFileData.environment()});
        }
        return result;
    }
}

