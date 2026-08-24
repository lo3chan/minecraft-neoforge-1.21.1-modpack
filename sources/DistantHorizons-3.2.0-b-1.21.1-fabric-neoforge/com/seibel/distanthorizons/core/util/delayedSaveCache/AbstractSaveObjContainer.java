package com.seibel.distanthorizons.core.util.delayedSaveCache;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractSaveObjContainer<T> {
   public long lastWrittenDateTimeMs = System.currentTimeMillis();

   public abstract void update(@Nullable T object);

   public void updateLastWrittenTimestamp() {
      this.lastWrittenDateTimeMs = System.currentTimeMillis();
   }

   public boolean hasTimedOut(long msTillTimeout) {
      long currentTime = System.currentTimeMillis();
      long timeSinceUpdate = currentTime - this.lastWrittenDateTimeMs;
      return timeSinceUpdate > msTillTimeout;
   }
}
