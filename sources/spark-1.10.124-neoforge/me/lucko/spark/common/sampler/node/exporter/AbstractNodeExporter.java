package me.lucko.spark.common.sampler.node.exporter;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.sampler.window.ProtoTimeEncoder;
import me.lucko.spark.common.util.IndexedListBuilder;
import me.lucko.spark.proto.SparkSamplerProtos;

public abstract class AbstractNodeExporter implements NodeExporter {
   protected final ProtoTimeEncoder timeEncoder;

   protected AbstractNodeExporter(ProtoTimeEncoder timeEncoder) {
      this.timeEncoder = timeEncoder;
   }

   @Override
   public SparkSamplerProtos.ThreadNode export(ThreadNode threadNode) {
      SparkSamplerProtos.ThreadNode.Builder proto = SparkSamplerProtos.ThreadNode.newBuilder().setName(threadNode.getThreadLabel());
      double[] times = threadNode.encodeTimesForProto(this.timeEncoder);

      for (double time : times) {
         proto.addTimes(time);
      }

      IndexedListBuilder<SparkSamplerProtos.StackTraceNode> nodesArray = new IndexedListBuilder<>();
      Deque<AbstractNodeExporter.Node> stack = new ArrayDeque<>();
      List<Integer> childrenRefs = new LinkedList<>();

      for (StackTraceNode child : this.exportChildren(threadNode.getChildren())) {
         stack.push(new AbstractNodeExporter.Node(child, childrenRefs));
      }

      while (!stack.isEmpty()) {
         AbstractNodeExporter.Node node = stack.peek();
         if (node.firstVisit) {
            for (StackTraceNode child : this.exportChildren(node.stackTraceNode.getChildren())) {
               stack.push(new AbstractNodeExporter.Node(child, node.childrenRefs));
            }

            node.firstVisit = false;
         } else {
            SparkSamplerProtos.StackTraceNode childProto = this.export(node.stackTraceNode, node.childrenRefs);
            int childIndex = nodesArray.add(childProto);
            node.parentChildrenRefs.add(childIndex);
            stack.pop();
         }
      }

      proto.addAllChildrenRefs(childrenRefs);
      proto.addAllChildren(nodesArray.build());
      return proto.build();
   }

   protected abstract SparkSamplerProtos.StackTraceNode export(StackTraceNode var1, Iterable<Integer> var2);

   protected abstract Collection<StackTraceNode> exportChildren(Collection<StackTraceNode> var1);

   private static final class Node {
      private final StackTraceNode stackTraceNode;
      private boolean firstVisit = true;
      private final List<Integer> childrenRefs = new LinkedList<>();
      private final List<Integer> parentChildrenRefs;

      private Node(StackTraceNode node, List<Integer> parentChildrenRefs) {
         this.stackTraceNode = node;
         this.parentChildrenRefs = parentChildrenRefs;
      }
   }
}
