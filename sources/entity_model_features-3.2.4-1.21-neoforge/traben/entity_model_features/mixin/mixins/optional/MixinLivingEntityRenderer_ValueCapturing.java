package traben.entity_model_features.mixin.mixins.optional;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin(
   value = {LivingEntityRenderer.class},
   priority = 2000
)
public abstract class MixinLivingEntityRenderer_ValueCapturing<T extends LivingEntity, M extends EntityModel<T>>
   extends EntityRenderer<T>
   implements RenderLayerParent<T, M> {
   protected MixinLivingEntityRenderer_ValueCapturing(Context ctx) {
      super(ctx);
   }

   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
      ),
      index = 1,
      require = 0
   )
   private float emf$getLimbAngle(float limbAngle) {
      EMFAnimationEntityContext.setLimbAngle(limbAngle);
      return limbAngle;
   }

   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
      ),
      index = 2,
      require = 0
   )
   private float emf$getLimbDistance(float limbDistance) {
      EMFAnimationEntityContext.setLimbDistance(limbDistance);
      return limbDistance;
   }

   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
      ),
      index = 4,
      require = 0
   )
   private float emf$getHeadYaw(float headYaw) {
      if (!(headYaw >= 180.0F) && !(headYaw < -180.0F)) {
         EMFAnimationEntityContext.setHeadYaw(headYaw);
      } else {
         EMFAnimationEntityContext.setHeadYaw(Mth.wrapDegrees(headYaw));
      }

      return headYaw;
   }

   @ModifyArg(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
      ),
      index = 5,
      require = 0
   )
   private float emf$getHeadPitch(float headPitch) {
      EMFAnimationEntityContext.setHeadPitch(headPitch);
      return headPitch;
   }
}
