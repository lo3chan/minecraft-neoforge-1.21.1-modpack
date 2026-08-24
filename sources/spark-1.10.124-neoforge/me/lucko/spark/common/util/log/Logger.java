package me.lucko.spark.common.util.log;

import java.util.logging.Level;

public interface Logger {
   Logger FALLBACK = new Logger() {
      @Override
      public void log(Level level, String msg) {
         if (level.intValue() >= 1000) {
            System.err.println(msg);
         } else {
            System.out.println(msg);
         }
      }

      @Override
      public void log(Level level, String msg, Throwable throwable) {
         if (Logger.isSevere(level)) {
            System.err.println(msg);
            if (throwable != null) {
               throwable.printStackTrace(System.err);
            }
         } else {
            System.out.println(msg);
            if (throwable != null) {
               throwable.printStackTrace(System.out);
            }
         }
      }
   };

   void log(Level var1, String var2);

   void log(Level var1, String var2, Throwable var3);

   static boolean isSevere(Level level) {
      return level.intValue() >= 1000;
   }

   static boolean isWarning(Level level) {
      return level.intValue() >= 900;
   }
}
