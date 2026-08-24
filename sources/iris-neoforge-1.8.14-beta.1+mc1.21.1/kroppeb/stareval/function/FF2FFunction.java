package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;

@FunctionalInterface
public interface FF2FFunction extends TypedFunction {
   float eval(float var1, float var2);

   @Override
   default void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
      params[0].evaluateTo(context, functionReturn);
      float a = functionReturn.floatReturn;
      params[1].evaluateTo(context, functionReturn);
      float b = functionReturn.floatReturn;
      functionReturn.floatReturn = this.eval(a, b);
   }

   @Override
   default Type getReturnType() {
      return Type.Float;
   }

   @Override
   default TypedFunction.Parameter[] getParameters() {
      return new TypedFunction.Parameter[]{Type.FloatParameter, Type.FloatParameter};
   }
}
