package net.mehvahdjukaar.moonlight.api.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FastCachedWriter {
   private final Set<Path> dirCache = ConcurrentHashMap.newKeySet();

   public void writeFast(Path filePath, byte[] bytes) throws IOException {
      Path parent = filePath.getParent();
      Path normParent = parent == null ? null : parent.toAbsolutePath().normalize();
      if (normParent != null && this.dirCache.add(normParent)) {
         Files.createDirectories(normParent);
      }

      int attempts = 0;

      while (true) {
         try {
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return;
         } catch (NoSuchFileException var7) {
            if (normParent != null) {
               if (++attempts <= 2) {
                  Files.createDirectories(normParent);
                  this.dirCache.add(normParent);
                  continue;
               }
            }

            throw var7;
         }
      }
   }

   public void clear() {
      this.dirCache.clear();
   }
}
