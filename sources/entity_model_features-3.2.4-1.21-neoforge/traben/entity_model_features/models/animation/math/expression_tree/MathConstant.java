package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.utils.EMFUtils;

public class MathConstant extends MathValue implements MathComponent {
   public static final MathConstant ZERO_CONST = new MathConstant(0.0F);
   public static final MathConstant FALSE_CONST = new MathConstant(-1.0F / 0.0F);
   private final float hardCodedValue;

   public MathConstant(float number, boolean isNegative) {
      this.hardCodedValue = isNegative ? -number : number;
   }

   public MathConstant(float number) {
      this.hardCodedValue = number;
   }

   @Override
   public MathValue.ResultSupplier getResultSupplier() {
      EMFUtils.logError("EMF math constant called supplier: this shouldn't happen!");
      return this::getResult;
   }

   @Override
   public boolean isConstant() {
      return true;
   }

   @Override
   public String toString() {
      return String.valueOf(this.getResult());
   }

   @Override
   public float getResult() {
      return this.hardCodedValue;
   }

   @Override
   public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) {
      if (vars.isScopeBool()) {
         mv.visitInsn(MathValue.toBoolean(this.hardCodedValue) ? 4 : 3);
      } else {
         mv.visitLdcInsn(this.hardCodedValue);
      }
   }
}
