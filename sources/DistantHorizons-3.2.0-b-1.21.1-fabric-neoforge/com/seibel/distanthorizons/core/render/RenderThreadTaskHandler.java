package com.seibel.distanthorizons.core.render;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.ExceptionUtil;
import com.seibel.distanthorizons.core.util.TimerUtil;
import com.seibel.distanthorizons.core.util.objects.RollingAverage;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftSharedWrapper;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.text.NumberFormat;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import org.jetbrains.annotations.Nullable;

public class RenderThreadTaskHandler {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logRendererEventToFile).build();
   private static final DhLogger RATE_LIMITED_LOGGER = new DhLoggerBuilder()
      .fileLevelConfig(Config.Common.Logging.logRendererEventToFile)
      .maxCountPerSecond(4)
      .build();
   private static final ConcurrentLinkedQueue<RenderThreadTaskHandler.QueuedRunnable> RENDER_THREAD_RUNNABLE_QUEUE = new ConcurrentLinkedQueue<>();
   private static final ConcurrentHashMap<String, RollingAverage> AVERAGE_NANO_RUN_TIME_BY_TASK_NAME = new ConcurrentHashMap<>();
   private static final LongAdder COMPLETED_TASK_COUNTER = new LongAdder();
   private static final NumberFormat DECIMAL_NUMBER_FORMAT = NumberFormat.getNumberInstance();
   private static final NumberFormat INT_NUMBER_FORMAT = NumberFormat.getIntegerInstance();
   private static final boolean LOG_SLOW_TASKS = false;
   private static final Timer TIMER = TimerUtil.CreateTimer("Cleanup timer");
   private static final long MS_BETWEEN_CLEANUP_TICKS = 1000L;
   private static final long NANOS_BEFORE_RUN_CLEANUP_TIMER = TimeUnit.NANOSECONDS.convert(1000L, TimeUnit.MILLISECONDS);
   public static final RenderThreadTaskHandler INSTANCE = new RenderThreadTaskHandler();
   private long nanoSinceTasksRun = System.nanoTime();
   private final boolean running;
   private Thread renderThread;
   @Nullable
   private volatile RenderThreadTaskHandler.QueuedRunnable currentQueuedRunnable;

   private RenderThreadTaskHandler() {
      IMinecraftSharedWrapper mcShared = SingletonInjector.INSTANCE.get(IMinecraftSharedWrapper.class);
      if (!mcShared.isDedicatedServer()) {
         LOGGER.debug("Starting [" + RenderThreadTaskHandler.class.getSimpleName() + "]...");
         this.running = true;
         TIMER.scheduleAtFixedRate(TimerUtil.createTimerTask(this::manualCleanupTick), 1000L, 1000L);
      } else {
         this.running = false;
         LOGGER.debug("Skipping [" + RenderThreadTaskHandler.class.getSimpleName() + "] startup due to running on a dedicated server.");
      }
   }

   public void queueRunningOnRenderThread(String name, Runnable renderCall) {
      if (this.running) {
         StackTraceElement[] stackTrace = null;
         if (ModInfo.IS_DEV_BUILD) {
            stackTrace = Thread.currentThread().getStackTrace();
         }

         RenderThreadTaskHandler.QueuedRunnable runnable = new RenderThreadTaskHandler.QueuedRunnable(name, renderCall, stackTrace);
         RENDER_THREAD_RUNNABLE_QUEUE.add(runnable);
      }
   }

   public void runRenderThreadTasks() {
      IMinecraftRenderWrapper MC_RENDER = SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
      int frameLimit = MC_RENDER.getFrameLimit();
      if (frameLimit <= 1) {
         frameLimit = 240;
      }

      int msPerFrame = 1000 / frameLimit;
      long nanoPerFrame = msPerFrame * 1000000L;
      nanoPerFrame /= 2L;
      this.runRenderThreadTasks(nanoPerFrame);
   }

   private void runRenderThreadTasks(long nanoMaxRunTime) {
      long loopStartTimeNano = System.nanoTime();
      this.nanoSinceTasksRun = loopStartTimeNano;
      if (this.renderThread == null) {
         this.renderThread = Thread.currentThread();
      }

      for (RenderThreadTaskHandler.QueuedRunnable runnable = RENDER_THREAD_RUNNABLE_QUEUE.poll();
         runnable != null;
         runnable = RENDER_THREAD_RUNNABLE_QUEUE.poll()
      ) {
         long taskStartNano = System.nanoTime();
         this.currentQueuedRunnable = runnable;
         runnable.run();
         this.currentQueuedRunnable = null;
         long taskNano = System.nanoTime() - taskStartNano;
         long totalLoopNano = System.nanoTime() - loopStartTimeNano;
         if (ModInfo.IS_DEV_BUILD) {
            if (!AVERAGE_NANO_RUN_TIME_BY_TASK_NAME.containsKey(runnable.name)) {
               AVERAGE_NANO_RUN_TIME_BY_TASK_NAME.put(runnable.name, new RollingAverage(1000));
            }

            AVERAGE_NANO_RUN_TIME_BY_TASK_NAME.get(runnable.name).add(totalLoopNano);
            COMPLETED_TASK_COUNTER.increment();
         }

         long expectedNextTaskNano = totalLoopNano + taskNano * 2L;
         if (expectedNextTaskNano >= nanoMaxRunTime) {
            break;
         }
      }
   }

   private void manualCleanupTick() {
      long nowNano = System.nanoTime();
      long nanoSinceLast = nowNano - this.nanoSinceTasksRun;
      if (nanoSinceLast >= NANOS_BEFORE_RUN_CLEANUP_TIMER) {
         IMinecraftClientWrapper mcClient = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class);
         if (mcClient != null) {
            mcClient.executeOnRenderThread(() -> this.runRenderThreadTasks(500000000L));
         } else {
            RATE_LIMITED_LOGGER.warn(
               "["
                  + RenderThreadTaskHandler.class.getSimpleName()
                  + "] timer started when ["
                  + IMinecraftClientWrapper.class.getSimpleName()
                  + "] is null. This shouldn't happen but can likely be ignored."
            );
         }
      }
   }

   public void clearDebugStats() {
      AVERAGE_NANO_RUN_TIME_BY_TASK_NAME.clear();
      COMPLETED_TASK_COUNTER.reset();
   }

   public void addDebugMenuStringsToList(List<String> messageList) {
      if (ModInfo.IS_DEV_BUILD) {
         String o = "§6";
         String g = "§a";
         String b = "§1";
         String y = "§e";
         String cf = "§r";
         String queueSize = DECIMAL_NUMBER_FORMAT.format((long)RENDER_THREAD_RUNNABLE_QUEUE.size());
         String completedCount = DECIMAL_NUMBER_FORMAT.format(COMPLETED_TASK_COUNTER.sum());
         String messageHeader = "Render Tasks, Queue: " + o + queueSize + cf + ", Done: " + g + completedCount + cf;
         messageList.add(messageHeader);
         AVERAGE_NANO_RUN_TIME_BY_TASK_NAME.forEach((name, rollingAverage) -> {
            double runTimeAvgInNano = rollingAverage.getAverage();
            String runTimeAvgStr;
            if (!Double.isNaN(runTimeAvgInNano)) {
               double runTimeAvgInMs = runTimeAvgInNano / 1000000.0;
               runTimeAvgStr = DECIMAL_NUMBER_FORMAT.format(runTimeAvgInMs);
            } else {
               runTimeAvgStr = "<0";
            }

            String lifetimeCount = INT_NUMBER_FORMAT.format(rollingAverage.getLifetimeCount());
            String message = name + " Avg: " + b + runTimeAvgStr + "ms" + cf + " #: " + y + lifetimeCount + cf;
            messageList.add(message);
         });
      }
   }

   public boolean isCurrentThread() {
      return this.renderThread != null ? Thread.currentThread() == this.renderThread : Thread.currentThread().getName().equals("Render thread");
   }

   @Nullable
   public RenderThreadTaskHandler.QueuedRunnable getCurrentlyRunningTask() {
      return this.currentQueuedRunnable;
   }

   public static class QueuedRunnable implements Runnable {
      public final String name;
      public final Runnable renderCall;
      @Nullable
      public final StackTraceElement[] stackTrace;

      public QueuedRunnable(String name, Runnable renderCall, @Nullable StackTraceElement[] stackTrace) {
         this.name = name;
         this.renderCall = renderCall;
         this.stackTrace = stackTrace;
      }

      @Override
      public void run() {
         try {
            this.renderCall.run();
         } catch (Exception var3) {
            if (ExceptionUtil.isShutdownException(var3)) {
               return;
            }

            RuntimeException error = new RuntimeException(
               "Uncaught Exception during GL call execution. StackTrace: ["
                  + (this.stackTrace != null ? "Present" : "Missing")
                  + "] Error: ["
                  + var3.getMessage()
                  + "]",
               var3
            );
            if (this.stackTrace != null) {
               error.setStackTrace(this.stackTrace);
            }

            RenderThreadTaskHandler.LOGGER
               .error("[" + Thread.currentThread().getName() + "] ran into an unexpected error running a GL call, Error: [" + var3.getMessage() + "].", error);
         }
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
