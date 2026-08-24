package me.lucko.spark.common.monitor.disk;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum DiskUsage {
   private static final FileStore FILE_STORE;

   public static long getUsed() {
      if (FILE_STORE == null) {
         return 0L;
      } else {
         try {
            long total = FILE_STORE.getTotalSpace();
            return total - FILE_STORE.getUsableSpace();
         } catch (IOException var2) {
            return 0L;
         }
      }
   }

   public static long getTotal() {
      if (FILE_STORE == null) {
         return 0L;
      } else {
         try {
            return FILE_STORE.getTotalSpace();
         } catch (IOException var1) {
            return 0L;
         }
      }
   }

   static {
      FileStore fileStore = null;

      try {
         fileStore = Files.getFileStore(Paths.get("."));
      } catch (IOException var2) {
      }

      FILE_STORE = fileStore;
   }
}
