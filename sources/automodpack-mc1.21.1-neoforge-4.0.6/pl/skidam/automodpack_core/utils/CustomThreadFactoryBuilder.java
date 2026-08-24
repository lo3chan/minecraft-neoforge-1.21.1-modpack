package pl.skidam.automodpack_core.utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactoryBuilder {
   private String nameFormat;
   private boolean daemon;
   private int priority;
   private UncaughtExceptionHandler uncaughtExceptionHandler;
   private ThreadFactory backingThreadFactory;

   public CustomThreadFactoryBuilder setNameFormat(String nameFormat) {
      this.nameFormat = nameFormat;
      return this;
   }

   public CustomThreadFactoryBuilder setDaemon(boolean daemon) {
      this.daemon = daemon;
      return this;
   }

   public CustomThreadFactoryBuilder setPriority(int priority) {
      this.priority = priority;
      return this;
   }

   public CustomThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
      this.uncaughtExceptionHandler = uncaughtExceptionHandler;
      return this;
   }

   public CustomThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
      this.backingThreadFactory = backingThreadFactory;
      return this;
   }

   public ThreadFactory build() {
      return new CustomThreadFactoryBuilder.CustomThreadFactory(
         this.nameFormat, this.daemon, this.priority, this.uncaughtExceptionHandler, this.backingThreadFactory
      );
   }

   private static class CustomThreadFactory implements ThreadFactory {
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String nameFormat;
      private final boolean daemon;
      private final int priority;
      private final UncaughtExceptionHandler uncaughtExceptionHandler;
      private final ThreadFactory backingThreadFactory;

      private CustomThreadFactory(
         String nameFormat, boolean daemon, int priority, UncaughtExceptionHandler uncaughtExceptionHandler, ThreadFactory backingThreadFactory
      ) {
         this.nameFormat = nameFormat;
         this.daemon = daemon;
         this.priority = priority;
         this.uncaughtExceptionHandler = uncaughtExceptionHandler;
         this.backingThreadFactory = backingThreadFactory;
      }

      @Override
      public Thread newThread(Runnable runnable) {
         Thread thread;
         if (this.backingThreadFactory != null) {
            thread = this.backingThreadFactory.newThread(runnable);
         } else {
            thread = new Thread(runnable);
         }

         if (this.nameFormat != null) {
            thread.setName(String.format(this.nameFormat, this.threadNumber.getAndIncrement()));
         }

         thread.setDaemon(this.daemon);
         if (this.priority != 0) {
            thread.setPriority(this.priority);
         } else {
            thread.setPriority(5);
         }

         thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
         return thread;
      }
   }
}
