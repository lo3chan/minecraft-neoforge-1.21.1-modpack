package com.seibel.distanthorizons.core.util.threading;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RateLimitedThreadPoolExecutor extends ThreadPoolExecutor {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public final ConfigEntry<Double> runTimeRatioConfig = Config.Common.MultiThreading.threadRunTimeRatio;
   private final ThreadLocal<Long> runStartTime = ThreadLocal.withInitial(() -> -1L);

   public RateLimitedThreadPoolExecutor(int poolSize, ThreadFactory threadFactory, BlockingQueue<Runnable> workQueue) {
      super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, workQueue, threadFactory);
   }

   @Override
   protected void beforeExecute(Thread thread, Runnable runnable) {
      this.runStartTime.set(System.nanoTime());
      super.beforeExecute(thread, runnable);
   }

   @Override
   protected void afterExecute(Runnable runnable, Throwable throwable) {
      super.afterExecute(runnable, throwable);
      double ratio = this.runTimeRatioConfig.get();
      if (!(ratio >= 1.0)) {
         try {
            long runTime = System.nanoTime() - this.runStartTime.get();
            Thread.sleep(TimeUnit.NANOSECONDS.toMillis((long)(runTime / ratio - runTime)));
         } catch (InterruptedException var7) {
         }
      }
   }

   @Override
   protected void terminated() {
      super.terminated();
   }

   @Deprecated
   @Override
   public void purge() {
      super.purge();
   }
}
