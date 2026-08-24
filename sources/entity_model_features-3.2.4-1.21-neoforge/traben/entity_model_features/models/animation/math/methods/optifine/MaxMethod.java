package traben.entity_model_features.models.animation.math.methods.optifine;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;

public class MaxMethod extends MathMethod {
   public MaxMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      MathComponent initial = this.parsedArgs.get(0);
      ArrayList<MathComponent> theRest = new ArrayList<>(this.parsedArgs);
      theRest.remove(0);
      this.setSupplierAndOptimize(() -> {
         float max = initial.getResult();

         for (MathComponent parsedArg : theRest) {
            float val = parsedArg.getResult();
            if (val > max) {
               max = val;
            }
         }

         return max;
      }, this.parsedArgs);
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount >= 2;
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      vars.scopeFloat();
      this.parsedArgs.get(0).asmVisit(mv, vars);
      ArrayList<MathComponent> theRest = new ArrayList<>(this.parsedArgs);
      theRest.remove(0);

      for (MathComponent arg : theRest) {
         arg.asmVisit(mv, vars);
         mv.visitMethodInsn(184, "java/lang/Math", "max", "(FF)F", false);
      }

      vars.scopePop();
   }
}
