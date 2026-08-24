package me.lucko.spark.common.sampler.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.sampler.node.exporter.AbstractNodeExporter;
import me.lucko.spark.common.sampler.window.ProtoTimeEncoder;
import me.lucko.spark.common.util.MethodDisambiguator;
import me.lucko.spark.proto.SparkSamplerProtos;

public class JavaNodeExporter extends AbstractNodeExporter {
   private final MergeStrategy mergeStrategy;
   private final MethodDisambiguator methodDisambiguator;

   public JavaNodeExporter(ProtoTimeEncoder timeEncoder, MergeStrategy mergeStrategy, MethodDisambiguator methodDisambiguator) {
      super(timeEncoder);
      this.mergeStrategy = mergeStrategy;
      this.methodDisambiguator = methodDisambiguator;
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

      int lineNumber = stackTraceNode.getLineNumber();
      if (lineNumber >= 0) {
         proto.setLineNumber(lineNumber);
      }

      if (this.mergeStrategy.separateParentCalls()) {
         int parentLineNumber = stackTraceNode.getParentLineNumber();
         if (parentLineNumber >= 0) {
            proto.setParentLineNumber(parentLineNumber);
         }
      }

      this.methodDisambiguator.disambiguate(stackTraceNode).map(MethodDisambiguator.MethodDescription::getDescription).ifPresent(proto::setMethodDesc);
      proto.addAllChildrenRefs(childrenRefs);
      return proto.build();
   }

   @Override
   protected Collection<StackTraceNode> exportChildren(Collection<StackTraceNode> children) {
      if (children.isEmpty()) {
         return children;
      } else {
         List<StackTraceNode> list = new ArrayList<>(children.size());

         label27:
         for (StackTraceNode child : children) {
            for (StackTraceNode other : list) {
               if (this.mergeStrategy.shouldMerge(this.methodDisambiguator, other, child)) {
                  other.merge(child);
                  continue label27;
               }
            }

            list.add(child);
         }

         return list;
      }
   }
}
