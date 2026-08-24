package de.markusbordihn.modsoptimizer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModFileUtils {
   protected static final Logger log = LogManager.getLogger("Mods Optimizer");
   private static final String LOG_PREFIX = "[Mod File Utils]";

   protected ModFileUtils() {
   }

   public static boolean deleteModFile(Path path) {
      return path == null ? false : deleteModFile(path.toFile());
   }

   public static boolean deleteModFile(File file) {
      if (file != null && !file.isDirectory()) {
         try {
            return Files.deleteIfExists(file.toPath());
         } catch (IOException var2) {
            log.error("{} ⚠ Was unable to delete mod file {}, because of: {}", "[Mod File Utils]", file, var2);
            return false;
         }
      } else {
         log.error("{} ⚠ Was unable to delete mod file {}, because it's {}!", "[Mod File Utils]", file, file == null ? "null" : "a directory");
         return false;
      }
   }
}
