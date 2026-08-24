package com.seibel.distanthorizons.core.logging;

import com.seibel.distanthorizons.api.enums.config.EDhApiLoggerLevel;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.Nullable;

public class DhLogger implements IConfigListener {
   private static final List<WeakReference<DhLogger>> LOGGER_REF_LIST = Collections.synchronizedList(new LinkedList<>());
   private static final ThreadPoolExecutor TICKER_THREAD = ThreadUtil.makeSingleDaemonThreadPool("Log Ticker");
   private static final Logger LOGGER = LogManager.getLogger();
   private static IMinecraftClientWrapper mc_client = null;
   private static EDhApiLoggerLevel globalMaxFileLevel = EDhApiLoggerLevel.ALL;
   private static EDhApiLoggerLevel globalMaxChatLevel = EDhApiLoggerLevel.ALL;
   private EDhApiLoggerLevel fileLevel;
   private EDhApiLoggerLevel chatLevel;
   private boolean delayedSetupComplete = false;
   @Nullable
   private final ConfigEntry<EDhApiLoggerLevel> fileLevelConfig;
   @Nullable
   private final ConfigEntry<EDhApiLoggerLevel> chatLevelConfig;
   private final int maxLogCountPerSecond;
   private final AtomicInteger logCountsThisSecondRef = new AtomicInteger(0);
   private final Logger logger;

   public static void runDelayedConfigSetup() {
      LOGGER.info("Applying config to loggers");
      LOGGER_REF_LIST.forEach(loggerRef -> {
         DhLogger logger = loggerRef.get();
         if (logger != null && !logger.delayedSetupComplete) {
            logger.delayedSetupComplete = true;
            Config.Common.Logging.globalChatMaxLevel.addListener(logger);
            Config.Common.Logging.globalFileMaxLevel.addListener(logger);
            logger.onConfigValueSet();
         }
      });
   }

   public DhLogger(
      String loggerName, @Nullable ConfigEntry<EDhApiLoggerLevel> chatLevelConfig, @Nullable ConfigEntry<EDhApiLoggerLevel> fileLevelConfig, int maxLogPerSec
   ) {
      this.logger = LogManager.getLogger("DistantHorizons-" + loggerName);
      this.maxLogCountPerSecond = maxLogPerSec;
      this.chatLevelConfig = chatLevelConfig;
      if (this.chatLevelConfig != null) {
         this.chatLevel = this.chatLevelConfig.get();
         this.chatLevelConfig.addListener(this);
      } else {
         this.chatLevel = EDhApiLoggerLevel.DISABLED;
      }

      this.fileLevelConfig = fileLevelConfig;
      if (this.fileLevelConfig != null) {
         this.fileLevel = this.fileLevelConfig.get();
         this.fileLevelConfig.addListener(this);
      } else {
         this.fileLevel = EDhApiLoggerLevel.ALL;
      }

      LOGGER_REF_LIST.add(new WeakReference<>(this));
   }

   @Override
   public void onConfigValueSet() {
      if (this.fileLevelConfig != null) {
         this.fileLevel = this.fileLevelConfig.get();
      }

      if (this.chatLevelConfig != null) {
         this.chatLevel = this.chatLevelConfig.get();
      }

      globalMaxFileLevel = Config.Common.Logging.globalFileMaxLevel.get();
      globalMaxChatLevel = Config.Common.Logging.globalChatMaxLevel.get();
   }

   public boolean canLog() {
      if (this.fileLevel == EDhApiLoggerLevel.DISABLED && this.chatLevel == EDhApiLoggerLevel.DISABLED) {
         return false;
      } else {
         return globalMaxFileLevel == EDhApiLoggerLevel.DISABLED && globalMaxChatLevel == EDhApiLoggerLevel.DISABLED
            ? false
            : this.maxLogCountPerSecond <= 0 || this.logCountsThisSecondRef.get() < this.maxLogCountPerSecond;
      }
   }

   public void fatal(String str, Object... param) {
      this.log(Level.FATAL, str, param);
   }

   public void error(String str, Object... param) {
      this.log(Level.ERROR, str, param);
   }

   public void warn(String str, Object... param) {
      this.log(Level.WARN, str, param);
   }

   public void info(String str, Object... param) {
      this.log(Level.INFO, str, param);
   }

   public void debug(String str, Object... param) {
      this.log(Level.DEBUG, str, param);
   }

   public void trace(String str, Object... param) {
      this.log(Level.TRACE, str, param);
   }

   public void log(Level level, String str, Object... param) {
      if (this.canLog()) {
         Message msg = this.logger.getMessageFactory().newMessage(str, param);
         String msgStr = msg.getFormattedMessage();
         boolean messageLogged = false;
         if (canLogThisLevel(this.fileLevel.level, globalMaxFileLevel.level, level)) {
            Level logLevel = loggingLevelIsLessSpecificThan(level, Level.INFO) ? Level.INFO : level;
            if (param.length > 0 && param[param.length - 1] instanceof Throwable) {
               this.logger.log(logLevel, msgStr, (Throwable)param[param.length - 1]);
            } else {
               this.logger.log(logLevel, msgStr);
            }

            messageLogged = true;
         }

         if (canLogThisLevel(this.chatLevel.level, globalMaxChatLevel.level, level)) {
            if (mc_client == null) {
               mc_client = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
            }

            if (mc_client != null) {
               this.logToChat(level, msgStr);
               messageLogged = true;
            }
         }

         if (messageLogged) {
            this.logCountsThisSecondRef.incrementAndGet();
         }
      }
   }

   private static boolean canLogThisLevel(Level thisLoggingLevel, Level thisGlobalLoggingLevel, Level requestedLogLevel) {
      return thisLoggingLevel.intLevel() >= requestedLogLevel.intLevel() && thisGlobalLoggingLevel.intLevel() >= requestedLogLevel.intLevel();
   }

   private static boolean loggingLevelIsLessSpecificThan(Level thisLoggingLevel, Level requestedLogLevel) {
      return thisLoggingLevel.intLevel() >= requestedLogLevel.intLevel();
   }

   private void logToChat(Level logLevel, String message) {
      String prefix = "[Distant Horizons] ";
      if (logLevel == Level.ERROR) {
         prefix = prefix + "§4";
      } else if (logLevel == Level.WARN) {
         prefix = prefix + "§6";
      } else if (logLevel == Level.INFO) {
         prefix = prefix + "§b";
      } else if (logLevel == Level.DEBUG) {
         prefix = prefix + "§a";
      } else if (logLevel == Level.TRACE) {
         prefix = prefix + "§8";
      } else {
         prefix = prefix + "§f";
      }

      prefix = prefix + "§l§f";
      prefix = prefix + logLevel.name();
      prefix = prefix + "§r ";
      mc_client.sendChatMessage(prefix + message);
   }

   private static void runTickerLoop() {
      while (true) {
         try {
            Thread.sleep(1000L);
            LOGGER_REF_LIST.removeIf(logger -> {
               boolean loggerGarbageCollected = logger.get() == null;
               if (loggerGarbageCollected) {
                  LOGGER.warn("Logger garbage collected. Loggers should only be created in static contexts otherwise memory leaks may occur.");
               }

               return loggerGarbageCollected;
            });
            LOGGER_REF_LIST.forEach(loggerRef -> {
               DhLogger logger = loggerRef.get();
               if (logger != null) {
                  logger.logCountsThisSecondRef.set(0);
               }
            });
         } catch (Exception var1) {
            LOGGER.error("Unexpected error in ticker thread: [" + var1.getMessage() + "].", var1);
         }
      }
   }

   static {
      TICKER_THREAD.execute(() -> runTickerLoop());
   }
}
