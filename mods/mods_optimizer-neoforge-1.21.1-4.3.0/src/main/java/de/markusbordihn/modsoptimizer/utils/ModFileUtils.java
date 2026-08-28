/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package de.markusbordihn.modsoptimizer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModFileUtils {
    protected static final Logger log = LogManager.getLogger((String)"Mods Optimizer");
    private static final String LOG_PREFIX = "[Mod File Utils]";

    protected ModFileUtils() {
    }

    public static boolean deleteModFile(Path path) {
        if (path == null) {
            return false;
        }
        return ModFileUtils.deleteModFile(path.toFile());
    }

    public static boolean deleteModFile(File file) {
        if (file == null || file.isDirectory()) {
            log.error("{} \u26a0 Was unable to delete mod file {}, because it's {}!", (Object)LOG_PREFIX, (Object)file, (Object)(file == null ? "null" : "a directory"));
            return false;
        }
        try {
            return Files.deleteIfExists(file.toPath());
        }
        catch (IOException e) {
            log.error("{} \u26a0 Was unable to delete mod file {}, because of: {}", (Object)LOG_PREFIX, (Object)file, (Object)e);
            return false;
        }
    }
}

