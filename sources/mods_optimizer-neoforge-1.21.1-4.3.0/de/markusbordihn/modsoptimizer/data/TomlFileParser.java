package de.markusbordihn.modsoptimizer.data;

import de.markusbordihn.modsoptimizer.Constants;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.Toml;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class TomlFileParser {
   private static final String LOG_PREFIX = "[TOMLFileParser]";

   private TomlFileParser() {
   }

   public static Toml readTomlFile(JarFile jarFile, Path path) {
      return readTomlFile(jarFile, path, false);
   }

   public static Toml tryReadTomlFile(JarFile jarFile, Path path) {
      return readTomlFile(jarFile, path, true);
   }

   public static Toml readTomlFile(JarFile jarFile, Path path, boolean ignoreErrors) {
      String normalizedPath = normalizePath(path);
      ZipEntry modsFile = jarFile.getEntry(normalizedPath);
      if (modsFile != null && !modsFile.isDirectory()) {
         try {
            Toml var6;
            try (InputStream inputStream = jarFile.getInputStream(modsFile)) {
               var6 = new Toml().read(preprocessToml(inputStream, jarFile.getName()));
            }

            return var6;
         } catch (Exception var10) {
            if (!ignoreErrors) {
               Constants.LOG.error("{} ⚠️ Error reading TOML file {} from {}: {}", new Object[]{"[TOMLFileParser]", path, jarFile, var10});
            }
         }
      } else if (!ignoreErrors) {
         Constants.LOG.error("{} ⚠️ TOML file {} not found in {}", new Object[]{"[TOMLFileParser]", normalizedPath, jarFile});
      }

      return new Toml();
   }

   private static String normalizePath(Path path) {
      return path.toString().replace("\\", "/");
   }

   public static String preprocessToml(InputStream inputStream, String jarFileName) {
      return new BufferedReader(new InputStreamReader(inputStream)).lines().map(line -> {
         String trimmed = line.trim();
         if (!trimmed.startsWith("[") && !trimmed.startsWith("#") && !trimmed.isEmpty()) {
            int equalsIndex = trimmed.indexOf(61);
            if (equalsIndex > 0) {
               String key = trimmed.substring(0, equalsIndex).trim();
               if (key.contains(".") && (!key.startsWith("\"") || !key.endsWith("\""))) {
                  String newKey = key.replace(".", "_");
                  Constants.LOG.warn("{} ⚠️ Found invalid key '{}' in {}", new Object[]{"[TOMLFileParser]", key, jarFileName});
                  return line.replaceFirst(key, newKey);
               }
            }

            return (CharSequence)line;
         } else {
            return (CharSequence)line;
         }
      }).collect(Collectors.joining("\n"));
   }
}
