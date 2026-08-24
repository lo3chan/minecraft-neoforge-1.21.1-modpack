package com.seibel.distanthorizons.core.util.ratelimiting;

import com.google.common.util.concurrent.RateLimiter;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SupplierBasedRateLimiter<TFailObject> {
   private final Supplier<Integer> maxRateSupplier;
   private final Consumer<TFailObject> onFailureConsumer;
   private final RateLimiter rateLimiter = RateLimiter.create(1.0 / 0.0);

   public SupplierBasedRateLimiter(Supplier<Integer> maxRateSupplier) {
      this(maxRateSupplier, ignored -> {});
   }

   public SupplierBasedRateLimiter(Supplier<Integer> maxRateSupplier, Consumer<TFailObject> onFailureConsumer) {
      this.maxRateSupplier = maxRateSupplier;
      this.onFailureConsumer = onFailureConsumer;
   }

   public boolean tryAcquire(TFailObject failContext) {
      return this.tryAcquire(failContext, 1);
   }

   public boolean tryAcquire() {
      return this.tryAcquire(null, 1);
   }

   public boolean tryAcquire(TFailObject failContext, int permits) {
      this.rateLimiter.setRate(this.maxRateSupplier.get().intValue());
      if (!this.rateLimiter.tryAcquire(permits)) {
         this.onFailureConsumer.accept(failContext);
         return false;
      } else {
         return true;
      }
   }

   public void acquireAll() {
      int ignored = this.acquireOrDrain(2147483647);
   }

   public int acquireOrDrain(int requestedPermits) {
      this.rateLimiter.setRate(this.maxRateSupplier.get().intValue());
      int acquiredCount = 0;

      while (requestedPermits > 0 && this.rateLimiter.tryAcquire()) {
         acquiredCount++;
         requestedPermits--;
      }

      return acquiredCount;
   }
}
