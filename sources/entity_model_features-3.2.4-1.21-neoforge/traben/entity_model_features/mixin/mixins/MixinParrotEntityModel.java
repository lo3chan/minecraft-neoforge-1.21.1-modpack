package traben.entity_model_features.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin({ParrotModel.class})
public class MixinParrotEntityModel {
   @Inject(
      method = {"renderOnShoulder"},
      at = {@At("HEAD")}
   )
   private void emf$parrot1(
      PoseStack matrices,
      VertexConsumer vertexConsumer,
      int light,
      int overlay,
      float limbAngle,
      float limbDistance,
      float headYaw,
      float headPitch,
      int danceAngle,
      CallbackInfo ci
   ) {
      EMFAnimationEntityContext.setCurrentEntityOnShoulder(true);
   }

   @Inject(
      method = {"renderOnShoulder"},
      at = {@At("TAIL")}
   )
   private void emf$parrot2(
      PoseStack matrices,
      VertexConsumer vertexConsumer,
      int light,
      int overlay,
      float limbAngle,
      float limbDistance,
      float headYaw,
      float headPitch,
      int danceAngle,
      CallbackInfo ci
   ) {
      EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
   }
}
