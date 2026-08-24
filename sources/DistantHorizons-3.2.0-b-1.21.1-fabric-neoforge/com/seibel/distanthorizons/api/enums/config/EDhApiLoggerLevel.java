package com.seibel.distanthorizons.api.enums.config;

import org.apache.logging.log4j.Level;

public enum EDhApiLoggerLevel {
   ALL(Level.ALL),
   DEBUG(Level.DEBUG),
   INFO(Level.INFO),
   WARN(Level.WARN),
   ERROR(Level.ERROR),
   DISABLED(Level.OFF);

   public final Level level;

   private EDhApiLoggerLevel(Level level) {
      this.level = level;
   }
}
