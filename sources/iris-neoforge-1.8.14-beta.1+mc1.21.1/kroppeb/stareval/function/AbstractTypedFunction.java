package kroppeb.stareval.function;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractTypedFunction implements TypedFunction {
   private final Type returnType;
   private final TypedFunction.Parameter[] parameters;
   private final int priority;
   private final boolean isPure;

   public AbstractTypedFunction(Type returnType, TypedFunction.Parameter[] parameters, int priority, boolean isPure) {
      this.returnType = returnType;
      this.parameters = parameters;
      this.priority = priority;
      this.isPure = isPure;
   }

   public AbstractTypedFunction(Type returnType, Type[] parameterType) {
      this.returnType = returnType;
      this.parameters = Arrays.stream(parameterType).map(TypedFunction.Parameter::new).toArray(TypedFunction.Parameter[]::new);
      this.priority = 0;
      this.isPure = true;
   }

   @Override
   public Type getReturnType() {
      return this.returnType;
   }

   @Override
   public TypedFunction.Parameter[] getParameters() {
      return this.parameters;
   }

   @Override
   public boolean isPure() {
      return this.isPure;
   }

   @Override
   public int priority() {
      return this.priority;
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof AbstractTypedFunction func)
         ? false
         : Objects.equals(this.returnType, func.returnType)
            && Arrays.equals((Object[])this.parameters, (Object[])func.parameters)
            && this.priority == func.priority
            && this.isPure == func.isPure;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.returnType, Arrays.hashCode((Object[])this.parameters), this.priority, this.isPure);
   }

   @Override
   public String toString() {
      return TypedFunction.format(this, "unknown");
   }
}
