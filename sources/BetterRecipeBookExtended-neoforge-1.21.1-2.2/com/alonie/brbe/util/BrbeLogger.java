package com.alonie.brbe.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class BrbeLogger {
   public static final String PROPERTY = "brbe.debug";
   private static final boolean ENABLED = "true".equalsIgnoreCase(System.getProperty("brbe.debug"));
   private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static volatile PrintWriter writer;

   private BrbeLogger() {
   }

   public static boolean isEnabled() {
      return ENABLED;
   }

   public static void init(Path gameDir) {
      if (ENABLED && writer == null) {
         Path logsDir = gameDir.resolve("logs");

         try {
            Files.createDirectories(logsDir);
            writer = new PrintWriter(Files.newBufferedWriter(logsDir.resolve("brbe-debug.log"), StandardCharsets.UTF_8), true);
            writer.println("=== BRBE Debug Log ===");
            writer.println("Session: " + Instant.now());
            writer.println();
         } catch (IOException var3) {
            System.err.println("[BrbeLogger] Failed to create log file: " + var3);
         }
      }
   }

   public static void log(BrbeLogger.Category cat, String format, Object... args) {
      if (ENABLED && writer != null) {
         String msg = args.length == 0 ? format : String.format(format, args);
         writer.printf("[%s] [%s] %s%n", TIME_FMT.format(LocalTime.now()), cat, msg);
      }
   }

   public static void log(BrbeLogger.Category cat, String msg, Throwable t) {
      if (ENABLED && writer != null) {
         writer.printf("[%s] [%s] %s%n", TIME_FMT.format(LocalTime.now()), cat, msg);
         t.printStackTrace(writer);
      }
   }

   public static enum Category {
      CONFIG,
      PIPELINE,
      SORT,
      FILTER,
      RENDER,
      STATE,
      VISIBILITY;
   }
}
