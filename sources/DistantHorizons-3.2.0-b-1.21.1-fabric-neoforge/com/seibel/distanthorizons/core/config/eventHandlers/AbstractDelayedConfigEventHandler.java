package com.seibel.distanthorizons.core.config.eventHandlers;

import com.seibel.distanthorizons.core.config.listeners.IConfigListener;
import com.seibel.distanthorizons.core.util.TimerUtil;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractDelayedConfigEventHandler implements IConfigListener {
   public static final long DEFAULT_TIMEOUT_IN_MS = 2000L;
   private final long timeoutInMs;
   private Timer timer;

   public AbstractDelayedConfigEventHandler(long timeoutInMs) {
      this.timeoutInMs = timeoutInMs;
   }

   public abstract void onConfigTimeout();

   @Override
   public void onConfigValueSet() {
      if (this.timeoutInMs > 0L) {
         this.refreshRenderDataAfterTimeout();
      } else {
         this.onConfigTimeout();
      }
   }

   private synchronized void refreshRenderDataAfterTimeout() {
      if (this.timer != null) {
         this.timer.cancel();
      }

      TimerTask timerTask = new TimerTask() {
         @Override
         public void run() {
            AbstractDelayedConfigEventHandler.this.onConfigTimeout();
         }
      };
      this.timer = TimerUtil.CreateTimer("AbstractDelayedConfigTimer");
      this.timer.schedule(timerTask, this.timeoutInMs);
   }
}
