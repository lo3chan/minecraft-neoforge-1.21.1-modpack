package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;

@FunctionalInterface
public interface I2IFunction extends TypedFunction {
   int eval(int var1);

   @Override
   default void evaluateTo(Expression[] params, FunctionContext context, FunctionReturn functionReturn) {
      params[0].evaluateTo(context, functionReturn);
      functionReturn.intReturn = this.eval(functionReturn.intReturn);
   }

   @Override
   default Type getReturnType() {
      return Type.Int;
   }

   @Override
   default TypedFunction.Parameter[] getParameters() {
      return new TypedFunction.Parameter[]{Type.IntParameter};
   }
}
