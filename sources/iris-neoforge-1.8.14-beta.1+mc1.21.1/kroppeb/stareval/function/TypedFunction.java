package kroppeb.stareval.function;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import kroppeb.stareval.expression.Expression;

public interface TypedFunction {
   static String format(TypedFunction function, String name) {
      return String.format(
         "%s %s(%s) (priority: %d, pure:%s)",
         function.getReturnType().toString(),
         name,
         Arrays.stream(function.getParameters())
            .map(param -> param.constant() ? "const " + param.type() : param.type().toString())
            .collect(Collectors.joining(", ")),
         function.priority(),
         function.isPure() ? "yes" : "no"
      );
   }

   Type getReturnType();

   TypedFunction.Parameter[] getParameters();

   void evaluateTo(Expression[] var1, FunctionContext var2, FunctionReturn var3);

   default boolean isPure() {
      return true;
   }

   default int priority() {
      return 0;
   }

   public static class Parameter {
      private final Type type;
      private final boolean isConstant;

      public Parameter(Type type, boolean isConstant) {
         this.type = type;
         this.isConstant = isConstant;
      }

      public Parameter(Type type) {
         this(type, false);
      }

      public Type type() {
         return this.type;
      }

      public boolean constant() {
         return this.isConstant;
      }

      @Override
      public boolean equals(Object obj) {
         return !(obj instanceof TypedFunction.Parameter p) ? false : Objects.equals(this.type, p.type) && Objects.equals(this.isConstant, p.isConstant);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.type) + 3192 + Objects.hashCode(this.isConstant);
      }
   }
}
