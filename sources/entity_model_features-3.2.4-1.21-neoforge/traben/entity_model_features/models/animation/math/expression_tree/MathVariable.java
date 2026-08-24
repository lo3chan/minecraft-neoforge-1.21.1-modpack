package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;

public class MathVariable extends MathValue implements MathComponent {
   private final MathValue.ResultSupplier resultSupplier;
   private final String name;

   public MathVariable(String variableName, boolean isNegative, MathValue.ResultSupplier supplier) {
      super(isNegative);
      this.resultSupplier = supplier;
      this.name = variableName;
   }

   public MathVariable(String variableName, MathValue.ResultSupplier supplier) {
      this.resultSupplier = supplier;
      this.name = variableName;
   }

   static MathComponent getOptimizedVariable(String variableName, boolean isNegative, AnimSetupContext context) {
      return variableName.startsWith("-")
         ? VariableRegistry.getInstance().getVariable(variableName.substring(1), true, context)
         : VariableRegistry.getInstance().getVariable(variableName, isNegative, context);
   }

   @Override
   MathValue.ResultSupplier getResultSupplier() {
      return this.resultSupplier;
   }

   @Override
   public String toString() {
      return "variable[" + this.name + "]=" + this.getResult();
   }

   @Override
   public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) {
      boolean inverted = this.name.startsWith("!");
      boolean negativeAgain = this.name.startsWith("-");
      String name = !inverted && !negativeAgain ? this.name : this.name.substring(1);
      vars.asmVisitVar(mv, name);
      if (inverted) {
         vars.asmInvertBoolean(mv);
      }

      if (this.isNegative || negativeAgain) {
         vars.asmNegateFloat(mv);
      }
   }
}
