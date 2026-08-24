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

public class KeyframeMethod extends MathMethod {
   public KeyframeMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      MathComponent delta = this.parsedArgs.get(0);
      List<MathComponent> frames = new ArrayList<>(this.parsedArgs.subList(1, this.parsedArgs.size()));
      MathComponent[] frameArray = frames.toArray(new MathComponent[0]);
      int frameEnd = frameArray.length - 1;
      MathValue.ResultSupplier supplier = () -> {
         float deltaRawx = delta.getResult();
         int deltaFloorx = Mth.floor(deltaRawx);
         if (deltaFloorx >= frameEnd) {
            return frameArray[frameEnd].getResult();
         } else if (deltaFloorx <= 0) {
            return frameArray[0].getResult();
         } else {
            MathComponent baseFramex = frameArray[Mth.clamp(deltaFloorx, 0, frameEnd)];
            MathComponent beforeFramex = frameArray[Mth.clamp(deltaFloorx - 1, 0, frameEnd)];
            MathComponent nextFramex = frameArray[Mth.clamp(deltaFloorx + 1, 0, frameEnd)];
            MathComponent afterFramex = frameArray[Mth.clamp(deltaFloorx + 2, 0, frameEnd)];
            float individualFrameDeltax = Mth.frac(deltaRawx);
            return Mth.catmullrom(individualFrameDeltax, beforeFramex.getResult(), baseFramex.getResult(), nextFramex.getResult(), afterFramex.getResult());
         }
      };
      if (delta.isConstant()) {
         float deltaRaw = delta.getResult();
         int deltaFloor = Mth.floor(deltaRaw);
         if (deltaFloor >= frameEnd) {
            this.setOptimizedAlternativeToThis(frameArray[frameEnd]);
         } else if (deltaFloor <= 0) {
            this.setOptimizedAlternativeToThis(frameArray[0]);
         } else {
            MathComponent baseFrame = frameArray[Mth.clamp(deltaFloor, 0, frameEnd)];
            MathComponent beforeFrame = frameArray[Mth.clamp(deltaFloor - 1, 0, frameEnd)];
            MathComponent nextFrame = frameArray[Mth.clamp(deltaFloor + 1, 0, frameEnd)];
            MathComponent afterFrame = frameArray[Mth.clamp(deltaFloor + 2, 0, frameEnd)];
            float individualFrameDelta = Mth.frac(deltaRaw);
            this.setOptimizedAlternativeToThis(
               new MathConstant(
                  Mth.catmullrom(individualFrameDelta, beforeFrame.getResult(), baseFrame.getResult(), nextFrame.getResult(), afterFrame.getResult())
               )
            );
         }
      }

      this.setSupplierAndOptimize(supplier, this.parsedArgs);
   }

   public static float keyFrameStatic(float deltaRaw, float... frameArray) {
      int deltaFloor = Mth.floor(deltaRaw);
      int frameEnd = frameArray.length - 1;
      if (deltaFloor >= frameEnd) {
         return frameArray[frameEnd];
      } else if (deltaFloor <= 0) {
         return frameArray[0];
      } else {
         float baseFrame = frameArray[Mth.clamp(deltaFloor, 0, frameEnd)];
         float beforeFrame = frameArray[Mth.clamp(deltaFloor - 1, 0, frameEnd)];
         float nextFrame = frameArray[Mth.clamp(deltaFloor + 1, 0, frameEnd)];
         float afterFrame = frameArray[Mth.clamp(deltaFloor + 2, 0, frameEnd)];
         float individualFrameDelta = Mth.frac(deltaRaw);
         return Mth.catmullrom(individualFrameDelta, beforeFrame, baseFrame, nextFrame, afterFrame);
      }
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

      mv.visitMethodInsn(184, "traben/entity_model_features/models/animation/math/methods/emf/KeyframeMethod", "keyFrameStatic", "(F[F)F", false);
      vars.scopePop();
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount >= 3;
   }
}
