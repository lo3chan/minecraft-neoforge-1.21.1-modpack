package com.seibel.distanthorizons.core.util.threading;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PriorityTaskPicker {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(
      1, new DhThreadFactory("Task Picker Re-queue Schedule", 5, true)
   );
   private static final int MS_TO_CHECK_ON_PAUSED_THREADS = 1000;
   private final AtomicReference<ScheduledFuture<?>> scheduledFutureRef = new AtomicReference<>();
   private final Runnable startNextTaskBlockingRunnable = () -> this.startNextTask(true);
   private final ArrayList<PriorityTaskPicker.Executor> executors = new ArrayList<>();
   private final ReentrantLock taskPickerLock = new ReentrantLock();
   private final AtomicInteger occupiedThreadsRef = new AtomicInteger(0);
   private final AtomicBoolean isShutDownRef = new AtomicBoolean(false);

   public PriorityTaskPicker.Executor createExecutor(String name) {
      return this.createExecutor(name, null);
   }

   public PriorityTaskPicker.Executor createExecutor(String name, @Nullable PriorityTaskPicker.IExecutorCanRunFunc canRunFunc) {
      PriorityTaskPicker.Executor executor = new PriorityTaskPicker.Executor(this, name, canRunFunc);
      this.executors.add(executor);
      return executor;
   }

   private void tryStartNextTask() {
      this.startNextTask(false);
   }

   private void startNextTask(boolean waitForLock) {
      if (waitForLock) {
         this.taskPickerLock.lock();
      } else if (!this.taskPickerLock.tryLock()) {
         return;
      }

      try {
         boolean executorPaused = false;
         Iterator<PriorityTaskPicker.Executor> iterator = this.getExecutorIteratorSortedByShortestTotalRunTime();

         while (iterator.hasNext()) {
            PriorityTaskPicker.Executor executor = iterator.next();
            PriorityTaskPicker.TrackedRunnable task;
            if (!executor.canRun()) {
               executorPaused = true;
            } else {
               while (this.occupiedThreadsRef.get() < Config.Common.MultiThreading.numberOfThreads.get() && (task = executor.taskQueue.poll()) != null) {
                  try {
                     executor.runTask(task);
                     this.occupiedThreadsRef.getAndIncrement();
                  } catch (RejectedExecutionException var10) {
                     if (this.isShutDownRef.get()) {
                        executor.taskQueue.clear();
                     } else {
                        executor.taskQueue.add(task);
                     }
                  }
               }
            }
         }

         ScheduledFuture<?> newScheduledFuture = null;
         if (executorPaused) {
            newScheduledFuture = SCHEDULED_EXECUTOR_SERVICE.schedule(this.startNextTaskBlockingRunnable, 1000L, TimeUnit.MILLISECONDS);
         }

         ScheduledFuture<?> oldScheduledFuture = this.scheduledFutureRef.getAndSet(newScheduledFuture);
         if (oldScheduledFuture != null) {
            oldScheduledFuture.cancel(false);
         }
      } finally {
         this.taskPickerLock.unlock();
      }
   }

   private Iterator<PriorityTaskPicker.Executor> getExecutorIteratorSortedByShortestTotalRunTime() {
      Stream<PriorityTaskPicker.Executor> stream = this.executors.stream();
      stream = stream.sorted(Comparator.comparingLong(executor -> executor.totalRuntimeNanos.get()));
      return stream.iterator();
   }

   public void shutdownNow() {
      LOGGER.info("Shutting down PriorityTaskPicker thread pool...");
      this.isShutDownRef.set(true);

      try {
         for (int i = 0; i < this.executors.size(); i++) {
            PriorityTaskPicker.Executor executor = this.executors.get(i);
            if (executor != null) {
               executor.shutdown();
               if (!executor.awaitTermination(5L, TimeUnit.SECONDS)) {
                  executor.shutdownNow();
               }
            }
         }
      } catch (InterruptedException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static class Executor extends AbstractExecutorService implements IConfigListener {
      private final PriorityTaskPicker parentTaskPicker;
      private final String name;
      private final Queue<PriorityTaskPicker.TrackedRunnable> taskQueue = new ConcurrentLinkedQueue<>();
      private final AtomicInteger runningTasksRef = new AtomicInteger(0);
      private final AtomicInteger completedTasksRef = new AtomicInteger(0);
      private final AtomicLong totalRuntimeNanos = new AtomicLong(0L);
      private final RollingAverage runTimeInMsRollingAverage = new RollingAverage(200);
      @Nullable
      private final PriorityTaskPicker.IExecutorCanRunFunc canRunFunc;
      private RateLimitedThreadPoolExecutor threadPoolExecutor;

      public Executor(PriorityTaskPicker parentTaskPicker, String name, @Nullable PriorityTaskPicker.IExecutorCanRunFunc canRunFunc) {
         this.parentTaskPicker = parentTaskPicker;
         this.name = name;
         this.canRunFunc = canRunFunc;
         this.threadPoolExecutor = this.createThreadPool();
         Config.Common.MultiThreading.numberOfThreads.addListener(this);
      }

      private RateLimitedThreadPoolExecutor createThreadPool() {
         return new RateLimitedThreadPoolExecutor(
            Config.Common.MultiThreading.numberOfThreads.get(),
            new DhThreadFactory(this.name, Config.Common.MultiThreading.threadPriority.get(), false),
            new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors())
         );
      }

      @Override
      public void onConfigValueSet() {
         RateLimitedThreadPoolExecutor oldExecutor = this.threadPoolExecutor;
         this.threadPoolExecutor = this.createThreadPool();
         if (oldExecutor != null) {
            oldExecutor.shutdown();
         }
      }

      @Override
      public void execute(@NotNull Runnable command) {
         if (this.threadPoolExecutor.isShutdown()) {
            throw new RejectedExecutionException("Thread pool [" + this.name + "] shutdown.");
         } else {
            this.taskQueue.add(new PriorityTaskPicker.TrackedRunnable(this.parentTaskPicker, this, command));
            this.parentTaskPicker.tryStartNextTask();
         }
      }

      public void remove(@NotNull Runnable command) {
         this.taskQueue.removeIf(trackedRunnable -> trackedRunnable.command == command);
      }

      public void runTask(@NotNull Runnable command) {
         this.threadPoolExecutor.execute(command);
      }

      public int getQueueSize() {
         return this.taskQueue.size();
      }

      public int getPoolSize() {
         return Config.Common.MultiThreading.numberOfThreads.get();
      }

      public int getRunningTaskCount() {
         return this.runningTasksRef.get();
      }

      public int getCompletedTaskCount() {
         return this.completedTasksRef.get();
      }

      public double getAverageRunTimeInMs() {
         return this.runTimeInMsRollingAverage.getAverage();
      }

      public void clearQueue() {
         this.taskQueue.clear();
      }

      public boolean canRun() {
         return this.canRunFunc == null ? true : this.canRunFunc.canRun();
      }

      @Override
      public void shutdown() {
         this.threadPoolExecutor.shutdown();
      }

      @NotNull
      @Override
      public List<Runnable> shutdownNow() {
         return this.threadPoolExecutor.shutdownNow();
      }

      @Override
      public boolean isShutdown() {
         return this.threadPoolExecutor.isShutdown();
      }

      @Override
      public boolean isTerminated() {
         return this.threadPoolExecutor.isTerminated();
      }

      @Override
      public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
         return this.threadPoolExecutor.awaitTermination(timeout, unit);
      }

      public static String getThreadPoolStatString(String displayName, PriorityTaskPicker.Executor pool) {
         String o = "§6";
         String g = "§a";
         String b = "§1";
         String y = "§e";
         String cf = "§r";
         NumberFormat numberFormat = F3Screen.NUMBER_FORMAT;
         String queueSize = pool != null ? numberFormat.format((long)pool.getQueueSize()) : "-";
         String completedCount = pool != null ? numberFormat.format((long)pool.getCompletedTaskCount()) : "-";
         String message = displayName + ", Tasks: " + o + queueSize + cf + ", Done: " + g + completedCount + cf;
         if (pool != null) {
            int activeThreadCount = pool.getRunningTaskCount();
            int threadCount = pool.getPoolSize();
            boolean threadPoolActive = pool.canRun();
            String poolActiveString = threadPoolActive ? "Active" : o + "Paused" + cf;
            message = message + ", " + poolActiveString + ": " + y + activeThreadCount + cf + "/" + threadCount;
            double runTimeAvgInMs = pool.getAverageRunTimeInMs();
            String runTimeAvgStr;
            if (!Double.isNaN(runTimeAvgInMs)) {
               runTimeAvgStr = numberFormat.format(runTimeAvgInMs);
            } else {
               runTimeAvgStr = "<0";
            }

            message = message + ", Avg: " + b + runTimeAvgStr + "ms" + cf;
         }

         return message;
      }

      @Override
      public String toString() {
         return getThreadPoolStatString(this.name, this);
      }
   }

   @FunctionalInterface
   public interface IExecutorCanRunFunc {
      boolean canRun();
   }

   private static class TrackedRunnable implements Runnable {
      private final PriorityTaskPicker parentTaskPicker;
      private final PriorityTaskPicker.Executor executor;
      public final Runnable command;

      public TrackedRunnable(PriorityTaskPicker parentTaskPicker, PriorityTaskPicker.Executor executor, Runnable command) {
         this.parentTaskPicker = parentTaskPicker;
         this.executor = executor;
         this.command = command;
      }

      @Override
      public void run() {
         this.executor.runningTasksRef.getAndIncrement();
         long startTime = System.nanoTime();

         try {
            this.command.run();
         } finally {
            long timeElapsed = System.nanoTime() - startTime;
            this.executor.runTimeInMsRollingAverage.add(TimeUnit.NANOSECONDS.toMillis(timeElapsed));
            this.parentTaskPicker.occupiedThreadsRef.getAndDecrement();
            this.executor.runningTasksRef.getAndDecrement();
            this.executor.completedTasksRef.getAndIncrement();
            this.executor.totalRuntimeNanos.addAndGet(timeElapsed);
            this.parentTaskPicker.tryStartNextTask();
         }
      }
   }
}
