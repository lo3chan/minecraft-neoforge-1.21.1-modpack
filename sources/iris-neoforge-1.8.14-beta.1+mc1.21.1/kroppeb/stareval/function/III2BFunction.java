package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;

@FunctionalInterface
public interface III2BFunction extends TypedFunction {
   boolean eval(int var1, int var2, int var3);

   @Override
   default void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
      params[0].evaluateTo(context, functionReturn);
      int a = functionReturn.intReturn;
      params[1].evaluateTo(context, functionReturn);
      int b = functionReturn.intReturn;
      params[2].evaluateTo(context, functionReturn);
      int c = functionReturn.intReturn;
      functionReturn.booleanReturn = this.eval(a, b, c);
   }

   @Override
   default Type getReturnType() {
      return Type.Boolean;
   }

   @Override
   default TypedFunction.Parameter[] getParameters() {
      return new TypedFunction.Parameter[]{Type.IntParameter, Type.IntParameter, Type.IntParameter};
   }
}
