package me.lucko.spark.common.sampler.async;

import java.util.Collection;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.sampler.node.exporter.AbstractNodeExporter;
import me.lucko.spark.common.sampler.window.ProtoTimeEncoder;
import me.lucko.spark.proto.SparkSamplerProtos;

public class AsyncNodeExporter extends AbstractNodeExporter {
   public AsyncNodeExporter(ProtoTimeEncoder timeEncoder) {
      super(timeEncoder);
   }

   @Override
   protected SparkSamplerProtos.StackTraceNode export(StackTraceNode stackTraceNode, Iterable<Integer> childrenRefs) {
      SparkSamplerProtos.StackTraceNode.Builder proto = SparkSamplerProtos.StackTraceNode.newBuilder()
         .setClassName(stackTraceNode.getClassName())
         .setMethodName(stackTraceNode.getMethodName());
      double[] times = stackTraceNode.encodeTimesForProto(this.timeEncoder);

      for (double time : times) {
         proto.addTimes(time);
      }

      String methodDescription = stackTraceNode.getMethodDescription();
      if (methodDescription != null) {
         proto.setMethodDesc(methodDescription);
      }

      proto.addAllChildrenRefs(childrenRefs);
      return proto.build();
   }

   @Override
   protected Collection<StackTraceNode> exportChildren(Collection<StackTraceNode> children) {
      return children;
   }
}
