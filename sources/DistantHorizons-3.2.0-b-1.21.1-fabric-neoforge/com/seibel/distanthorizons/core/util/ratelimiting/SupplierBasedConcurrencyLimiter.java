package com.seibel.distanthorizons.core.util.ratelimiting;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SupplierBasedConcurrencyLimiter<TFailObj> {
   private final Supplier<Integer> maxConcurrentTasksSupplier;
   private final Consumer<TFailObj> onFailureConsumer;
   private final AtomicInteger pendingTasks = new AtomicInteger();

   public SupplierBasedConcurrencyLimiter(Supplier<Integer> maxConcurrentTasksSupplier, Consumer<TFailObj> onFailureConsumer) {
      this.maxConcurrentTasksSupplier = maxConcurrentTasksSupplier;
      this.onFailureConsumer = onFailureConsumer;
   }

   public boolean tryAcquire(TFailObj context) {
      if (this.pendingTasks.incrementAndGet() > this.maxConcurrentTasksSupplier.get()) {
         this.pendingTasks.decrementAndGet();
         this.onFailureConsumer.accept(context);
         return false;
      } else {
         return true;
      }
   }

   public void release() {
      this.pendingTasks.decrementAndGet();
   }
}
