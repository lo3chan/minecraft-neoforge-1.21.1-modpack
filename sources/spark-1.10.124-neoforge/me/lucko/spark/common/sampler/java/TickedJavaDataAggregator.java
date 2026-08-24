package me.lucko.spark.common.sampler.java;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.sampler.window.WindowStatisticsCollector;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.proto.SparkSamplerProtos;

public class TickedJavaDataAggregator extends JavaDataAggregator {
   private final TickHook tickHook;
   private final long tickLengthThreshold;
   private final int expectedSize;
   private WindowStatisticsCollector.ExplicitTickCounter tickCounter;
   private int currentTick = -1;
   private TickedJavaDataAggregator.TickList currentData = null;
   private final Object mutex = new Object();

   public TickedJavaDataAggregator(
      ExecutorService workerPool, ThreadGrouper threadGrouper, int interval, boolean ignoreSleeping, TickHook tickHook, int tickLengthThreshold
   ) {
      super(workerPool, threadGrouper, interval, ignoreSleeping);
      this.tickHook = tickHook;
      this.tickLengthThreshold = TimeUnit.MILLISECONDS.toMicros(tickLengthThreshold);
      double intervalMilliseconds = interval / 1000.0;
      this.expectedSize = (int)(50.0 / intervalMilliseconds + 10.0);
   }

   public void setTickCounter(WindowStatisticsCollector.ExplicitTickCounter tickCounter) {
      this.tickCounter = tickCounter;
   }

   @Override
   public SparkSamplerProtos.SamplerMetadata.DataAggregator getMetadata() {
      synchronized (this.mutex) {
         this.pushCurrentTick(Runnable::run);
         this.currentData = null;
      }

      return SparkSamplerProtos.SamplerMetadata.DataAggregator.newBuilder()
         .setType(SparkSamplerProtos.SamplerMetadata.DataAggregator.Type.TICKED)
         .setThreadGrouper(this.threadGrouper.asProto())
         .setTickLengthThreshold(this.tickLengthThreshold)
         .setNumberOfIncludedTicks(this.tickCounter.getTotalCountedTicks())
         .build();
   }

   @Override
   public void insertData(ThreadInfo threadInfo, int window) {
      synchronized (this.mutex) {
         int tick = this.tickHook.getCurrentTick();
         if (this.currentTick != tick || this.currentData == null) {
            this.pushCurrentTick(this.workerPool);
            this.currentTick = tick;
            this.currentData = new TickedJavaDataAggregator.TickList(this.expectedSize, window);
         }

         this.currentData.addData(threadInfo);
      }
   }

   private void pushCurrentTick(Executor executor) {
      TickedJavaDataAggregator.TickList currentData = this.currentData;
      if (currentData != null) {
         int tickLengthMicros = currentData.sizeWithoutTrailingSleeping() * this.interval;
         if (tickLengthMicros >= this.tickLengthThreshold) {
            executor.execute(currentData);
            this.tickCounter.increment();
         }
      }
   }

   @Override
   public List<ThreadNode> exportData() {
      synchronized (this.mutex) {
         this.pushCurrentTick(Runnable::run);
      }

      return super.exportData();
   }

   private final class TickList implements Runnable {
      private final List<ThreadInfo> list;
      private final int window;

      TickList(int expectedSize, int window) {
         this.list = new ArrayList<>(expectedSize);
         this.window = window;
      }

      @Override
      public void run() {
         for (ThreadInfo data : this.list) {
            TickedJavaDataAggregator.this.writeData(data, this.window);
         }
      }

      public List<ThreadInfo> getList() {
         return this.list;
      }

      public int sizeWithoutTrailingSleeping() {
         for (int i = this.list.size() - 1; i >= 0; i--) {
            if (!JavaDataAggregator.isSleeping(this.list.get(i))) {
               return i + 1;
            }
         }

         return 0;
      }

      public void addData(ThreadInfo data) {
         this.list.add(data);
      }
   }
}
