package com.seibel.distanthorizons.coreapi.util.jar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class DeleteOnUnlock {
   public static int SUCCESS_EXIT_CODE = 0;
   public static int FAIL_EXIT_CODE = 1;
   public static int ERROR_EXIT_CODE = 2;
   private static final int ATTEMPT_SPEED_IN_MS = 100;
   private static final int TIMEOUT_IN_MINUTES = 60;
   private static FileWriter logFileWriter;

   public static void main(String[] args) {
      String filePathToDelete = args[0];
      String logFilePath = null;
      if (args.length >= 2) {
         logFilePath = args[1];
      }

      try {
         if (logFilePath != null && logFilePath.trim().length() != 0) {
            File logFile = new File(logFilePath);
            logFileWriter = new FileWriter(logFile, true);

            try {
               if (!logFile.createNewFile() && !logFile.exists()) {
                  System.err.println("Unable to create log file at: [" + logFile.getPath() + "]");
               }
            } catch (IOException var9) {
               System.err.println(var9.getMessage());
            }
         }

         File fileToDelete = new File(URLDecoder.decode(filePathToDelete, "UTF-8"));
         log("starting deletion loop... Attempting to delete: [" + fileToDelete.getPath() + "].");

         for (int i = 0; i < 36000.0F; i++) {
            log("delete attempt [" + i + "]");
            if (fileToDelete.exists() && fileToDelete.renameTo(fileToDelete)) {
               try {
                  Files.delete(fileToDelete.toPath());
                  if (!fileToDelete.exists()) {
                     log("success");
                     break;
                  }

                  log("failed to delete without error");
               } catch (NoSuchFileException var10) {
                  log("no file found");
                  break;
               } catch (Exception var11) {
                  log("failed to delete with error: " + var11.getMessage());
               }
            }

            TimeUnit.MILLISECONDS.sleep(100L);
         }

         boolean programSuccess = !fileToDelete.exists();
         log("delete program completed " + (programSuccess ? "successfully" : "unsuccessfully"));
         System.exit(programSuccess ? SUCCESS_EXIT_CODE : FAIL_EXIT_CODE);
      } catch (Exception var12) {
         String stackTrace = "";

         for (StackTraceElement stackTraceElement : var12.getStackTrace()) {
            stackTrace = stackTrace + stackTraceElement.toString() + "\n";
         }

         String message = "Unexpected exception occurred: " + var12.getMessage() + "\n\n" + stackTrace;
         log(message);
         System.err.println(message);
         System.exit(ERROR_EXIT_CODE);
      }
   }

   private static void log(String message) {
      if (logFileWriter != null) {
         try {
            String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
               + " "
               + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
            logFileWriter.write(localDateTime + " - " + message + "\n");
            logFileWriter.flush();
         } catch (IOException var2) {
            System.err.println("Error writing to log: " + var2.getMessage());
            var2.printStackTrace();
         }
      }
   }
}
