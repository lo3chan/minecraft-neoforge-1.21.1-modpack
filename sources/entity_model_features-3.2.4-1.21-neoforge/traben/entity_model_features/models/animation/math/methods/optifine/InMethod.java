package traben.entity_model_features.models.animation.math.methods.optifine;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;

public class InMethod extends MathMethod {
   public InMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      MathComponent x = this.parsedArgs.get(0);
      List<MathComponent> vals = new ArrayList<>(this.parsedArgs);
      vals.remove(0);
      this.setSupplierAndOptimize(() -> {
         float X = x.getResult();

         for (MathComponent expression : vals) {
            if (expression.getResult() == X) {
               return 1.0F / 0.0F;
            }
         }

         return -1.0F / 0.0F;
      }, this.parsedArgs);
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      vars.scopeFloat();
      this.parsedArgs.get(0).asmVisit(mv, vars);
      int xSlot = vars.getLocalVarIndex();
      mv.visitVarInsn(56, xSlot);
      Label endTrue = new Label();
      Label end = new Label();

      for (int i = 1; i < this.parsedArgs.size(); i++) {
         mv.visitVarInsn(23, xSlot);
         this.parsedArgs.get(i).asmVisit(mv, vars);
         mv.visitInsn(149);
         mv.visitJumpInsn(153, endTrue);
      }

      mv.visitInsn(3);
      mv.visitJumpInsn(167, end);
      mv.visitLabel(endTrue);
      mv.visitInsn(4);
      mv.visitLabel(end);
      vars.popLocalVarIndex(xSlot);
      vars.scopePop();
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount >= 2;
   }
}
