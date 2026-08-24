package pl.skidam.automodpack_loader_core.compat.crashassistant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import pl.skidam.automodpack_core.GlobalVariables;

public final class ProcessSignalIO {
   private static final Path BASE_DIR = Paths.get("local", "crash_assistant");

   public static long getCurrentProcessId() {
      return ProcessHandle.current().pid();
   }

   public static void post(String name, String data) {
      if (Files.exists(BASE_DIR)) {
         String fileName = name + "_pid" + getCurrentProcessId() + ".tmp";
         Path filePath = BASE_DIR.resolve(fileName);

         try {
            Files.writeString(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
         } catch (IOException var5) {
            GlobalVariables.LOGGER.error("Error while saving data to {}", fileName, var5);
         }
      }
   }

   public static void post(String name) {
      post(name, Long.toString(System.currentTimeMillis()));
   }

   public static void postAsOtherProcess(String name, String data, long pid) {
      if (Files.exists(BASE_DIR)) {
         String fileName = name + "_pid" + pid + ".tmp";
         Path filePath = BASE_DIR.resolve(fileName);

         try {
            Files.writeString(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
         } catch (IOException var7) {
            GlobalVariables.LOGGER.error("Error while saving data to {}", fileName, var7);
         }
      }
   }

   public static void postAsOtherProcess(String name, long pid) {
      postAsOtherProcess(name, Long.toString(System.currentTimeMillis()), pid);
   }

   public static Optional<String> get(String name, long pid) {
      String fileName = name + "_pid" + pid + ".tmp";
      Path filePath = BASE_DIR.resolve(fileName);
      if (!Files.isReadable(filePath)) {
         return Optional.empty();
      } else {
         try {
            byte[] bytes = Files.readAllBytes(filePath);
            String content = new String(bytes, StandardCharsets.UTF_8);
            return Optional.of(content);
         } catch (IOException var7) {
            GlobalVariables.LOGGER.error("Error while reading data from {}", fileName, var7);
            return Optional.empty();
         }
      }
   }

   public static boolean exists(String name, long pid) {
      String fileName = name + "_pid" + pid + ".tmp";
      Path filePath = BASE_DIR.resolve(fileName);
      return Files.isRegularFile(filePath);
   }

   public static void postInfo(String name, String data) {
      if (Files.exists(BASE_DIR)) {
         String fileName = name + ".info";
         Path filePath = BASE_DIR.resolve(fileName);

         try {
            Files.writeString(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
         } catch (IOException var5) {
            GlobalVariables.LOGGER.error("Error while saving info to {}", fileName, var5);
         }
      }
   }

   public static Optional<String> getInfo(String name) {
      String fileName = name + ".info";
      Path filePath = BASE_DIR.resolve(fileName);
      if (!Files.isReadable(filePath)) {
         return Optional.empty();
      } else {
         try {
            byte[] bytes = Files.readAllBytes(filePath);
            String content = new String(bytes, StandardCharsets.UTF_8);
            return Optional.of(content);
         } catch (IOException var5) {
            GlobalVariables.LOGGER.error("Error while reading info from {}", fileName, var5);
            return Optional.empty();
         }
      }
   }

   public static boolean existsInfo(String name) {
      String fileName = name + ".info";
      Path filePath = BASE_DIR.resolve(fileName);
      return Files.isRegularFile(filePath);
   }
}
