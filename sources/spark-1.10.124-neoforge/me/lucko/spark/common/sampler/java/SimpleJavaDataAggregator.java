package me.lucko.spark.common.sampler.java;

import java.lang.management.ThreadInfo;
import java.util.concurrent.ExecutorService;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.proto.SparkSamplerProtos;

public class SimpleJavaDataAggregator extends JavaDataAggregator {
   public SimpleJavaDataAggregator(ExecutorService workerPool, ThreadGrouper threadGrouper, int interval, boolean ignoreSleeping) {
      super(workerPool, threadGrouper, interval, ignoreSleeping);
   }

   @Override
   public SparkSamplerProtos.SamplerMetadata.DataAggregator getMetadata() {
      return SparkSamplerProtos.SamplerMetadata.DataAggregator.newBuilder()
         .setType(SparkSamplerProtos.SamplerMetadata.DataAggregator.Type.SIMPLE)
         .setThreadGrouper(this.threadGrouper.asProto())
         .build();
   }

   @Override
   public void insertData(ThreadInfo threadInfo, int window) {
      this.writeData(threadInfo, window);
   }
}
