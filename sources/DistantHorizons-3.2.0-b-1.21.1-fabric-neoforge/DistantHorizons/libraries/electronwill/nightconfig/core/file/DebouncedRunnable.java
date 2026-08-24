package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class DebouncedRunnable {
   private final Runnable runnable;
   private final long debounceTimeNanos;
   private ScheduledFuture<?> scheduledTask;

   public DebouncedRunnable(Runnable runnable, Duration debounceTime) {
      this.runnable = runnable;
      this.debounceTimeNanos = debounceTime.toNanos();
   }

   private DebouncedRunnable(Runnable runnable, long debounceTimeNanos, ScheduledFuture<?> scheduledTask) {
      this.runnable = runnable;
      this.debounceTimeNanos = debounceTimeNanos;
      this.scheduledTask = scheduledTask;
   }

   public void run(ScheduledExecutorService executor) {
      if (this.scheduledTask != null) {
         this.scheduledTask.cancel(false);
      }

      this.scheduledTask = executor.schedule(this.runnable, this.debounceTimeNanos, TimeUnit.NANOSECONDS);
   }

   public DebouncedRunnable andThen(Runnable then) {
      Runnable combined = () -> {
         this.runnable.run();
         then.run();
      };
      return new DebouncedRunnable(combined, this.debounceTimeNanos, this.scheduledTask);
   }
}
