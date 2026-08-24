package de.cristelknight.cristellib.util;

import com.mojang.datafixers.util.Pair;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.config.ConfigManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.ResourceLocationException;

public class FileHelper {
   public static Path janksonPathFromString(String path, String name) {
      return pathFromString(path).resolve(name + ".json5");
   }

   public static Path pathFromString(String path) {
      return path.startsWith("<CONFIG_DIR>/") ? ConfigManager.CONFIG_DIR.resolve(path.replace("<CONFIG_DIR>/", "")) : Path.of(path);
   }

   public static String fileName(Path path) {
      if (path == null) {
         throw new IllegalArgumentException(Constants.getWithPrefix("Path to get filename from is null!"));
      } else {
         Path file = path.getFileName();
         if (file == null) {
            throw new IllegalArgumentException(Constants.getWithPrefix("Path cannot have zero elements!"));
         } else {
            return cutFileType(file);
         }
      }
   }

   public static String cutFileType(Path path) {
      if (path == null) {
         throw new IllegalArgumentException(Constants.getWithPrefix("Path to cut filename from is null!"));
      } else {
         String fileName = path.toString();
         int dotIndex = fileName.lastIndexOf(46);
         return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
      }
   }

   public static String normalizeResourcePath(String path) {
      if (path == null) {
         throw new IllegalArgumentException(Constants.getWithPrefix("Path to normalize is null!"));
      } else {
         String normalized = path.replace('\\', '/');
         if (!normalized.isEmpty() && normalized.charAt(0) == '/') {
            normalized = normalized.substring(1);
         }

         while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
         }

         return normalized;
      }
   }

   public static void renameFile(Path path, String newBaseName) {
      String filename = path.getFileName().toString();
      int dotIndex = filename.lastIndexOf(46);
      String ext = dotIndex == -1 ? "" : filename.substring(dotIndex);
      Path newPath = path.resolveSibling(newBaseName + ext);

      try {
         Files.move(path, newPath);
      } catch (IOException var7) {
         Constants.LOG.error("Failed to rename file at path: {}, to: {}", path, newBaseName, var7);
      }
   }

   public static Pair<String, String> parseNamespaceAndPath(String fileName, char separator) throws IllegalArgumentException {
      int sepIndex = fileName.indexOf(separator);
      if (sepIndex >= 1 && sepIndex != fileName.length() - 1) {
         String namespace = fileName.substring(0, sepIndex);
         String path = fileName.substring(sepIndex + 1);
         return new Pair(namespace, path);
      } else {
         throw new ResourceLocationException("Invalid file name: " + fileName + ", missing or misplaced separator '" + separator + "'");
      }
   }
}
