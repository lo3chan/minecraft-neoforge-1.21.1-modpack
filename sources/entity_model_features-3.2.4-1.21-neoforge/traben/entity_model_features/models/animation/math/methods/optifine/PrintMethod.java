package traben.entity_model_features.models.animation.math.methods.optifine;

import java.util.List;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.utils.EMFUtils;

public class PrintMethod extends MathMethod {
   private int printCount = 0;

   public PrintMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      if (args.size() == 1) {
         String expressionStr = args.get(0);
         MathComponent x = this.parsedArgs.get(0);
         this.setSupplierAndOptimize(() -> {
            float xVal = x.getResult();
            if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
               EMFUtils.log("print: [" + expressionStr + "] = " + xVal);
            }

            return xVal;
         });
      } else {
         String id = args.get(0);
         MathComponent n = this.parsedArgs.get(1);
         MathComponent x = this.parsedArgs.get(2);
         this.setSupplierAndOptimize(() -> {
            float xVal = x.getResult();
            if (!Minecraft.getInstance().isPaused() && this.getPrintCount() % (int)n.getResult() == 0 && !EMFManager.getInstance().isAnimationValidationPhase) {
               EMFUtils.log("print: [" + id + "] = " + xVal);
            }

            return xVal;
         });
      }
   }

   private int getPrintCount() {
      this.printCount++;
      return this.printCount;
   }

   public static float printStaticOne(float x, String expressionStr) {
      if (!Minecraft.getInstance().isPaused() && !EMFManager.getInstance().isAnimationValidationPhase) {
         EMFUtils.log("print: [" + expressionStr + "] = " + x);
      }

      return x;
   }

   public static float printStatic(float x, String id, float n, float counter) {
      if (n <= 0.0F || !Minecraft.getInstance().isPaused() && counter % (int)n == 0.0F && !EMFManager.getInstance().isAnimationValidationPhase) {
         EMFUtils.log("print: [" + id + "] = " + x);
      }

      return x;
   }

   @Override
   protected boolean canOptimizeForConstantArgs() {
      return false;
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      vars.scopeFloat();
      if (this.rawArgs.size() == 1) {
         this.parsedArgs.get(0).asmVisit(mv, vars);
         mv.visitLdcInsn(this.rawArgs.get(0));
         ASMHelper.visitStaticFunctionASM(mv, "printStaticOne", PrintMethod.class);
      } else {
         this.parsedArgs.get(2).asmVisit(mv, vars);
         mv.visitLdcInsn(this.rawArgs.get(0));
         this.parsedArgs.get(1).asmVisit(mv, vars);
         vars.asmVisitFrameCounter(mv);
         ASMHelper.visitStaticFunctionASM(mv, "printStatic", PrintMethod.class);
      }

      vars.scopePop();
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount == 3 || argCount == 1;
   }

   @Override
   protected boolean isRawStringArg(int index) {
      return this.rawArgs.size() == 3 && index == 0;
   }
}
