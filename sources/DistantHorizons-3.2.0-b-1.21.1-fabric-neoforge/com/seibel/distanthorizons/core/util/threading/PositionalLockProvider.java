package com.seibel.distanthorizons.core.util.threading;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.util.ThreadUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class PositionalLockProvider {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final ThreadPoolExecutor LOCK_CLEANUP_THREAD = ThreadUtil.makeSingleDaemonThreadPool("Positional Lock Cleanup");
   private static final int CLEANUP_THREAD_MAX_FREQUENCY_IN_MS = 1000;
   private static final long UNUSED_LOCK_TIMEOUT_IN_MS = 10000L;
   private static final int MAX_NUMBER_OF_LOCKS = 100;
   private final ConcurrentHashMap<Long, PositionalLockProvider.ExpiringLock> lockByPos = new ConcurrentHashMap<>();
   private final AtomicBoolean lockRemovalThreadRunning = new AtomicBoolean(false);

   public ReentrantLock getLock(long pos) {
      return this.lockByPos.compute(pos, (ignorePos, lock) -> {
         if (lock == null) {
            lock = new PositionalLockProvider.ExpiringLock();
         }

         if (this.lockByPos.size() > 100 && this.lockRemovalThreadRunning.getAndSet(true)) {
            LOCK_CLEANUP_THREAD.execute(this::removeExpiredLocks);
         }

         return (PositionalLockProvider.ExpiringLock)lock;
      });
   }

   private void removeExpiredLocks() {
      try {
         Thread.sleep(1000L);
         Iterator<Long> keySet = this.lockByPos.keySet().iterator();

         while (keySet.hasNext()) {
            try {
               long currentTime = System.currentTimeMillis();
               long pos = keySet.next();
               PositionalLockProvider.ExpiringLock lock = this.lockByPos.get(pos);
               if (lock.tryLockWithoutUpdatingExpirationTime()) {
                  if (currentTime > lock.expirationTimeInMs) {
                     this.lockByPos.remove(pos);
                  }

                  lock.unlock();
               }
            } catch (NoSuchElementException var11) {
            }
         }
      } catch (Exception var12) {
         LOGGER.error("PositionLockProvider unexpected error when removing expired locks. Error: " + var12.getMessage(), var12);
      } finally {
         this.lockRemovalThreadRunning.set(false);
      }
   }

   private static class ExpiringLock extends ReentrantLock {
      public long expirationTimeInMs;

      public ExpiringLock() {
         this.resetExpirationTime();
      }

      @Override
      public void lock() {
         this.resetExpirationTime();
         super.lock();
      }

      @Override
      public boolean tryLock() {
         this.resetExpirationTime();
         return super.tryLock();
      }

      @Override
      public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
         this.resetExpirationTime();
         return super.tryLock(timeout, unit);
      }

      @Override
      public void unlock() {
         this.resetExpirationTime();
         super.unlock();
      }

      private void resetExpirationTime() {
         this.expirationTimeInMs = System.currentTimeMillis() + 10000L;
      }

      public boolean tryLockWithoutUpdatingExpirationTime() {
         return super.tryLock();
      }
   }
}
