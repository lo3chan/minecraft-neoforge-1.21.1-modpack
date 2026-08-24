package traben.entity_model_features.models.animation.math.methods.emf;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathConstant;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public class KeyframeloopMethod extends MathMethod {
   public KeyframeloopMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      MathComponent delta = this.parsedArgs.get(0);
      List<MathComponent> frames = new ArrayList<>(this.parsedArgs.subList(1, this.parsedArgs.size()));
      MathComponent[] frameArray = frames.toArray(new MathComponent[0]);
      int frameCount = frameArray.length;
      MathValue.ResultSupplier supplier = () -> {
         float deltaRawx = delta.getResult();
         int deltaFloorx = Mth.floor(deltaRawx);
         MathComponent baseFramex = frameArray[(deltaFloorx % frameCount + frameCount) % frameCount];
         MathComponent beforeFramex = frameArray[((deltaFloorx - 1) % frameCount + frameCount) % frameCount];
         MathComponent nextFramex = frameArray[((deltaFloorx + 1) % frameCount + frameCount) % frameCount];
         MathComponent afterFramex = frameArray[((deltaFloorx + 2) % frameCount + frameCount) % frameCount];
         float individualFrameDeltax = Mth.frac(deltaRawx);
         return Mth.catmullrom(individualFrameDeltax, beforeFramex.getResult(), baseFramex.getResult(), nextFramex.getResult(), afterFramex.getResult());
      };
      if (delta.isConstant()) {
         float deltaRaw = delta.getResult();
         int deltaFloor = Mth.floor(deltaRaw);
         MathComponent baseFrame = frameArray[(deltaFloor % frameCount + frameCount) % frameCount];
         MathComponent beforeFrame = frameArray[((deltaFloor - 1) % frameCount + frameCount) % frameCount];
         MathComponent nextFrame = frameArray[((deltaFloor + 1) % frameCount + frameCount) % frameCount];
         MathComponent afterFrame = frameArray[((deltaFloor + 2) % frameCount + frameCount) % frameCount];
         float individualFrameDelta = Mth.frac(deltaRaw);
         this.setOptimizedAlternativeToThis(
            new MathConstant(
               Mth.catmullrom(individualFrameDelta, beforeFrame.getResult(), baseFrame.getResult(), nextFrame.getResult(), afterFrame.getResult())
            )
         );
      }

      this.setSupplierAndOptimize(supplier, this.parsedArgs);
   }

   public static float keyFrameLoopStatic(float deltaRaw, float... frameArray) {
      int deltaFloor = Mth.floor(deltaRaw);
      int frameCount = frameArray.length;
      float baseFrame = frameArray[(deltaFloor % frameCount + frameCount) % frameCount];
      float beforeFrame = frameArray[((deltaFloor - 1) % frameCount + frameCount) % frameCount];
      float nextFrame = frameArray[((deltaFloor + 1) % frameCount + frameCount) % frameCount];
      float afterFrame = frameArray[((deltaFloor + 2) % frameCount + frameCount) % frameCount];
      float individualFrameDelta = Mth.frac(deltaRaw);
      return Mth.catmullrom(individualFrameDelta, beforeFrame, baseFrame, nextFrame, afterFrame);
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      vars.scopeFloat();
      int size = this.parsedArgs.size();
      this.parsedArgs.get(0).asmVisit(mv, vars);
      mv.visitLdcInsn(size);
      mv.visitIntInsn(188, 6);

      for (int i = 1; i < size; i++) {
         mv.visitInsn(89);
         mv.visitLdcInsn(i);
         this.parsedArgs.get(i).asmVisit(mv, vars);
         mv.visitInsn(81);
      }

      mv.visitMethodInsn(184, "traben/entity_model_features/models/animation/math/methods/emf/KeyframeLoopMethod", "keyFrameLoopStatic", "(F[F)F", false);
      vars.scopePop();
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount >= 3;
   }
}
