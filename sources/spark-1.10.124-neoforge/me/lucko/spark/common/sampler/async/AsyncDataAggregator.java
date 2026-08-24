package me.lucko.spark.common.sampler.async;

import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.aggregator.AbstractDataAggregator;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.proto.SparkSamplerProtos;

public class AsyncDataAggregator extends AbstractDataAggregator {
   private static final StackTraceNode.Describer<AsyncStackTraceElement> STACK_TRACE_DESCRIBER = (element, parent) -> new StackTraceNode.AsyncDescription(
      element.getClassName(), element.getMethodName(), element.getMethodDescription()
   );

   protected AsyncDataAggregator(ThreadGrouper threadGrouper, boolean ignoreSleeping) {
      super(threadGrouper, ignoreSleeping);
   }

   @Override
   public SparkSamplerProtos.SamplerMetadata.DataAggregator getMetadata() {
      return SparkSamplerProtos.SamplerMetadata.DataAggregator.newBuilder()
         .setType(SparkSamplerProtos.SamplerMetadata.DataAggregator.Type.SIMPLE)
         .setThreadGrouper(this.threadGrouper.asProto())
         .build();
   }

   public void insertData(ProfileSegment element, int window) {
      if (!this.ignoreSleeping || !isSleeping(element)) {
         try {
            ThreadNode node = this.getNode(this.threadGrouper.getGroup(element.getNativeThreadId(), element.getThreadName()));
            node.log(STACK_TRACE_DESCRIBER, element.getStackTrace(), element.getValue(), window);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   private static boolean isSleeping(ProfileSegment element) {
      String threadState = element.getThreadState();
      if (threadState.equals("STATE_SLEEPING")) {
         return true;
      } else {
         AsyncStackTraceElement[] stackTrace = element.getStackTrace();

         for (int i = 0; i < Math.min(3, stackTrace.length); i++) {
            String clazz = stackTrace[i].getClassName();
            String method = stackTrace[i].getMethodName();
            if (isSleeping(clazz, method)) {
               return true;
            }
         }

         return false;
      }
   }
}
