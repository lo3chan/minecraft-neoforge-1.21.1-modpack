package com.seibel.distanthorizons.core.util.ratelimiting;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SupplierBasedRateAndConcurrencyLimiter<TFailObj> {
   private final SupplierBasedRateLimiter<TFailObj> rateLimiter;
   private final SupplierBasedConcurrencyLimiter<TFailObj> concurrencyLimiter;

   public SupplierBasedRateAndConcurrencyLimiter(Supplier<Integer> maxRateSupplier, Consumer<TFailObj> onFailureConsumer) {
      this.rateLimiter = new SupplierBasedRateLimiter<>(maxRateSupplier, onFailureConsumer);
      this.concurrencyLimiter = new SupplierBasedConcurrencyLimiter<>(maxRateSupplier, onFailureConsumer);
   }

   public boolean tryAcquire(TFailObj context) {
      if (!this.concurrencyLimiter.tryAcquire(context)) {
         return false;
      } else if (!this.rateLimiter.tryAcquire(context)) {
         this.concurrencyLimiter.release();
         return false;
      } else {
         return true;
      }
   }

   public void release() {
      this.concurrencyLimiter.release();
   }
}
