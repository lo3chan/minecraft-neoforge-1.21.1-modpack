package kroppeb.stareval.resolver;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import kroppeb.stareval.element.ExpressionElement;
import kroppeb.stareval.element.token.IdToken;
import kroppeb.stareval.element.token.NumberToken;
import kroppeb.stareval.element.tree.AccessExpressionElement;
import kroppeb.stareval.element.tree.BinaryExpressionElement;
import kroppeb.stareval.element.tree.FunctionCall;
import kroppeb.stareval.element.tree.UnaryExpressionElement;
import kroppeb.stareval.expression.CallExpression;
import kroppeb.stareval.expression.ConstantExpression;
import kroppeb.stareval.expression.Expression;
import kroppeb.stareval.expression.VariableExpression;
import kroppeb.stareval.function.FunctionContext;
import kroppeb.stareval.function.FunctionResolver;
import kroppeb.stareval.function.FunctionReturn;
import kroppeb.stareval.function.Type;
import kroppeb.stareval.function.TypedFunction;

public class ExpressionResolver {
   private final FunctionResolver functionResolver;
   private final Function<String, Type> variableTypeMap;
   private final boolean enableDebugging;
   private final Map<String, ConstantExpression> numbers = new Object2ObjectOpenHashMap();
   private List<String> logs;

   public ExpressionResolver(FunctionResolver functionResolver, Function<String, Type> variableTypeMap) {
      this(functionResolver, variableTypeMap, false);
   }

   public ExpressionResolver(FunctionResolver functionResolver, Function<String, Type> variableTypeMap, boolean enableDebugging) {
      this.functionResolver = functionResolver;
      this.variableTypeMap = variableTypeMap;
      this.enableDebugging = enableDebugging;
   }

   public Expression resolveExpression(Type targetType, ExpressionElement expression) {
      this.clearLogs();
      Expression result = this.resolveExpressionInternal(targetType, expression, true, true);
      if (result != null) {
         return result;
      } else {
         throw new RuntimeException("Couldn't resolve: \n" + String.join("\n", this.extractLogs()));
      }
   }

   Expression resolveCallExpressionInternal(Type targetType, String name, List<? extends ExpressionElement> inner, boolean implicit) {
      int innerLength = inner.size();
      Expression result = null;
      TypedFunction resultFunction = null;

      label59:
      for (TypedFunction f : this.functionResolver.resolve(name, targetType)) {
         TypedFunction.Parameter[] paramTypes = f.getParameters();
         if (paramTypes.length == innerLength) {
            Expression[] params = new Expression[innerLength];

            for (int i = 0; i < innerLength; i++) {
               ExpressionElement paramExpression = inner.get(i);
               TypedFunction.Parameter param = paramTypes[i];
               if (param.constant() && !(paramExpression instanceof NumberToken)) {
                  continue label59;
               }

               Expression expression = this.resolveExpressionInternal(param.type(), paramExpression, !implicit || innerLength > 1, implicit);
               if (expression == null) {
                  continue label59;
               }

               params[i] = expression;
            }

            if (result != null && f.priority() == resultFunction.priority()) {
               throw new RuntimeException("Ambiguity, \n\told: " + TypedFunction.format(resultFunction, "") + "\n\tnew: " + TypedFunction.format(f, ""));
            }

            if (resultFunction == null || f.priority() >= resultFunction.priority()) {
               result = new CallExpression(f, params);
               resultFunction = f;
            }
         }
      }

      return result;
   }

   private Expression resolveCallExpression(
      Type targetType, String name, List<? extends ExpressionElement> inner, boolean allowNonImplicit, boolean allowImplicit
   ) {
      this.log("[DEBUG] resolving function %s with args %s to type %s", name, inner, targetType);
      Expression result = null;
      if (allowNonImplicit) {
         result = this.resolveCallExpressionInternal(targetType, name, inner, false);
      }

      if (result != null) {
         this.log("[DEBUG] resolved function %s with args %s to type %s directly", name, inner, targetType);
         return result;
      } else if (!allowImplicit) {
         this.log("[DEBUG] Failed to resolve function %s with args %s to type %s directly", name, inner, targetType);
         return null;
      } else {
         for (TypedFunction f : this.functionResolver.resolve("<cast>", targetType)) {
            Expression u = this.resolveCallExpression(f.getParameters()[0].type(), name, inner, true, true);
            if (u != null) {
               if (result != null) {
                  throw new RuntimeException("Ambiguity");
               }

               result = new CallExpression(f, new Expression[]{u});
            }
         }

         if (result != null) {
            this.log("[DEBUG] resolved function %s with args %s to type %s using only final cast", name, inner, targetType);
            return result;
         } else {
            result = this.resolveCallExpressionInternal(targetType, name, inner, true);
            if (result != null) {
               this.log("[DEBUG] resolved function %s with args %s to type %s using implicit inner casts", name, inner, targetType);
            } else {
               this.log("[DEBUG] failed to resolve function %s with args %s to type %s", name, inner, targetType);
            }

            return result;
         }
      }
   }

   public List<String> extractLogs() {
      List<String> old = this.logs;
      this.clearLogs();
      return old;
   }

   public void clearLogs() {
      this.logs = new ArrayList<>();
   }

   private void log(String str) {
      if (this.enableDebugging) {
         this.logs.add(str);
      }
   }

   private void log(String str, Object... args) {
      if (this.enableDebugging) {
         this.logs.add(String.format(str, args));
      }
   }

   private void log(Supplier<String> str) {
      if (this.enableDebugging) {
         this.log(str.get());
      }
   }

   private Expression resolveExpressionInternal(Type targetType, ExpressionElement expression, boolean allowNonImplicit, boolean allowImplicit) {
      this.log("[DEBUG] resolving %s to type %s (%d%d)", expression, targetType, allowNonImplicit ? 1 : 0, allowImplicit ? 1 : 0);
      Expression castable;
      Type innerType;
      switch (expression) {
         case UnaryExpressionElement token:
            return this.resolveCallExpression(targetType, token.op().name(), Collections.singletonList(token.inner()), allowNonImplicit, allowImplicit);
         case BinaryExpressionElement tokenx:
            return this.resolveCallExpression(targetType, tokenx.op().name(), Arrays.asList(tokenx.left(), tokenx.right()), allowNonImplicit, allowImplicit);
         case FunctionCall tokenxx:
            return this.resolveCallExpression(targetType, tokenxx.id(), tokenxx.args(), allowNonImplicit, allowImplicit);
         case AccessExpressionElement tokenxxx:
            return this.resolveCallExpression(
               targetType, "<access$" + tokenxxx.index() + ">", Collections.singletonList(tokenxxx.base()), allowNonImplicit, allowImplicit
            );
         case NumberToken tokenxxxx:
            ConstantExpression exp = this.resolveNumber(tokenxxxx.getNumber());
            if (exp.getType().equals(targetType)) {
               this.log("[DEBUG] resolved constant %s to type %s", tokenxxxx.getNumber(), targetType);
               return exp;
            }

            if (!allowImplicit) {
               this.log(
                  "[DEBUG] failed to resolve constant %s (of type %s) to type %s without implicit casts", tokenxxxx.getNumber(), exp.getType(), targetType
               );
               return null;
            }

            this.log("[DEBUG] trying implicit casts to resolve constant %s (of type %s) to type %s", tokenxxxx.getNumber(), exp.getType(), targetType);
            castable = exp;
            innerType = exp.getType();
            break;
         case IdToken tokenxxxxx:
            final String name = tokenxxxxx.getId();
            Type type = this.variableTypeMap.apply(name);
            if (type == null) {
               throw new RuntimeException("Unknown variable: " + name);
            }

            if (type.equals(targetType)) {
               this.log("[DEBUG] resolved variable %s to type %s", name, targetType);
               return new VariableExpression() {
                  @Override
                  public void evaluateTo(FunctionContext c, FunctionReturn r) {
                     c.getVariable(name).evaluateTo(c, r);
                  }

                  @Override
                  public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
                     return (Expression)(context.hasVariable(name) ? context.getVariable(name) : this);
                  }
               };
            }

            if (!allowImplicit) {
               this.log("[DEBUG] failed to resolve variable %s (of type %s) to type %s without implicit casts", name, type, targetType);
               return null;
            }

            castable = new VariableExpression() {
               @Override
               public void evaluateTo(FunctionContext c, FunctionReturn r) {
                  c.getVariable(name).evaluateTo(c, r);
               }

               @Override
               public Expression partialEval(FunctionContext context, FunctionReturn functionReturn) {
                  return (Expression)(context.hasVariable(name) ? context.getVariable(name) : this);
               }
            };
            innerType = type;
            break;
         case null:
         default:
            throw new RuntimeException("unexpected token: " + expression.toString());
      }

      for (TypedFunction f : this.functionResolver.resolve("<cast>", targetType)) {
         if (f.getParameters()[0].type().equals(innerType)) {
            this.log("[DEBUG] resolved %s to type %s using implicit casts", expression, targetType);
            return new CallExpression(f, new Expression[]{castable});
         }
      }

      this.log("[DEBUG] failed to resolved %s to type %s, even using implicit casts", expression, targetType);
      return null;
   }

   private ConstantExpression resolveNumber(String s) {
      return this.numbers.computeIfAbsent(s, str -> {
         try {
            final int val;
            if (str.length() >= 2 && str.charAt(0) == '0') {
               switch (str.charAt(1)) {
                  case 'b':
                     val = Integer.parseInt(str.substring(2), 2);
                     break;
                  case 'x':
                     val = Integer.parseInt(str.substring(2), 16);
                     break;
                  default:
                     val = Integer.parseInt(str.substring(1), 8);
               }
            } else {
               val = Integer.parseInt(str);
            }

            return new ConstantExpression(Type.Int) {
               @Override
               public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
                  functionReturn.intReturn = val;
               }
            };
         } catch (NumberFormatException var6) {
            try {
               final float res = Float.parseFloat(str);
               return new ConstantExpression(Type.Float) {
                  @Override
                  public void evaluateTo(FunctionContext context, FunctionReturn functionReturn) {
                     functionReturn.floatReturn = res;
                  }
               };
            } catch (NumberFormatException var5) {
               RuntimeException exception = new RuntimeException("Illegal number: " + str, var5);
               exception.addSuppressed(var6);
               throw exception;
            }
         }
      });
   }
}
