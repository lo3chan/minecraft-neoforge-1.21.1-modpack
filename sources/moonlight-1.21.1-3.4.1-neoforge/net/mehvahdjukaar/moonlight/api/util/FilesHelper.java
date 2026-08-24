package net.mehvahdjukaar.moonlight.api.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import org.apache.commons.io.FileUtils;

public class FilesHelper {
   public static FastCachedWriter fastCacheWriter() {
      return new FastCachedWriter();
   }

   public static boolean fastRemove(Path path) {
      if (path != null && Files.exists(path)) {
         try {
            if (Files.isDirectory(path)) {
               Moonlight.LOGGER.info("Deleting directory: {}", path);
               Path tempPath = path.resolveSibling(path.getFileName() + "_temp_deleting_" + System.currentTimeMillis());

               try {
                  Files.move(path, tempPath);
               } catch (IOException var3) {
                  Files.move(path, tempPath);
               }

               new Thread(() -> {
                  try {
                     FileUtils.deleteDirectory(tempPath.toFile());
                  } catch (Exception var2) {
                  }
               }).start();
            } else if (Files.exists(path)) {
               Moonlight.LOGGER.info("Deleting file: {}", path);
               Files.deleteIfExists(path);
            }

            return true;
         } catch (Exception var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   public static void writeAtomically(Path target, FilesHelper.IOConsumer<OutputStream> writeLogic) throws IOException {
      Path dir = target.getParent();
      if (dir == null) {
         dir = Paths.get(System.getProperty("java.io.tmpdir"));
      } else {
         Files.createDirectories(dir);
      }

      Path temp = Files.createTempFile(dir, target.getFileName().toString(), ".tmp");

      try {
         try (OutputStream out = Files.newOutputStream(temp, StandardOpenOption.WRITE)) {
            writeLogic.accept(out);
            out.flush();
         }

         try (FileChannel fc = FileChannel.open(temp, StandardOpenOption.WRITE)) {
            fc.force(true);
         }

         Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

         try (FileChannel dirFc = FileChannel.open(dir, StandardOpenOption.READ)) {
            dirFc.force(true);
         } catch (Exception var13) {
         }
      } catch (Exception var14) {
         Files.deleteIfExists(temp);
         throw var14;
      }
   }

   public static void writeTextAtomically(Path target, FilesHelper.IOConsumer<Writer> writeLogic) throws IOException {
      writeAtomically(target, out -> {
         try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writeLogic.accept(writer);
            writer.flush();
         }
      });
   }

   @FunctionalInterface
   public interface IOConsumer<T> {
      void accept(T var1) throws IOException;
   }
}
