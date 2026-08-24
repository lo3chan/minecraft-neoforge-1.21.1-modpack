package pl.skidam.automodpack_core.utils;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FileSystemCapabilities {
   private static final Map<FileSystem, Boolean> SENSITIVITY_CACHE = new ConcurrentHashMap<>();

   private FileSystemCapabilities() {
   }

   public static boolean isCaseInsensitive() {
      return isCaseInsensitive(FileSystems.getDefault());
   }

   public static boolean isCaseInsensitive(FileSystem fs) {
      return fs == null ? false : SENSITIVITY_CACHE.computeIfAbsent(fs, FileSystemCapabilities::probeSensitivity);
   }

   private static boolean probeSensitivity(FileSystem fs) {
      try {
         Path p1 = fs.getPath("A");
         Path p2 = fs.getPath("a");
         return p1.equals(p2) ? true : p1.hashCode() == p2.hashCode();
      } catch (Exception var3) {
         return false;
      }
   }
}
