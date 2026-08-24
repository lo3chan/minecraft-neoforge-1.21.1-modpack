package mezz.jei.common.util;

import java.time.Duration;
import java.util.concurrent.Future;
import mezz.jei.common.Internal;
import org.jetbrains.annotations.Nullable;

public class DeduplicatingRunner {
   private final IDelayedExecutor executor;
   private final Duration delay;
   @Nullable
   private Future<?> future;

   public DeduplicatingRunner(Duration delay) {
      this(delay, Internal.getDelayedExecutor());
   }

   public DeduplicatingRunner(Duration delay, IDelayedExecutor executor) {
      this.delay = delay;
      this.executor = executor;
   }

   public synchronized void run(Runnable runnable) {
      if (this.future != null) {
         this.future.cancel(false);
      }

      this.future = this.executor.schedule(runnable, this.delay);
   }
}
