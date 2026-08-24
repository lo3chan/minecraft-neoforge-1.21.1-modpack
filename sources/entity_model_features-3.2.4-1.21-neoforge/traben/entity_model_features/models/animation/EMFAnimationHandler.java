package traben.entity_model_features.models.animation;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.parts.EMFModelPart;

public abstract class EMFAnimationHandler {
   private final List<EMFAnimationHandler.AnimLineData> animLineDataList;
   public final String modelName;
   private boolean blockFutureAnimation = false;

   public EMFAnimationHandler(String modelName, List<EMFAnimationHandler.AnimLineData> animLineDataList) {
      this.modelName = modelName;
      this.animLineDataList = animLineDataList;
   }

   public void addAnimLineData(EMFAnimationHandler.AnimLineData animLineData) {
      this.animLineDataList.add(animLineData);
      animLineData.lineIndex = this.animLineDataList.size() - 1;
   }

   public List<EMFAnimationHandler.AnimLineData> lines() {
      return this.animLineDataList;
   }

   public EMFAnimationHandler.AnimLineData getLine(String animKey) {
      for (EMFAnimationHandler.AnimLineData animLineData : this.animLineDataList) {
         if (animLineData.animKey.equals(animKey)) {
            return animLineData;
         }
      }

      return null;
   }

   public final void animate(ModelPart[] pausedParts) throws Throwable {
      if (!this.blockFutureAnimation) {
         try {
            this.animateInner(pausedParts);
         } catch (Throwable var3) {
            this.blockFutureAnimation = true;
            throw var3;
         }
      }
   }

   protected abstract void animateInner(ModelPart[] var1) throws Throwable;

   public abstract boolean finishAndValidate();

   @Override
   public String toString() {
      return "EMFAnimationHandler for " + this.modelName + ", type is " + this.getClass().getName();
   }

   public static final class AnimLineData {
      public final String animKey;
      public final String expression;
      @Nullable
      public final EMFModelPart partToApplyTo;
      @Nullable
      public final EMFModelOrRenderVariable applier;
      public final boolean isBoolean;
      public final boolean isVar;
      public final boolean isVarGlobal;
      public int asmIndex = -1;
      private int lineIndex = -1;

      public AnimLineData(String animKey, String expression, @Nullable EMFModelPart partToApplyTo, @Nullable EMFModelOrRenderVariable applier) {
         this.animKey = animKey;
         this.expression = expression;
         this.partToApplyTo = partToApplyTo;
         this.applier = applier;
         boolean animKeyIsBoolean = animKey.startsWith("varb.") || animKey.startsWith("global_varb.");
         this.isBoolean = animKeyIsBoolean || applier != null && applier.isBoolean();
         this.isVar = animKeyIsBoolean || animKey.startsWith("var.") || animKey.startsWith("global_var.");
         this.isVarGlobal = this.isVar && animKey.startsWith("global_var");
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.lineIndex);
      }

      @Override
      public String toString() {
         return "AnimLineData{animKey='"
            + this.animKey
            + "', expression='"
            + this.expression
            + "', partToApplyTo?="
            + (this.partToApplyTo != null)
            + ", applier?="
            + (this.applier != null)
            + ", isBoolean="
            + this.isBoolean
            + ", isVar="
            + this.isVar
            + ", isVarGlobal="
            + this.isVarGlobal
            + ", asmIndex="
            + this.asmIndex
            + "}";
      }
   }
}
