package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;

public class MathBinaryExpressionComponent extends MathValue implements MathComponent {
   private final MathComponent first;
   private final MathOperator action;
   private final MathComponent second;

   private MathBinaryExpressionComponent(MathComponent first, MathOperator action, MathComponent second) {
      this.first = first;
      this.action = action;
      this.second = second;
   }

   public static MathComponent getOptimizedExpression(MathComponent first, MathOperator action, MathComponent second) {
      MathBinaryExpressionComponent component = new MathBinaryExpressionComponent(first, action, second);
      if (first.isConstant() && second.isConstant()) {
         return (MathComponent)(second.getResult() != 0.0F || action != MathOperator.DIVIDE && action != MathOperator.DIVISION_REMAINDER
            ? component.toConstant()
            : new MathBinaryExpressionComponent(first.toConstant(), action, second));
      } else {
         return component;
      }
   }

   @Override
   public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      boolean overrideScopeToBool;
      if (this.action.isEqualsType()) {
         boolean b = MathValue.isBoolean(this.first.getResult());
         boolean bb = MathValue.isBoolean(this.second.getResult());
         if (b && bb) {
            overrideScopeToBool = true;
         } else {
            overrideScopeToBool = b || bb;
         }
      } else {
         overrideScopeToBool = false;
      }

      vars.scope(overrideScopeToBool || this.action.isScopeBool());
      this.first.asmVisit(mv, vars);
      this.second.asmVisit(mv, vars);
      this.action.asmVisit(mv, vars);
      vars.scopePop();
   }

   @Override
   MathValue.ResultSupplier getResultSupplier() {
      return null;
   }

   @Override
   public float getResult() {
      float value = this.action.execute(this.first, this.second);
      return this.isNegative ? -value : value;
   }

   @Override
   public String toString() {
      return "[oExp:{" + this.first + ", " + this.action + ", " + this.second + "}=" + this.getResult() + "]";
   }
}
