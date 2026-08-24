package kroppeb.stareval.function;

import kroppeb.stareval.expression.Expression;

public interface FunctionContext {
   Expression getVariable(String var1);

   boolean hasVariable(String var1);
}
