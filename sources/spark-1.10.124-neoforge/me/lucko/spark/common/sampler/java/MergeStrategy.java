package me.lucko.spark.common.sampler.java;

import java.util.Objects;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.util.MethodDisambiguator;

public enum MergeStrategy {
   SAME_METHOD(false),
   SEPARATE_PARENT_CALLS(true);

   private final boolean separateParentCalls;

   private MergeStrategy(boolean separateParentCalls) {
      this.separateParentCalls = separateParentCalls;
   }

   public boolean separateParentCalls() {
      return this.separateParentCalls;
   }

   public boolean shouldMerge(MethodDisambiguator disambiguator, StackTraceNode n1, StackTraceNode n2) {
      if (!n1.getClassName().equals(n2.getClassName())) {
         return false;
      } else if (!n1.getMethodName().equals(n2.getMethodName())) {
         return false;
      } else if (this.separateParentCalls && n1.getParentLineNumber() != n2.getParentLineNumber()) {
         return false;
      } else {
         String desc1 = disambiguator.disambiguate(n1).map(MethodDisambiguator.MethodDescription::getDescription).orElse(null);
         String desc2 = disambiguator.disambiguate(n2).map(MethodDisambiguator.MethodDescription::getDescription).orElse(null);
         return desc1 == null && desc2 == null ? true : Objects.equals(desc1, desc2);
      }
   }
}
