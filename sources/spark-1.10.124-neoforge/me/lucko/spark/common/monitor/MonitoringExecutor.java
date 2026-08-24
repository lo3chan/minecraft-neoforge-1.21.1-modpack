package me.lucko.spark.common.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import me.lucko.spark.common.util.SparkThreadFactory;

public enum MonitoringExecutor {
   public static final ScheduledExecutorService INSTANCE = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread thread = Executors.defaultThreadFactory().newThread(r);
      thread.setName("spark-monitoring-thread");
      thread.setUncaughtExceptionHandler(SparkThreadFactory.EXCEPTION_HANDLER);
      thread.setDaemon(true);
      return thread;
   });
}
