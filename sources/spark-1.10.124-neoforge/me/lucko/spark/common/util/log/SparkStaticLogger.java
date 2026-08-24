package me.lucko.spark.common.util.log;

import java.util.logging.Level;

public enum SparkStaticLogger {
   private static Logger logger = Logger.FALLBACK;

   public static synchronized void setLogger(Logger logger) {
      if (SparkStaticLogger.logger == null) {
         SparkStaticLogger.logger = logger;
      }
   }

   public static void log(Level level, String msg, Throwable throwable) {
      logger.log(level, msg, throwable);
   }

   public static void log(Level level, String msg) {
      logger.log(level, msg);
   }
}
