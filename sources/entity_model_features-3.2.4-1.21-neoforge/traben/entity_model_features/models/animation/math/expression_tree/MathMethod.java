package traben.entity_model_features.models.animation.math.expression_tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;

public abstract class MathMethod extends MathValue implements MathComponent {
   @Nullable
   protected MathComponent optimizedAlternativeToThis = null;
   @Nullable
   protected MathValue.ResultSupplier supplier = null;
   @NotNull
   protected final List<String> rawArgs;
   @NotNull
   protected final List<MathComponent> parsedArgs;
   protected boolean isInvertedBoolean = false;

   protected MathMethod(boolean isNegative, AnimSetupContext context, @NotNull List<String> args) throws EMFMathException {
      this(isNegative, context, args, -1);
   }

   protected MathMethod(boolean isNegative, AnimSetupContext context, @NotNull List<String> args, int customArgCount) throws EMFMathException {
      super(isNegative);
      this.rawArgs = args;
      boolean correctArgCount = customArgCount != -1 ? customArgCount == args.size() : this.hasCorrectArgCount(args.size());
      if (!correctArgCount) {
         throw new EMFMathException(
            "ERROR: wrong number of arguments ["
               + args.size()
               + "] in ["
               + this.getClass().getSimpleName()
               + "] for ["
               + context.animKey
               + "] in ["
               + context.modelName
               + "]."
         );
      } else {
         this.parsedArgs = this.parseAllArgs(args, context);
      }
   }

   @Nullable
   private MathComponent parseArg(int index, String arg, AnimSetupContext context) throws EMFMathException {
      if (this.isRawStringArg(index)) {
         return null;
      } else if (arg != null && !arg.isBlank()) {
         MathComponent ret = MathExpressionParser.getOptimizedExpression(arg, false, context);
         if (ret == MathExpressionParser.NULL_EXPRESSION) {
            throw new EMFMathException("Method argument parsing null [" + arg + "] in [" + context.animKey + "] in [" + context.modelName + "].");
         } else {
            return ret;
         }
      } else {
         throw new EMFMathException("Method argument parsing error [" + arg + "] in [" + context.animKey + "] in [" + context.modelName + "].");
      }
   }

   private List<MathComponent> parseAllArgs(List<String> args, AnimSetupContext context) throws EMFMathException {
      if (args == null) {
         throw new EMFMathException("Method argument parsing error [" + args + "] in [" + context.animKey + "] in [" + context.modelName + "].");
      } else {
         List<MathComponent> expressionList = new ArrayList<>();

         for (int i = 0; i < args.size(); i++) {
            expressionList.add(this.parseArg(i, args.get(i), context));
         }

         return expressionList;
      }
   }

   private static MathMethod of(String methodNameIn, String args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      boolean booleanInvert = methodNameIn.startsWith("!");
      String methodName = booleanInvert ? methodNameIn.substring(1) : methodNameIn;
      if (!MethodRegistry.getInstance().containsMethod(methodName)) {
         throw new EMFMathException("ERROR: Unknown method [" + methodName + "], rejecting animation expression for [" + context.animKey + "].");
      } else {
         List<String> argsList = getArgsList(args);
         MathMethod method = MethodRegistry.getInstance().getMethodFactory(methodName).getMethod(argsList, isNegative, context);
         if (booleanInvert) {
            method.invertSupplierBoolean();
         }

         return method;
      }
   }

   @NotNull
   private static List<String> getArgsList(String args) {
      List<String> argsList = new ArrayList<>();
      int openBracketCount = 0;
      StringBuilder builder = new StringBuilder();
      Iterator<Character> charIterator = args.chars().mapToObj(c -> (char)c).iterator();
      char lastChar = 0;

      while (charIterator.hasNext()) {
         char ch = charIterator.next();
         if (lastChar == '\\') {
            builder.append(ch);
            lastChar = 0;
         } else {
            if (ch == '(') {
               openBracketCount++;
            } else if (ch == ')') {
               openBracketCount--;
            } else if (ch == ',' && openBracketCount == 0) {
               argsList.add(builder.toString().trim());
               builder.setLength(0);
               continue;
            }

            builder.append(ch);
            lastChar = ch;
         }
      }

      if (!builder.isEmpty()) {
         argsList.add(builder.toString().trim());
      }

      return argsList;
   }

   static MathComponent getOptimizedExpression(String methodName, String args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      if (methodName.startsWith("-")) {
         isNegative = true;
         methodName = methodName.substring(1);
      }

      MathMethod method = of(methodName, args, isNegative, context);
      return Objects.requireNonNullElse(method.optimizedAlternativeToThis, method);
   }

   protected void setOptimizedAlternativeToThis(MathComponent optimizedAlternativeToThis) {
      this.optimizedAlternativeToThis = optimizedAlternativeToThis;
   }

   protected boolean canOptimizeForConstantArgs() {
      return true;
   }

   protected void setSupplierAndOptimize(MathValue.ResultSupplier supplier) {
      this.supplier = supplier;
   }

   protected void setSupplierAndOptimize(MathValue.ResultSupplier supplier, MathComponent arg) {
      this.supplier = supplier;
      this.setOptimizedIfPossible(supplier, List.of(arg));
   }

   protected void setSupplierAndOptimize(MathValue.ResultSupplier supplier, List<MathComponent> allArgs) {
      this.supplier = supplier;
      this.setOptimizedIfPossible(supplier, allArgs);
   }

   private void invertSupplierBoolean() {
      this.isInvertedBoolean = true;
      if (this.optimizedAlternativeToThis == null) {
         MathValue.ResultSupplier currentSupplier = this.supplier;
         this.supplier = () -> MathValue.invertBoolean(currentSupplier);
      } else {
         this.optimizedAlternativeToThis = new MathConstant(MathValue.invertBoolean(this.optimizedAlternativeToThis.getResult()), this.isNegative);
      }
   }

   protected void setOptimizedIfPossible(MathValue.ResultSupplier supplier, List<MathComponent> allComponents) {
      if (this.canOptimizeForConstantArgs() && !allComponents.isEmpty()) {
         boolean foundNonConstant = allComponents.stream().anyMatch(comp -> comp != null && !comp.isConstant());
         if (!foundNonConstant) {
            float constantResult = supplier.get();
            if (!Float.isNaN(constantResult)) {
               this.optimizedAlternativeToThis = new MathConstant(constantResult, this.isNegative);
            }
         }
      }
   }

   @Override
   MathValue.ResultSupplier getResultSupplier() {
      return this.supplier;
   }

   @Override
   public final void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      this.asmVisitInner(mv, vars);
      if (this.isInvertedBoolean) {
         vars.asmInvertBoolean(mv);
      }

      if (this.isNegative) {
         vars.asmNegateFloat(mv);
      }
   }

   public abstract void asmVisitInner(MethodVisitor var1, ASMVariableHandler var2) throws EMFMathException;

   protected boolean isRawStringArg(int index) {
      return false;
   }

   protected abstract boolean hasCorrectArgCount(int var1);
}
