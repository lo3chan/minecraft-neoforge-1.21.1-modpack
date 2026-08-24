package me.lucko.spark.common.sampler.node;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntPredicate;
import me.lucko.spark.common.sampler.window.ProtoTimeEncoder;

public abstract class AbstractNode {
   protected static final int MAX_STACK_DEPTH = Integer.getInteger("spark.maxStackDepth", 300);
   private final Map<StackTraceNode.Description, StackTraceNode> children = new ConcurrentHashMap<>();
   private final Map<Integer, LongAdder> times = new ConcurrentHashMap<>();

   protected LongAdder getTimeAccumulator(int window) {
      LongAdder adder = this.times.get(window);
      if (adder == null) {
         adder = new LongAdder();
         this.times.put(window, adder);
      }

      return adder;
   }

   public Set<Integer> getTimeWindows() {
      return this.times.keySet();
   }

   public boolean removeTimeWindows(IntPredicate predicate) {
      return this.times.keySet().removeIf(predicate::test);
   }

   public double[] encodeTimesForProto(ProtoTimeEncoder encoder) {
      return encoder.encode(this.times);
   }

   public Collection<StackTraceNode> getChildren() {
      return this.children.values();
   }

   protected StackTraceNode resolveChild(StackTraceNode.Description description) {
      StackTraceNode result = this.children.get(description);
      return result != null ? result : this.children.computeIfAbsent(description, StackTraceNode::new);
   }

   public void merge(AbstractNode other) {
      other.times.forEach((key, value) -> this.getTimeAccumulator(key).add(value.longValue()));

      for (Entry<StackTraceNode.Description, StackTraceNode> child : other.children.entrySet()) {
         this.resolveChild(child.getKey()).merge(child.getValue());
      }
   }
}
