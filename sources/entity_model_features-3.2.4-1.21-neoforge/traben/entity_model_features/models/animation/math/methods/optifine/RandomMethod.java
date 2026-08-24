package traben.entity_model_features.models.animation.math.methods.optifine;

import java.util.List;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;

public class RandomMethod extends MathMethod {
   protected final boolean hasSeed;

   public RandomMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      this.hasSeed = args.size() == 1 && !args.get(0).isBlank();
      if (this.hasSeed) {
         MathComponent arg = this.parsedArgs.get(0);
         this.setSupplierAndOptimize(() -> nextValue(arg.getResult()), arg);
      } else {
         this.setSupplierAndOptimize(RandomMethod::nextValueBasic);
      }
   }

   public static float nextValue(float seed) {
      int hash = optifineIntHash(Float.floatToIntBits(seed));
      return Math.abs(hash) / 2.1474836E9F;
   }

   public static float nextValueBasic() {
      return (float)Math.random();
   }

   public static int optifineIntHash(int x) {
      x = x ^ 61 ^ x >> 16;
      x += x << 3;
      x ^= x >> 4;
      x *= 668265261;
      return x ^ x >> 15;
   }

   @Override
   protected boolean canOptimizeForConstantArgs() {
      return this.hasSeed;
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      if (this.hasSeed) {
         vars.scopeFloat();
         this.parsedArgs.get(0).asmVisit(mv, vars);
         vars.scopePop();
         ASMHelper.visitStaticFunctionASM(mv, "nextValue", RandomMethod.class);
      } else {
         ASMHelper.visitStaticFunctionASM(mv, "nextValueBasic", RandomMethod.class);
      }
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount == 1 || argCount == 0;
   }
}
