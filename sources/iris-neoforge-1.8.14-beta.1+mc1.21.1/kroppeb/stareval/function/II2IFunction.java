package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;

@FunctionalInterface
public interface II2IFunction extends TypedFunction {
   int eval(int var1, int var2);

   @Override
   default void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
      params[0].evaluateTo(context, functionReturn);
      int a = functionReturn.intReturn;
      params[1].evaluateTo(context, functionReturn);
      int b = functionReturn.intReturn;
      functionReturn.intReturn = this.eval(a, b);
   }

   @Override
   default Type getReturnType() {
      return Type.Int;
   }

   @Override
   default TypedFunction.Parameter[] getParameters() {
      return new TypedFunction.Parameter[]{Type.IntParameter, Type.IntParameter};
   }
}
