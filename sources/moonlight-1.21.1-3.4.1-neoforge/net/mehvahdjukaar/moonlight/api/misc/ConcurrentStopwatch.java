package net.mehvahdjukaar.moonlight.api.misc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public final class ConcurrentStopwatch {
   private final LongAdder accumulated = new LongAdder();
   private final AtomicInteger active = new AtomicInteger();
   private final AtomicLong startedAt = new AtomicLong();

   public ConcurrentStopwatch.Timing measure() {
      return new ConcurrentStopwatch.Timing(this);
   }

   public void clear() {
      this.accumulated.reset();
      this.active.set(0);
      this.startedAt.set(0L);
   }

   public long elapsedNanos() {
      long base = this.accumulated.sum();
      return this.active.get() > 0 ? base + (System.nanoTime() - this.startedAt.get()) : base;
   }

   public double elapsedMillis() {
      return this.elapsedNanos() / 1000000.0;
   }

   public double elapsedSeconds() {
      return this.elapsedNanos() / 1.0E9;
   }

   @Override
   public String toString() {
      return String.format("Elapsed: %.3f ms (%.6f s)", this.elapsedMillis(), this.elapsedSeconds());
   }

   private void onStart() {
      if (this.active.getAndIncrement() == 0) {
         this.startedAt.set(System.nanoTime());
      }
   }

   private void onStop() {
      int left = this.active.decrementAndGet();
      if (left == 0) {
         this.accumulated.add(System.nanoTime() - this.startedAt.get());
      } else if (left < 0) {
         this.active.incrementAndGet();
         throw new IllegalStateException("close() called more times than started");
      }
   }

   public static final class Timing implements AutoCloseable {
      private final ConcurrentStopwatch owner;
      private boolean closed;

      private Timing(ConcurrentStopwatch owner) {
         this.owner = owner;
         owner.onStart();
      }

      @Override
      public void close() {
         if (!this.closed) {
            this.owner.onStop();
            this.closed = true;
         }
      }
   }
}
