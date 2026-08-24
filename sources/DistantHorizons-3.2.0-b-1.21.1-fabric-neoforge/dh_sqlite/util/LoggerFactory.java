package dh_sqlite.util;

import java.util.function.Supplier;
import java.util.logging.Level;

public class LoggerFactory {
   static final boolean USE_SLF4J;

   public static Logger getLogger(Class<?> hostClass) {
      return (Logger)(USE_SLF4J ? new LoggerFactory.SLF4JLogger(hostClass) : new LoggerFactory.JDKLogger(hostClass));
   }

   static {
      boolean useSLF4J;
      try {
         Class.forName("DistantHorizons.libraries.slf4j.Logger");
         useSLF4J = true;
      } catch (Exception var2) {
         useSLF4J = false;
      }

      USE_SLF4J = useSLF4J;
   }

   private static class JDKLogger implements Logger {
      final java.util.logging.Logger logger;

      public JDKLogger(Class<?> hostClass) {
         this.logger = java.util.logging.Logger.getLogger(hostClass.getCanonicalName());
      }

      @Override
      public void trace(Supplier<String> message) {
         if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.log(Level.FINEST, message.get());
         }
      }

      @Override
      public void info(Supplier<String> message) {
         if (this.logger.isLoggable(Level.INFO)) {
            this.logger.log(Level.INFO, message.get());
         }
      }

      @Override
      public void warn(Supplier<String> message) {
         if (this.logger.isLoggable(Level.WARNING)) {
            this.logger.log(Level.WARNING, message.get());
         }
      }

      @Override
      public void error(Supplier<String> message, Throwable t) {
         if (this.logger.isLoggable(Level.SEVERE)) {
            this.logger.log(Level.SEVERE, message.get(), t);
         }
      }
   }

   private static class SLF4JLogger implements Logger {
      final DistantHorizons.libraries.slf4j.Logger logger;

      SLF4JLogger(Class<?> hostClass) {
         this.logger = DistantHorizons.libraries.slf4j.LoggerFactory.getLogger(hostClass);
      }

      @Override
      public void trace(Supplier<String> message) {
         if (this.logger.isTraceEnabled()) {
            this.logger.trace(message.get());
         }
      }

      @Override
      public void info(Supplier<String> message) {
         if (this.logger.isInfoEnabled()) {
            this.logger.info(message.get());
         }
      }

      @Override
      public void warn(Supplier<String> message) {
         if (this.logger.isWarnEnabled()) {
            this.logger.warn(message.get());
         }
      }

      @Override
      public void error(Supplier<String> message, Throwable t) {
         if (this.logger.isErrorEnabled()) {
            this.logger.error(message.get(), t);
         }
      }
   }
}
