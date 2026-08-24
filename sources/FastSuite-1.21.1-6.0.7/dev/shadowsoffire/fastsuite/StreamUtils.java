package dev.shadowsoffire.fastsuite;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.ReportType;
import net.minecraft.ReportedException;
import net.minecraft.server.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamUtils {
   private static final Logger LOGGER = LogManager.getLogger(StreamUtils.class);
   private static final AtomicInteger POOL_THREAD_COUNTER = new AtomicInteger();
   private static ForkJoinPool POOL = null;

   public static void setup(Object classCtx) {
      ClassLoader classLoader = classCtx.getClass().getClassLoader();
      POOL = new ForkJoinPool(Math.max(4, Runtime.getRuntime().availableProcessors() - 4), forkJoinPool -> {
         ForkJoinWorkerThread thread = new ForkJoinWorkerThread(forkJoinPool) {};
         thread.setContextClassLoader(classLoader);
         thread.setName(String.format("FastSuite Recipe Lookup Thread: %s", POOL_THREAD_COUNTER.incrementAndGet()));
         return thread;
      }, StreamUtils::onThreadException, true);
   }

   private static void onThreadException(Thread thread, Throwable cause) {
      if (cause instanceof CompletionException) {
         cause = cause.getCause();
      }

      if (cause instanceof ReportedException) {
         Bootstrap.realStdoutPrintln(((ReportedException)cause).getReport().getFriendlyReport(ReportType.CRASH));
         System.exit(-1);
      }

      LOGGER.error(String.format("Caught exception in thread %s", thread), cause);
   }

   public static void execute(Runnable runnable) {
      if (POOL == null) {
         throw new IllegalStateException("Tried to run a task in parallel before FastSuite has been initialized!");
      } else {
         POOL.invoke(new StreamUtils.RunnableExecuteAction(runnable));
      }
   }

   public static <T> T execute(Callable<T> callable) {
      if (POOL == null) {
         throw new IllegalStateException("Tried to run a task in parallel before FastSuite has been initialized!");
      } else {
         return POOL.invoke(new StreamUtils.CallableExecuteAction<>(callable));
      }
   }

   public static <T> T executeUntil(Callable<T> callable, long maxTime, TimeUnit unit, T fallback, Supplier<String> timeoutMsg) {
      if (POOL == null) {
         throw new IllegalStateException("Tried to run a task in parallel before FastSuite has been initialized!");
      } else {
         ForkJoinTask<T> task = POOL.submit(new StreamUtils.CallableExecuteAction<>(callable));

         try {
            return task.get(maxTime, unit);
         } catch (TimeoutException | InterruptedException var8) {
            FastSuite.LOGGER.error(timeoutMsg.get());
            var8.printStackTrace();
            dumpFSThreads();
            return fallback;
         } catch (ExecutionException var9) {
            FastSuite.LOGGER.error("Exception during multithreaded recipe lookup");
            var9.printStackTrace();
            throw new RuntimeException(var9.getCause());
         }
      }
   }

   public static void dumpFSThreads() {
      ThreadMXBean bean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] infos = bean.dumpAllThreads(true, true);
      FastSuite.LOGGER
         .debug(Arrays.stream(infos).filter(info -> info.getThreadName().startsWith("FastSuite")).map(Object::toString).collect(Collectors.joining()));
   }

   private static final class CallableExecuteAction<T> extends ForkJoinTask<T> {
      final Callable<T> callable;
      T rawResult;

      private CallableExecuteAction(Callable<T> callable) {
         this.callable = Objects.requireNonNull(callable);
      }

      @Override
      public T getRawResult() {
         return this.rawResult;
      }

      @Override
      public void setRawResult(T v) {
         this.rawResult = v;
      }

      @Override
      public boolean exec() {
         try {
            this.setRawResult(this.callable.call());
            return true;
         } catch (Exception var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   private static final class RunnableExecuteAction extends ForkJoinTask<Void> {
      final Runnable runnable;

      private RunnableExecuteAction(Runnable runnable) {
         this.runnable = Objects.requireNonNull(runnable);
      }

      public Void getRawResult() {
         return null;
      }

      public void setRawResult(Void v) {
      }

      @Override
      public boolean exec() {
         this.runnable.run();
         return true;
      }
   }
}
