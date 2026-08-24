package me.lucko.spark.common.sampler.node;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StackTraceNode extends AbstractNode {
   public static final int NULL_LINE_NUMBER = -1;
   private final StackTraceNode.Description description;

   public StackTraceNode(StackTraceNode.Description description) {
      this.description = description;
   }

   public String getClassName() {
      return this.description.className();
   }

   public String getMethodName() {
      return this.description.methodName();
   }

   public String getMethodDescription() {
      return this.description instanceof StackTraceNode.AsyncDescription ? ((StackTraceNode.AsyncDescription)this.description).methodDescription() : null;
   }

   public int getLineNumber() {
      return this.description instanceof StackTraceNode.JavaDescription ? ((StackTraceNode.JavaDescription)this.description).lineNumber() : -1;
   }

   public int getParentLineNumber() {
      return this.description instanceof StackTraceNode.JavaDescription ? ((StackTraceNode.JavaDescription)this.description).parentLineNumber() : -1;
   }

   public static final class AsyncDescription implements StackTraceNode.Description {
      private final String className;
      private final String methodName;
      private final String methodDescription;
      private final int hash;

      public AsyncDescription(String className, String methodName, String methodDescription) {
         this.className = className;
         this.methodName = methodName;
         this.methodDescription = methodDescription;
         this.hash = Objects.hash(this.className, this.methodName, this.methodDescription);
      }

      @Override
      public String className() {
         return this.className;
      }

      @Override
      public String methodName() {
         return this.methodName;
      }

      public String methodDescription() {
         return this.methodDescription;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            StackTraceNode.AsyncDescription description = (StackTraceNode.AsyncDescription)o;
            return this.hash == description.hash
               && this.className.equals(description.className)
               && this.methodName.equals(description.methodName)
               && Objects.equals(this.methodDescription, description.methodDescription);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }

   @FunctionalInterface
   public interface Describer<T> {
      StackTraceNode.Description describe(T var1, @Nullable T var2);
   }

   public interface Description {
      String className();

      String methodName();
   }

   public static final class JavaDescription implements StackTraceNode.Description {
      private final String className;
      private final String methodName;
      private final int lineNumber;
      private final int parentLineNumber;
      private final int hash;

      public JavaDescription(String className, String methodName, int lineNumber, int parentLineNumber) {
         this.className = className;
         this.methodName = methodName;
         this.lineNumber = lineNumber;
         this.parentLineNumber = parentLineNumber;
         this.hash = Objects.hash(this.className, this.methodName, this.lineNumber, this.parentLineNumber);
      }

      @Override
      public String className() {
         return this.className;
      }

      @Override
      public String methodName() {
         return this.methodName;
      }

      public int lineNumber() {
         return this.lineNumber;
      }

      public int parentLineNumber() {
         return this.parentLineNumber;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            StackTraceNode.JavaDescription description = (StackTraceNode.JavaDescription)o;
            return this.hash == description.hash
               && this.lineNumber == description.lineNumber
               && this.parentLineNumber == description.parentLineNumber
               && this.className.equals(description.className)
               && this.methodName.equals(description.methodName);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }
}
