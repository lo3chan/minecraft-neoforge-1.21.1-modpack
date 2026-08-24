package com.seibel.distanthorizons.core.multiplayer.client;

import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSplitMessage;
import java.util.concurrent.atomic.AtomicLong;

public class ClientCongestionControl {
   private static final double ADDITIVE_INCREASE = 50000.0;
   private static final long INTERVAL_MS = 1000L;
   private final Runnable rateUpdateHandler;
   private final AtomicLong bytesReceived = new AtomicLong(0L);
   private double desiredRate;
   private long lastAdjustTime;

   public ClientCongestionControl(Runnable rateUpdateHandler) {
      this.rateUpdateHandler = rateUpdateHandler;
      this.reset();
   }

   public void reset() {
      this.desiredRate = 50000.0;
      this.lastAdjustTime = System.currentTimeMillis();
      this.bytesReceived.set(0L);
   }

   public void onPayloadReceived(FullDataSplitMessage message) {
      long now = System.currentTimeMillis();
      if (now - this.lastAdjustTime >= 1000L) {
         this.adjustRate(now);
      }

      this.bytesReceived.addAndGet(message.buffer.readableBytes());
   }

   private void adjustRate(long now) {
      double throughput = this.bytesReceived.getAndSet(0L);
      if (throughput >= this.desiredRate) {
         this.desiredRate += 50000.0;
      } else {
         this.desiredRate = Math.max(throughput - 25000.0, 1000.0);
      }

      this.lastAdjustTime = now;
      this.rateUpdateHandler.run();
   }

   public int getDesiredRate() {
      return (int)(this.desiredRate / 1000.0);
   }
}
