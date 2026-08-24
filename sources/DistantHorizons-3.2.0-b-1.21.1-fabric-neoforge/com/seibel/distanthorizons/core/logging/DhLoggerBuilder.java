package com.seibel.distanthorizons.core.logging;

import com.seibel.distanthorizons.api.enums.config.EDhApiLoggerLevel;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import org.jetbrains.annotations.Nullable;

public class DhLoggerBuilder {
   private String name;
   @Nullable
   private ConfigEntry<EDhApiLoggerLevel> chatLevelConfig;
   @Nullable
   private ConfigEntry<EDhApiLoggerLevel> fileLevelConfig;
   private int maxLogPerSec = -1;

   public DhLoggerBuilder() {
      this.name = "DistantHorizons-" + getCallingClassName();
   }

   private static String getCallingClassName() {
      StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
      String callerClassName = "??";

      for (int i = 1; i < stElements.length; i++) {
         StackTraceElement ste = stElements[i];
         if (!ste.getClassName().equals(DhLoggerBuilder.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
            callerClassName = ste.getClassName();
            break;
         }
      }

      return callerClassName;
   }

   public DhLoggerBuilder name(String name) {
      this.name = name;
      return this;
   }

   public DhLoggerBuilder chatLevelConfig(ConfigEntry<EDhApiLoggerLevel> chatLevelConfig) {
      this.chatLevelConfig = chatLevelConfig;
      return this;
   }

   public DhLoggerBuilder fileLevelConfig(ConfigEntry<EDhApiLoggerLevel> fileLevelConfig) {
      this.fileLevelConfig = fileLevelConfig;
      return this;
   }

   public DhLoggerBuilder maxCountPerSecond(int maxLogPerSec) {
      this.maxLogPerSec = maxLogPerSec;
      return this;
   }

   public DhLogger build() {
      try {
         return new DhLogger(this.name, this.chatLevelConfig, this.fileLevelConfig, this.maxLogPerSec);
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }
}
