package traben.entity_model_features.mixin.mixins.rendering.feature;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAttachments;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;

@Mixin({ItemInHandLayer.class})
public class MixinHeldItemFeatureRenderer {
   private static final String RENDER_ARM = "renderArmWithItem";

   @Inject(
      method = {"renderArmWithItem"},
      at = {@At("HEAD")}
   )
   private void emf$setHand(CallbackInfo ci, @Local HumanoidArm arm, @Share("armOverride") LocalRef<EMFAttachments> armOverride) {
      EMFAnimationEntityContext.setInHand = true;
      EMFEntityRenderState state = EMFAnimationEntityContext.getEmfState();
      if (state != null) {
         armOverride.set(arm == HumanoidArm.RIGHT ? state.rightArmOverride() : state.leftArmOverride());
      }
   }

   @Inject(
      method = {"renderArmWithItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
         shift = Shift.AFTER
      )}
   )
   private void emf$transforms(
      CallbackInfo ci,
      @Local(argsOnly = true) PoseStack matrices,
      @Share("armOverride") LocalRef<EMFAttachments> armOverride,
      @Share("needsPop") LocalBooleanRef needsToPop
   ) {
      if (armOverride.get() != null) {
         Pose entry = ((EMFAttachments)armOverride.get()).pose;
         if (entry != null) {
            needsToPop.set(true);
            matrices.poseStack.addLast(entry);
         }
      }
   }

   @Inject(
      method = {"renderArmWithItem"},
      at = {@At("TAIL")}
   )
   private void emf$unsetHand(CallbackInfo ci, @Local(argsOnly = true) PoseStack matrices, @Share("needsPop") LocalBooleanRef needsToPop) {
      EMFAnimationEntityContext.setInHand = false;
      if (needsToPop.get()) {
         matrices.popPose();
      }
   }
}
