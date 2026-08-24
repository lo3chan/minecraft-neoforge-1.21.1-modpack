package kroppeb.stareval.expression;

import java.util.Collection;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionReturn;

public interface Expression {
   void evaluateTo(FunctionContext var1, FunctionReturn var2);

   default Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
      return this;
   }

   void listVariables(Collection<? super VariableExpression> var1);
}
