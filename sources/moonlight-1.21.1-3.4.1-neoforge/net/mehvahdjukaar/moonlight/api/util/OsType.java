package net.mehvahdjukaar.moonlight.api.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public enum OsType {
   WINDOWS("windows"),
   MAC("macos"),
   LINUX("linux");

   private static final OsType CURRENT = detect();
   private static final List<String> EXTRA_MAC_BIN_DIRS = List.of("/opt/homebrew/bin", "/usr/local/bin");
   private final String key;

   private OsType(String key) {
      this.key = key;
   }

   public static OsType current() {
      return CURRENT;
   }

   public String key() {
      return this.key;
   }

   public boolean isWindows() {
      return this == WINDOWS;
   }

   public boolean isMac() {
      return this == MAC;
   }

   public boolean isLinux() {
      return this == LINUX;
   }

   public boolean requiresExecutableBit() {
      return this != WINDOWS;
   }

   public String executableName(String baseName) {
      return this == WINDOWS ? baseName + ".exe" : baseName;
   }

   @Nullable
   public Path findExecutable(String baseName) {
      String fileName = this.executableName(baseName);

      for (String dir : this.executableSearchDirs()) {
         if (!dir.isEmpty()) {
            try {
               Path candidate = Paths.get(dir).resolve(fileName);
               if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
                  return candidate.toAbsolutePath();
               }
            } catch (Exception var6) {
            }
         }
      }

      return null;
   }

   private List<String> executableSearchDirs() {
      List<String> dirs = new ArrayList<>();
      String pathEnv = System.getenv("PATH");
      if (pathEnv != null && !pathEnv.isEmpty()) {
         Collections.addAll(dirs, pathEnv.split(File.pathSeparator));
      }

      if (this.isMac()) {
         dirs.addAll(EXTRA_MAC_BIN_DIRS);
      }

      return dirs;
   }

   private static OsType detect() {
      String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (os.contains("win")) {
         return WINDOWS;
      } else {
         return !os.contains("mac") && !os.contains("darwin") ? LINUX : MAC;
      }
   }
}
