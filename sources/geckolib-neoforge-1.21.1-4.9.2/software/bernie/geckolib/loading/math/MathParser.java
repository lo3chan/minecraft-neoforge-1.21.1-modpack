package software.bernie.geckolib.loading.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.Util;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.loading.math.function.MathFunction;
import software.bernie.geckolib.loading.math.function.generic.ACosFunction;
import software.bernie.geckolib.loading.math.function.generic.ASinFunction;
import software.bernie.geckolib.loading.math.function.generic.ATan2Function;
import software.bernie.geckolib.loading.math.function.generic.ATanFunction;
import software.bernie.geckolib.loading.math.function.generic.AbsFunction;
import software.bernie.geckolib.loading.math.function.generic.CosFunction;
import software.bernie.geckolib.loading.math.function.generic.ExpFunction;
import software.bernie.geckolib.loading.math.function.generic.LogFunction;
import software.bernie.geckolib.loading.math.function.generic.ModFunction;
import software.bernie.geckolib.loading.math.function.generic.PowFunction;
import software.bernie.geckolib.loading.math.function.generic.SinFunction;
import software.bernie.geckolib.loading.math.function.generic.SqrtFunction;
import software.bernie.geckolib.loading.math.function.limit.ClampFunction;
import software.bernie.geckolib.loading.math.function.limit.MaxFunction;
import software.bernie.geckolib.loading.math.function.limit.MinFunction;
import software.bernie.geckolib.loading.math.function.misc.PiFunction;
import software.bernie.geckolib.loading.math.function.misc.ToDegFunction;
import software.bernie.geckolib.loading.math.function.misc.ToRadFunction;
import software.bernie.geckolib.loading.math.function.random.DieRollFunction;
import software.bernie.geckolib.loading.math.function.random.DieRollIntegerFunction;
import software.bernie.geckolib.loading.math.function.random.RandomFunction;
import software.bernie.geckolib.loading.math.function.random.RandomIntegerFunction;
import software.bernie.geckolib.loading.math.function.round.CeilFunction;
import software.bernie.geckolib.loading.math.function.round.FloorFunction;
import software.bernie.geckolib.loading.math.function.round.HermiteBlendFunction;
import software.bernie.geckolib.loading.math.function.round.LerpFunction;
import software.bernie.geckolib.loading.math.function.round.LerpRotFunction;
import software.bernie.geckolib.loading.math.function.round.RoundFunction;
import software.bernie.geckolib.loading.math.function.round.TruncateFunction;
import software.bernie.geckolib.loading.math.value.BooleanNegate;
import software.bernie.geckolib.loading.math.value.Calculation;
import software.bernie.geckolib.loading.math.value.CompoundValue;
import software.bernie.geckolib.loading.math.value.Constant;
import software.bernie.geckolib.loading.math.value.Group;
import software.bernie.geckolib.loading.math.value.Negative;
import software.bernie.geckolib.loading.math.value.Ternary;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.loading.math.value.VariableAssignment;
import software.bernie.geckolib.util.CompoundException;

public class MathParser {
   private static final Pattern EXPRESSION_FORMAT = Pattern.compile("^[\\w\\s_+-/*%^&|<>=!?:.,()]+$");
   private static final Pattern WHITESPACE = Pattern.compile("\\s");
   private static final Pattern NUMERIC = Pattern.compile("^-?\\d+(\\.\\d+)?$");
   private static final Pattern VALID_DOUBLE = Pattern.compile(
      "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\d+)(\\.)?((\\d+)?)([eE][+-]?(\\d+))?)|(\\.(\\d+)([eE][+-]?(\\d+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\d+)))[fFdD]?))[\\x00-\\x20]*"
   );
   private static final String MOLANG_RETURN = "return ";
   private static final String STATEMENT_DELIMITER = ";";
   private static final Map<String, MathFunction.Factory<?>> FUNCTION_FACTORIES = (Map<String, MathFunction.Factory<?>>)Util.make(
      new ConcurrentHashMap(18), map -> {
         map.put("math.abs", AbsFunction::new);
         map.put("math.acos", ACosFunction::new);
         map.put("math.asin", ASinFunction::new);
         map.put("math.atan", ATanFunction::new);
         map.put("math.atan2", ATan2Function::new);
         map.put("math.ceil", CeilFunction::new);
         map.put("math.clamp", ClampFunction::new);
         map.put("math.cos", CosFunction::new);
         map.put("math.die_roll", DieRollFunction::new);
         map.put("math.die_roll_integer", DieRollIntegerFunction::new);
         map.put("math.exp", ExpFunction::new);
         map.put("math.floor", FloorFunction::new);
         map.put("math.hermite_blend", HermiteBlendFunction::new);
         map.put("math.lerp", LerpFunction::new);
         map.put("math.lerprotate", LerpRotFunction::new);
         map.put("math.ln", LogFunction::new);
         map.put("math.max", MaxFunction::new);
         map.put("math.min", MinFunction::new);
         map.put("math.mod", ModFunction::new);
         map.put("math.pi", PiFunction::new);
         map.put("math.pow", PowFunction::new);
         map.put("math.random", RandomFunction::new);
         map.put("math.random_integer", RandomIntegerFunction::new);
         map.put("math.round", RoundFunction::new);
         map.put("math.sin", SinFunction::new);
         map.put("math.sqrt", SqrtFunction::new);
         map.put("math.to_deg", ToDegFunction::new);
         map.put("math.to_rad", ToRadFunction::new);
         map.put("math.trunc", TruncateFunction::new);
      }
   );

   public static boolean isFunctionRegistered(String name) {
      return FUNCTION_FACTORIES.containsKey(name);
   }

   public static void registerFunction(String name, MathFunction.Factory<?> factory) {
      if (FUNCTION_FACTORIES.put(name, factory) != null) {
         GeckoLibConstants.LOGGER.log(Level.WARN, "Duplicate registration of MathFunction: '" + name + "'. Ignore if intentional override");
      }

      GeckoLibConstants.LOGGER.log(Level.DEBUG, "Registered MathFunction '" + name + "'");
   }

   @Nullable
   public static <T extends MathFunction> T buildFunction(String name, MathValue... values) {
      return (T)(!FUNCTION_FACTORIES.containsKey(name) ? null : FUNCTION_FACTORIES.get(name).create(values));
   }

   public static void registerVariable(Variable variable) {
      MolangQueries.registerVariable(variable);
   }

   public static Variable getVariableFor(String name) {
      return MolangQueries.getVariableFor(name);
   }

   public static void setVariable(String name, DoubleSupplier value) {
      getVariableFor(name).set(value);
   }

   public static MathValue parseJson(JsonElement element) {
      if (!(element instanceof JsonPrimitive primitive && !primitive.isBoolean())) {
         throw new CompoundException("Bad formatting on Molang expression, expected single value, received: " + element.getClass().getSimpleName());
      } else if (primitive.isNumber()) {
         return new Constant(primitive.getAsDouble());
      } else if (primitive.isString()) {
         String value = primitive.getAsString();
         return (MathValue)(VALID_DOUBLE.matcher(value).matches() ? new Constant(Double.parseDouble(value)) : compileMolang(value));
      } else {
         return new Constant(0.0);
      }
   }

   public static MathValue compileMolang(String expression) {
      if (expression.startsWith("return ")) {
         expression = expression.substring("return ".length());
         if (expression.contains(";")) {
            expression = expression.substring(0, expression.indexOf(";"));
         }
      } else if (expression.contains(";")) {
         String[] subExpressions = expression.split(";");
         List<MathValue> subValues = new ObjectArrayList(subExpressions.length);

         for (String subExpression : subExpressions) {
            boolean isReturn = subExpression.startsWith("return ");
            if (isReturn) {
               subExpression = subExpression.substring("return ".length());
            }

            subValues.add(compileExpression(subExpression));
            if (isReturn) {
               break;
            }
         }

         return new CompoundValue(subValues.toArray(new MathValue[0]));
      }

      return compileExpression(expression);
   }

   public static MathValue compileExpression(String expression) {
      try {
         return parseSymbols(compileSymbols(decomposeExpression(expression)));
      } catch (CompoundException var2) {
         throw var2.withMessage("Failed to parse expression '" + expression + "'");
      }
   }

   public static char[] decomposeExpression(String expression) throws CompoundException {
      if (expression.isEmpty()) {
         return new char[]{'\u0000'};
      } else if (!EXPRESSION_FORMAT.matcher(expression).matches()) {
         throw new CompoundException("Invalid characters found in expression: '" + expression + "'");
      } else {
         char[] chars = WHITESPACE.matcher(expression).replaceAll("").toLowerCase(Locale.ROOT).toCharArray();
         int groupState = 0;

         for (char character : chars) {
            if (character == '(') {
               groupState++;
            } else if (character == ')') {
               groupState--;
            }

            if (groupState < 0) {
               throw new CompoundException("Closing parenthesis before opening parenthesis in expression '" + expression + "'");
            }
         }

         if (groupState != 0) {
            throw new CompoundException("Uneven parenthesis in expression, each opening brace must have a pairing close brace '" + expression + "'");
         } else {
            return chars;
         }
      }
   }

   @Nullable
   protected static String tryMergeOperativeSymbols(char[] chars, int index) {
      char ch = chars[index];
      if (!Operator.isOperativeSymbol(ch)) {
         return null;
      } else {
         int maxLength = Math.min(chars.length - index, Operator.maxOperatorLength());

         for (int length = maxLength; length > 0; length--) {
            String testOperator = String.copyValueOf(chars, index, length);
            if (Operator.isOperator(testOperator)) {
               return testOperator;
            }
         }

         return ch != '?' && ch != ':' && ch != ',' ? null : String.valueOf(ch);
      }
   }

   public static List<Either<String, List<MathValue>>> compileSymbols(char[] chars) {
      List<Either<String, List<MathValue>>> symbols = new ObjectArrayList();
      StringBuilder buffer = new StringBuilder();
      int lastSymbolIndex = -1;

      for (int i = 0; i < chars.length; i++) {
         char ch = chars[i];
         if (ch != '-' || !buffer.isEmpty() || !symbols.isEmpty() && lastSymbolIndex != symbols.size() - 1) {
            String operator = tryMergeOperativeSymbols(chars, i);
            if (operator != null) {
               i += operator.length() - 1;
               if (!buffer.isEmpty()) {
                  symbols.add(Either.left(buffer.toString()));
               }

               lastSymbolIndex = symbols.size();
               symbols.add(Either.left(operator));
               buffer.setLength(0);
            } else if (ch != '(') {
               buffer.append(ch);
            } else {
               if (!buffer.isEmpty()) {
                  symbols.add(Either.left(buffer.toString()));
                  buffer.setLength(0);
               }

               List<MathValue> subValues = new ObjectArrayList();
               int groupState = 1;

               for (int j = i + 1; j < chars.length; j++) {
                  char groupChar = chars[j];
                  if (groupChar == '(') {
                     groupState++;
                  } else if (groupChar == ')') {
                     groupState--;
                  } else if (groupChar == ',' && groupState == 1) {
                     subValues.add(parseSymbols(compileSymbols(buffer.toString().toCharArray())));
                     buffer.setLength(0);
                     continue;
                  }

                  if (groupState == 0) {
                     if (!buffer.isEmpty()) {
                        if (symbols.isEmpty()
                           || !((Either)symbols.getLast()).left().filter("-"::equals).isPresent()
                           || symbols.size() != 1 && !symbols.get(symbols.size() - 2).left().filter(Operator::isOperator).isPresent()) {
                           subValues.add(parseSymbols(compileSymbols(buffer.toString().toCharArray())));
                        } else {
                           symbols.removeLast();
                           subValues.add(new Negative(parseSymbols(compileSymbols(buffer.toString().toCharArray()))));
                        }
                     }

                     i = j;
                     symbols.add(Either.right(subValues));
                     buffer.setLength(0);
                     break;
                  }

                  buffer.append(groupChar);
               }
            }
         } else {
            buffer.append(ch);
         }
      }

      if (!buffer.isEmpty()) {
         symbols.add(Either.left(buffer.toString()));
      }

      return symbols;
   }

   public static MathValue parseSymbols(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
      if (symbols.size() == 2) {
         Optional<String> prefix = ((Either)symbols.getFirst())
            .left()
            .filter(left -> left.startsWith("-") || left.startsWith("!") || isFunctionRegistered(left));
         Optional<List<MathValue>> group = symbols.get(1).right();
         if (prefix.isPresent() && group.isPresent()) {
            return compileFunction(prefix.get(), group.get());
         }
      }

      MathValue value = compileValue(symbols);
      if (value != null) {
         return value;
      } else {
         throw new CompoundException("Unable to parse compiled symbols from expression: " + symbols);
      }
   }

   @Nullable
   protected static MathValue compileValue(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
      if (symbols.size() == 1) {
         return compileSingleValue((Either<String, List<MathValue>>)symbols.getFirst());
      } else {
         Ternary ternary = compileTernary(symbols);
         return (MathValue)(ternary != null ? ternary : compileCalculation(symbols));
      }
   }

   @Nullable
   protected static MathValue compileSingleValue(Either<String, List<MathValue>> symbol) throws CompoundException {
      return (MathValue)(symbol.right().isPresent() ? new Group((MathValue)((List)symbol.right().get()).getFirst()) : symbol.left().map(string -> {
         if (string.startsWith("!")) {
            return new BooleanNegate(compileSingleValue(Either.left(string.substring(1))));
         } else if (isNumeric(string)) {
            return new Constant(Double.parseDouble(string));
         } else if (isLikelyVariable(string)) {
            return (MathValue)(string.startsWith("-") ? new Negative(getVariableFor(string.substring(1))) : getVariableFor(string));
         } else {
            return isFunctionRegistered(string) ? compileFunction(string, List.of()) : null;
         }
      }).orElse(null));
   }

   @Nullable
   protected static MathValue compileCalculation(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
      if (symbols.size() < 3) {
         return null;
      } else {
         int symbolCount = symbols.size();
         List<Operator> operators = new ObjectArrayList(symbolCount / 2);
         List<MathValue> components = new ObjectArrayList(symbolCount / 2 + 1);
         int lastOperatorIndex = -1;

         for (int i = 0; i < symbolCount; i++) {
            Operator operator = symbols.get(i).left().filter(Operator::isOperator).map(MathParser::getOperatorFor).orElse(null);
            if (operator != null) {
               if (operator == Operator.ASSIGN_VARIABLE) {
                  if (parseSymbols(symbols.subList(0, i)) instanceof Variable variable) {
                     return new VariableAssignment(variable, parseSymbols(symbols.subList(i + 1, symbolCount)));
                  }

                  throw new CompoundException("Attempted to assign a value to a non-variable");
               }

               components.add(parseSymbols(symbols.subList(lastOperatorIndex + 1, i)));
               operators.add(operator);
               lastOperatorIndex = i;
            }
         }

         if (components.isEmpty()) {
            return null;
         } else {
            components.add(parseSymbols(symbols.subList(lastOperatorIndex + 1, symbolCount)));

            while (true) {
               Operator lastOperator = null;
               int highestOperatorIndex = -1;

               for (int ix = 0; ix < operators.size(); ix++) {
                  Operator operator = operators.get(ix);
                  if (lastOperator == null || operator.takesPrecedenceOver(lastOperator)) {
                     lastOperator = operator;
                     highestOperatorIndex = ix;
                  }
               }

               if (highestOperatorIndex == -1) {
                  if (components.size() != 1) {
                     throw new CompoundException("Invalidly formatted expression: " + symbols);
                  }

                  return (MathValue)components.getFirst();
               }

               components.add(
                  highestOperatorIndex, new Calculation(lastOperator, components.get(highestOperatorIndex), components.get(highestOperatorIndex + 1))
               );
               operators.remove(highestOperatorIndex);
               components.remove(highestOperatorIndex + 1);
               components.remove(highestOperatorIndex + 1);
            }
         }
      }
   }

   @Nullable
   protected static Ternary compileTernary(List<Either<String, List<MathValue>>> symbols) throws CompoundException {
      int symbolCount = symbols.size();
      if (symbolCount < 3) {
         return null;
      } else {
         Supplier<MathValue> condition = null;
         Supplier<MathValue> ifTrue = null;
         int ternaryState = 0;
         int lastColon = -1;
         int queryIndex = -1;

         for (int i = 0; i < symbolCount; i++) {
            int i2 = i;
            String string = (String)symbols.get(i).left().orElse(null);
            if ("?".equals(string)) {
               if (condition == null) {
                  condition = () -> parseSymbols(symbols.subList(0, i2));
                  queryIndex = i2 + 1;
               }

               ternaryState++;
            } else if (":".equals(string)) {
               if (ternaryState == 1 && ifTrue == null && queryIndex > 0) {
                  int queryIndex2 = queryIndex;
                  ifTrue = () -> parseSymbols(symbols.subList(queryIndex2, i2));
               }

               ternaryState--;
               lastColon = i;
            }
         }

         return ternaryState == 0 && condition != null && ifTrue != null && lastColon < symbolCount - 1
            ? new Ternary(condition.get(), ifTrue.get(), parseSymbols(symbols.subList(lastColon + 1, symbolCount)))
            : null;
      }
   }

   @Nullable
   protected static MathValue compileFunction(String name, List<MathValue> args) throws CompoundException {
      if (name.startsWith("!")) {
         return name.length() == 1 ? new BooleanNegate((MathValue)args.getFirst()) : new BooleanNegate(compileFunction(name.substring(1), args));
      } else if (name.startsWith("-")) {
         return name.length() == 1 ? new Negative((MathValue)args.getFirst()) : new Negative(compileFunction(name.substring(1), args));
      } else {
         return !isFunctionRegistered(name) ? null : buildFunction(name, args.toArray(new MathValue[0]));
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean isOperativeSymbol(char symbol) {
      return isOperativeSymbol(String.valueOf(symbol));
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean isOperativeSymbol(@NotNull String symbol) {
      return Operator.isOperator(symbol) || symbol.equals("?") || symbol.equals(":");
   }

   public static boolean isNumeric(String string) {
      return NUMERIC.matcher(string).matches();
   }

   protected static Operator getOperatorFor(String op) throws CompoundException {
      return Operator.getOperatorFor(op).orElseThrow(() -> new CompoundException("Unknown operator symbol '" + op + "'"));
   }

   @Deprecated(
      forRemoval = true
   )
   protected static boolean isQueryOrFunctionName(String string) {
      return !isNumeric(string) && !isOperativeSymbol(string);
   }

   protected static boolean isLikelyVariable(String string) {
      return MolangQueries.isExistingVariable(string)
         ? true
         : !isNumeric(string) && !isFunctionRegistered(string) && !Operator.isOperator(string) && !string.equals("?") && !string.equals(":");
   }
}
