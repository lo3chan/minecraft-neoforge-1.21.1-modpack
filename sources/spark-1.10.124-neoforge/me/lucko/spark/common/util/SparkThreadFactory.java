package me.lucko.spark.common.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import me.lucko.spark.common.util.log.SparkStaticLogger;

public class SparkThreadFactory implements ThreadFactory {
   public static final UncaughtExceptionHandler EXCEPTION_HANDLER = (t, e) -> SparkStaticLogger.log(
      Level.SEVERE, "Uncaught exception thrown by thread " + t.getName(), e
   );
   private static final AtomicInteger poolNumber = new AtomicInteger(1);
   private final AtomicInteger threadNumber = new AtomicInteger(1);
   private final String namePrefix = "spark-worker-pool-" + poolNumber.getAndIncrement() + "-thread-";

   @Override
   public Thread newThread(Runnable r) {
      Thread t = new Thread(r, this.namePrefix + this.threadNumber.getAndIncrement());
      t.setUncaughtExceptionHandler(EXCEPTION_HANDLER);
      t.setDaemon(true);
      return t;
   }
}
