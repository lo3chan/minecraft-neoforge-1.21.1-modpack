package com.seibel.distanthorizons.core.util;

import java.util.concurrent.locks.ReentrantLock;

public class KeyedLockContainer<TKey> {
   protected final ReentrantLock[] lockArray;

   public KeyedLockContainer() {
      this(Runtime.getRuntime().availableProcessors() * 2);
   }

   public KeyedLockContainer(int lockCount) {
      this.lockArray = new ReentrantLock[lockCount];

      for (int i = 0; i < lockCount; i++) {
         this.lockArray[i] = new ReentrantLock();
      }
   }

   public ReentrantLock getLockForPos(TKey key) {
      return this.lockArray[Math.abs(key.hashCode()) % this.lockArray.length];
   }
}
