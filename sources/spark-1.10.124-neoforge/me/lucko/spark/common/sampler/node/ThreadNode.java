package me.lucko.spark.common.sampler.node;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.IntPredicate;

public final class ThreadNode extends AbstractNode {
   private final String name;
   public String label;

   public ThreadNode(String name) {
      this.name = name;
   }

   public String getThreadLabel() {
      return this.label != null ? this.label : this.name;
   }

   public String getThreadGroup() {
      return this.name;
   }

   public void setThreadLabel(String label) {
      this.label = label;
   }

   public <T> void log(StackTraceNode.Describer<T> describer, T[] stack, long time, int window) {
      if (stack.length != 0) {
         this.getTimeAccumulator(window).add(time);
         AbstractNode node = this;
         T previousElement = null;

         for (int offset = 0; offset < Math.min(MAX_STACK_DEPTH, stack.length); offset++) {
            T element = stack[stack.length - 1 - offset];
            node = node.resolveChild(describer.describe(element, previousElement));
            node.getTimeAccumulator(window).add(time);
            previousElement = element;
         }
      }
   }

   public boolean removeTimeWindowsRecursively(IntPredicate predicate) {
      Queue<AbstractNode> queue = new ArrayDeque<>();
      queue.add(this);

      while (!queue.isEmpty()) {
         AbstractNode node = queue.remove();
         Collection<StackTraceNode> children = node.getChildren();
         boolean needToProcessChildren = false;
         Iterator<StackTraceNode> it = children.iterator();

         while (it.hasNext()) {
            StackTraceNode child = it.next();
            boolean windowsWereRemoved = child.removeTimeWindows(predicate);
            boolean childIsNowEmpty = child.getTimeWindows().isEmpty();
            if (childIsNowEmpty) {
               it.remove();
            } else if (windowsWereRemoved) {
               needToProcessChildren = true;
            }
         }

         if (needToProcessChildren) {
            queue.addAll(children);
         }
      }

      this.removeTimeWindows(predicate);
      return this.getTimeWindows().isEmpty();
   }
}
