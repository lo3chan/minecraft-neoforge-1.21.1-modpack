package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class FileUtil {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();

   public static File renameCorruptedFile(File file) {
      String corruptedFileName = file.getName() + ".corrupted";
      File corruptedFile = new File(file.getParentFile(), corruptedFileName);
      if (corruptedFile.exists() && !corruptedFile.delete()) {
         LOGGER.error("Unable to delete pre-existing corrupted file [" + corruptedFileName + "].");
      }

      if (file.exists()) {
         if (file.renameTo(corruptedFile)) {
            LOGGER.error("Renamed corrupted file to [" + corruptedFileName + "].");
         } else {
            LOGGER.error("Failed to rename corrupted file to [" + corruptedFileName + "]. Attempting to delete file...");
            if (!file.delete()) {
               LOGGER.error("Unable to delete corrupted file [" + corruptedFileName + "].");
            }
         }
      } else {
         LOGGER.error("Corrupted file [" + file + "] doesn't exist.");
      }

      return corruptedFile;
   }

   public static String readFile(File file, Charset encoding) throws IOException {
      byte[] encoded = Files.readAllBytes(file.toPath());
      return new String(encoded, encoding);
   }
}
