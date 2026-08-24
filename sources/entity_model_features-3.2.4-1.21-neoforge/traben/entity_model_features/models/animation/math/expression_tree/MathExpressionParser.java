package traben.entity_model_features.models.animation.math.expression_tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFException;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.utils.EMFUtils;

public class MathExpressionParser {
   public static final MathComponent NULL_EXPRESSION = new MathConstant(0.0F / 0.0F) {
      @Override
      public float getResult() {
         EMFUtils.logError("ERROR: NULL_EXPRESSION was called, this should not happen.");
         return super.getResult();
      }
   };
   private static final List<MathOperator> BOOLEAN_COMPARATOR_ACTIONS = List.of(
      MathOperator.EQUALS,
      MathOperator.SMALLER_THAN_OR_EQUALS,
      MathOperator.SMALLER_THAN,
      MathOperator.LARGER_THAN_OR_EQUALS,
      MathOperator.LARGER_THAN,
      MathOperator.NOT_EQUALS
   );
   private static final List<MathOperator> BOOLEAN_LOGICAL_ACTIONS = List.of(MathOperator.AND, MathOperator.OR);
   private static final List<MathOperator> MULTIPLICATION_ACTIONS = List.of(MathOperator.MULTIPLY, MathOperator.DIVIDE, MathOperator.DIVISION_REMAINDER);
   private static final List<MathOperator> ADDITION_ACTIONS = List.of(MathOperator.ADD, MathOperator.SUBTRACT);
   private final String originalExpression;
   private final boolean wasInvertedBooleanExpression;
   private final boolean isNegative;
   private final AnimSetupContext context;
   private MathComponent optimizedComponent = null;
   private MathExpressionParser.CalculationList components;
   private boolean nextValueIsNegative = false;
   private String caughtExceptionString = null;

   private MathExpressionParser(String expressionString, boolean isNegative, AnimSetupContext context, boolean invertBoolean) {
      this.isNegative = isNegative;
      this.context = context;
      this.wasInvertedBooleanExpression = invertBoolean;
      expressionString = expressionString.trim();
      this.originalExpression = expressionString;
      this.components = new MathExpressionParser.CalculationList();

      try {
         MathExpressionParser.RollingReader rollingReader = new MathExpressionParser.RollingReader();
         Iterator<Character> charIterator = expressionString.chars().mapToObj(c -> (char)c).iterator();
         Character firstBooleanChar = null;

         while (charIterator.hasNext()) {
            char currentChar = charIterator.next();
            if (!Character.isWhitespace(currentChar)) {
               MathOperator asAction = MathOperator.getAction(currentChar);
               if (firstBooleanChar != null) {
                  if (asAction == MathOperator.BOOLEAN_CHAR) {
                     this.readDoubleBooleanAction(this.context, firstBooleanChar, currentChar);
                     if (!charIterator.hasNext()) {
                        throw new EMFMathException(
                           "ERROR: boolean operator ["
                              + firstBooleanChar
                              + currentChar
                              + "] at end of expression for ["
                              + this.context.animKey
                              + "] in ["
                              + this.context.modelName
                              + "]."
                        );
                     }

                     currentChar = charIterator.next();
                     asAction = MathOperator.getAction(currentChar);
                  } else {
                     this.readLastSingleBooleanAction(this.context, firstBooleanChar, rollingReader);
                  }

                  firstBooleanChar = null;
               }

               if (asAction == MathOperator.BOOLEAN_CHAR) {
                  firstBooleanChar = currentChar;
               }

               if (asAction != MathOperator.SUBTRACT
                  || (!this.components.isEmpty() || !rollingReader.isEmpty())
                     && (this.components.isEmpty() || !(this.components.getLast() instanceof MathOperator) || !rollingReader.isEmpty())) {
                  if (asAction == MathOperator.NONE) {
                     rollingReader.write(currentChar);
                  } else if (asAction == MathOperator.OPEN_BRACKET) {
                     this.readMethodOrBrackets(rollingReader, charIterator);
                  } else {
                     this.readVariableOrConstant(rollingReader);
                     if (rollingReader.isEmpty() && asAction != MathOperator.BOOLEAN_CHAR) {
                        this.components.add(asAction);
                     }
                  }
               } else {
                  this.nextValueIsNegative = true;
               }
            }
         }

         this.readVariableOrConstant(rollingReader);
         if (this.components.isEmpty()) {
            throw new EMFMathException("ERROR: math components found to be empty for [" + this.context.animKey + "] in [" + this.context.modelName + "]");
         }

         MathExpressionParser.CalculationList newComponents = new MathExpressionParser.CalculationList();
         MathComponent lastComponent = null;

         for (MathComponent component : this.components) {
            if (lastComponent instanceof MathOperator && component instanceof MathOperator action) {
               if (action != MathOperator.ADD) {
                  newComponents.add(component);
               }
            } else {
               newComponents.add(component);
            }

            lastComponent = component;
         }

         if (newComponents.get(0) == MathOperator.ADD) {
            newComponents.remove(0);
         }

         if (newComponents.size() != this.components.size()) {
            this.components = newComponents;
         }

         this.validateAndOptimize();
      } catch (EMFMathException var13) {
         this.caughtExceptionString = var13.toString();
      } catch (Exception var14) {
         this.caughtExceptionString = "EMF animation ERROR: for [" + this.context.animKey + "] in [" + this.context.modelName + "] cause [" + var14 + "].";
         var14.printStackTrace();
      }
   }

   public static MathComponent getOptimizedExpression(String expressionString, boolean isNegative, AnimSetupContext context) {
      return getOptimizedExpression(expressionString, isNegative, context, false);
   }

   private static MathComponent getOptimizedExpression(String expressionString, boolean isNegative, AnimSetupContext context, boolean invertBoolean) {
      try {
         MathExpressionParser expression = new MathExpressionParser(expressionString, isNegative, context, invertBoolean);
         MathComponent optimized = expression.optimizedComponent;
         if (optimized == null) {
            EMFUtils.logError("EMF animation ERROR: for [" + context.animKey + "] in [" + context.modelName + "] because " + expression.caughtExceptionString);
            return NULL_EXPRESSION;
         } else {
            return expression.wasInvertedBooleanExpression ? MathComponent.invertedBooleanDelegate(optimized) : optimized;
         }
      } catch (Exception var6) {
         EMFUtils.logError("EMF animation ERROR: for [" + context.animKey + "] in [" + context.modelName + "] because [" + var6 + "].");
         return NULL_EXPRESSION;
      }
   }

   private static String readBracketContents(Iterator<Character> charIterator) {
      StringBuilder bracketContents = new StringBuilder();
      int nesting = 0;

      while (charIterator.hasNext()) {
         char ch2 = charIterator.next();
         if (ch2 == '(') {
            nesting++;
         } else if (ch2 == ')') {
            if (nesting == 0) {
               break;
            }

            nesting--;
         }

         bracketContents.append(ch2);
      }

      return bracketContents.toString();
   }

   private void readLastSingleBooleanAction(AnimSetupContext context, Character firstBooleanChar, MathExpressionParser.RollingReader rollingReader) throws EMFMathException {
      if (firstBooleanChar == '!') {
         rollingReader.write('!');
      } else {
         this.components
            .add(
               switch (firstBooleanChar) {
                  case '&' -> MathOperator.AND;
                  case '<' -> MathOperator.SMALLER_THAN;
                  case '=' -> MathOperator.EQUALS;
                  case '>' -> MathOperator.LARGER_THAN;
                  case '|' -> MathOperator.OR;
                  default -> throw new EMFMathException(
                     "ERROR: with boolean processing for operator [" + firstBooleanChar + "] for [" + context.animKey + "] in [" + context.modelName + "]."
                  );
               }
            );
      }
   }

   private void readDoubleBooleanAction(AnimSetupContext context, Character firstBooleanChar, char currentChar) throws EMFMathException {
      String var5 = "" + firstBooleanChar + currentChar;

      MathOperator doubleAction = switch (var5) {
         case "==" -> MathOperator.EQUALS;
         case "!=" -> MathOperator.NOT_EQUALS;
         case "&&" -> MathOperator.AND;
         case "||" -> MathOperator.OR;
         case ">=" -> MathOperator.LARGER_THAN_OR_EQUALS;
         case "<=" -> MathOperator.SMALLER_THAN_OR_EQUALS;
         default -> throw new EMFMathException(
            "ERROR: with boolean processing for operator ["
               + firstBooleanChar
               + currentChar
               + "] for ["
               + context.animKey
               + "] in ["
               + context.modelName
               + "]."
         );
      };
      this.components.add(doubleAction);
   }

   private void readMethodOrBrackets(MathExpressionParser.RollingReader rollingReader, Iterator<Character> charIterator) throws EMFMathException {
      String functionName = rollingReader.read();
      String bracketContents = readBracketContents(charIterator);
      switch (functionName) {
         case "":
            this.components.add(getOptimizedExpression(bracketContents, this.getNegativeNext(), this.context, false));
            break;
         case "!":
            this.components.add(getOptimizedExpression(bracketContents, this.getNegativeNext(), this.context, true));
            break;
         case "-":
            this.getNegativeNext();
            this.components.add(getOptimizedExpression(bracketContents, true, this.context, false));
            break;
         default:
            this.components.add(MathMethod.getOptimizedExpression(functionName, bracketContents, this.getNegativeNext(), this.context));
      }
   }

   private void readVariableOrConstant(MathExpressionParser.RollingReader rollingReader) throws EMFMathException {
      if (!rollingReader.isEmpty()) {
         String read = rollingReader.read();

         try {
            float number = Float.parseFloat(read);
            if (read.startsWith(".") && ((EMFConfig)EMF.config().getConfig()).enforceOptiFineAnimSyntaxLimits) {
               throw new EMFMathException(
                  "ERROR: number ["
                     + read
                     + "] in expression ["
                     + this.originalExpression
                     + "] for ["
                     + this.context.animKey
                     + "] in ["
                     + this.context.modelName
                     + "] is not valid in OptiFine. It must not start with '.' please add a zero"
               );
            }

            this.components.add(new MathConstant(number, this.getNegativeNext()));
         } catch (NumberFormatException var4) {
            if (read.matches("^(\\d|_).*") && ((EMFConfig)EMF.config().getConfig()).enforceOptiFineAnimSyntaxLimits) {
               throw new EMFMathException(
                  "ERROR: variable ["
                     + read
                     + "] in expression ["
                     + this.originalExpression
                     + "] for ["
                     + this.context.animKey
                     + "] in ["
                     + this.context.modelName
                     + "] is not valid in OptiFine. It must not start with '.' please add a zero"
               );
            }

            this.components.add(MathVariable.getOptimizedVariable(read, this.getNegativeNext(), this.context));
         }
      }
   }

   protected void validateAndOptimize() {
      if (this.caughtExceptionString != null) {
         EMFUtils.logWarn(this.caughtExceptionString);
         new EMFMathException(this.caughtExceptionString).record();
      } else {
         if (Float.isNaN(this.validateCalculationAndOptimize())) {
            EMFUtils.logWarn("result was NaN, expression not valid: " + this.originalExpression);
            new EMFMathException("result was NaN, expression not valid: " + this.originalExpression).record();
         }
      }
   }

   private boolean getNegativeNext() {
      boolean neg = this.nextValueIsNegative;
      this.nextValueIsNegative = false;
      return neg;
   }

   private float validateCalculationAndOptimize() {
      if (this.components.size() == 1) {
         MathComponent comp = this.components.getLast();
         if (comp instanceof MathConstant constnt) {
            if (this.isNegative) {
               comp = new MathConstant(-constnt.getResult());
            }
         } else if (comp instanceof MathValue val) {
            val.isNegative = this.isNegative != val.isNegative;
         }

         this.optimizedComponent = comp;
         return comp.getResult();
      } else {
         try {
            MathExpressionParser.CalculationList optimised = this.optimiseTheseActionsIntoBinaryComponents(
               BOOLEAN_LOGICAL_ACTIONS,
               this.optimiseTheseActionsIntoBinaryComponents(
                  BOOLEAN_COMPARATOR_ACTIONS,
                  this.optimiseTheseActionsIntoBinaryComponents(
                     ADDITION_ACTIONS,
                     this.optimiseTheseActionsIntoBinaryComponents(MULTIPLICATION_ACTIONS, new MathExpressionParser.CalculationList(this.components))
                  )
               )
            );
            if (optimised.size() == 1) {
               float result = optimised.getLast().getResult();
               if (Float.isNaN(result)) {
                  EMFUtils.logError(" result was NaN in [" + this.context.modelName + "] for expression: " + this.originalExpression + " as " + this.components);
               } else {
                  this.optimizedComponent = this.isNegative ? MathComponent.negativeDelegate(optimised.getLast()) : optimised.getLast();
               }

               return result;
            }

            EMFUtils.logError(
               "ERROR: calculation did not result in 1 component, found: "
                  + optimised
                  + " in ["
                  + this.context.animKey
                  + "] in ["
                  + this.context.modelName
                  + "]."
            );
            EMFUtils.logError("\texpression was [" + this.originalExpression + "].");
         } catch (Exception var4) {
            String message = "EMF animation ERROR: expression error in ["
               + this.context.animKey
               + "] in ["
               + this.context.modelName
               + "] caused by ["
               + var4
               + "].";
            EMFUtils.logError(message);
            new EMFException(message).record();
         }

         return 0.0F / 0.0F;
      }
   }

   private MathExpressionParser.CalculationList optimiseTheseActionsIntoBinaryComponents(
      List<MathOperator> actionsForThisPass, MathExpressionParser.CalculationList componentsOptimized
   ) {
      List<MathOperator> containedActions = actionsForThisPass.stream().filter(componentsOptimized::contains).toList();
      if (containedActions.isEmpty()) {
         return componentsOptimized;
      } else {
         MathExpressionParser.CalculationList newComponents = new MathExpressionParser.CalculationList();
         Iterator<MathComponent> compIterator = componentsOptimized.iterator();

         while (compIterator.hasNext()) {
            MathComponent component = compIterator.next();
            if (component instanceof MathOperator action && containedActions.contains(action)) {
               MathComponent last = newComponents.getLast();
               MathComponent next = compIterator.next();
               newComponents.remove(newComponents.size() - 1);
               newComponents.add(MathBinaryExpressionComponent.getOptimizedExpression(last, action, next));
            } else {
               newComponents.add(component);
            }
         }

         return newComponents;
      }
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("{");

      for (MathComponent comp : this.components) {
         builder.append(comp.toString()).append(" ");
      }

      builder.append("}");
      return builder.toString();
   }

   private static class CalculationList extends ArrayList<MathComponent> {
      public CalculationList(MathExpressionParser.CalculationList components) {
         super(components);
      }

      public CalculationList() {
      }

      public MathComponent getLast() {
         return (MathComponent)super.get(this.size() - 1);
      }
   }

   private static class RollingReader {
      private StringBuilder builder = new StringBuilder();

      void clear() {
         this.builder = new StringBuilder();
      }

      void write(char ch) {
         this.builder.append(ch);
      }

      String read() {
         String result = this.builder.toString();
         this.clear();
         return result;
      }

      @Override
      public String toString() {
         return this.builder.toString();
      }

      boolean isEmpty() {
         return this.builder.isEmpty();
      }
   }
}
