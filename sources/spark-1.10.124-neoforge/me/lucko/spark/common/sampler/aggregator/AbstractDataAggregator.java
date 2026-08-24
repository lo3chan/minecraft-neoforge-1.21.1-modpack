package me.lucko.spark.common.sampler.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntPredicate;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.node.ThreadNode;

public abstract class AbstractDataAggregator implements DataAggregator {
   protected final Map<String, ThreadNode> threadData = new ConcurrentHashMap<>();
   protected final ThreadGrouper threadGrouper;
   protected final boolean ignoreSleeping;

   protected AbstractDataAggregator(ThreadGrouper threadGrouper, boolean ignoreSleeping) {
      this.threadGrouper = threadGrouper;
      this.ignoreSleeping = ignoreSleeping;
   }

   protected ThreadNode getNode(String group) {
      ThreadNode node = this.threadData.get(group);
      return node != null ? node : this.threadData.computeIfAbsent(group, ThreadNode::new);
   }

   @Override
   public void pruneData(IntPredicate timeWindowPredicate) {
      this.threadData.values().removeIf(node -> node.removeTimeWindowsRecursively(timeWindowPredicate));
   }

   @Override
   public List<ThreadNode> exportData() {
      List<ThreadNode> data = new ArrayList<>(this.threadData.values());

      for (ThreadNode node : data) {
         node.setThreadLabel(this.threadGrouper.getLabel(node.getThreadGroup()));
      }

      return data;
   }

   protected static boolean isSleeping(String clazz, String method) {
      return clazz.equals("java.lang.Thread") && method.equals("yield")
         || clazz.equals("jdk.internal.misc.Unsafe") && method.equals("park")
         || clazz.equals("sun.misc.Unsafe") && method.equals("park");
   }
}
