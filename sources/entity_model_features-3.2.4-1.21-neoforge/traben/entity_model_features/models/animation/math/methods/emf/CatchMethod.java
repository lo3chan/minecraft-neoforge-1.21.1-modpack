package traben.entity_model_features.models.animation.math.methods.emf;

import java.util.List;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.utils.EMFUtils;

public class CatchMethod extends MathMethod {
   public CatchMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      MathComponent x = this.parsedArgs.get(0);
      MathComponent c = this.parsedArgs.get(1);
      String print;
      if (args.size() == 3 && !args.get(2).isBlank()) {
         print = args.get(2);
      } else {
         print = null;
      }

      this.setSupplierAndOptimize(() -> {
         try {
            float result = x.getResult();
            if (Float.isNaN(result)) {
               if (print != null) {
                  EMFUtils.log("print: catch(" + print + ") found NaN in x.");
               }

               return c.getResult();
            } else {
               return result;
            }
         } catch (Exception var4x) {
            if (print != null) {
               EMFUtils.log("print: catch(" + print + ") found Exception in x: " + var4x.getMessage());
            }

            return c.getResult();
         }
      }, List.of(x, c));
   }

   @Override
   protected boolean canOptimizeForConstantArgs() {
      return false;
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      Label tryStart = new Label();
      Label tryEnd = new Label();
      Label catchBlock = new Label();
      Label end = new Label();
      vars.scopeFloat();
      mv.visitTryCatchBlock(tryStart, tryEnd, catchBlock, null);
      mv.visitLabel(tryStart);
      this.parsedArgs.get(0).asmVisit(mv, vars);
      mv.visitJumpInsn(167, end);
      mv.visitLabel(tryEnd);
      mv.visitLabel(catchBlock);
      this.parsedArgs.get(1).asmVisit(mv, vars);
      mv.visitLabel(end);
      vars.scopePop();
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount == 2 || argCount == 3;
   }
}
