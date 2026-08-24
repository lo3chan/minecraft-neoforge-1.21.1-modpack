package traben.entity_model_features.mixin.mixins.rendering.arrows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.IEMFCustomModelHolder;

@Mixin({ArrowRenderer.class})
public abstract class MixinProjectileEntityRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
   public MixinProjectileEntityRenderer(Context context) {
      super(context);
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;last()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   private void emf$cancelAndCEMRender(
      T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci
   ) {
      if (this instanceof IEMFCustomModelHolder customModelHolder && customModelHolder.emf$hasModel()) {
         poseStack.scale(16.0F, -12.8F, -12.8F);
         EMFAnimationEntityContext.setHeadYaw(entityYaw);
         float s = entity.shakeTime - partialTicks;
         EMFAnimationEntityContext.setHeadPitch(-Mth.sin(s * 3.0F) * s);
         VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
         customModelHolder.emf$getModel().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
         ci.cancel();
      }
   }
}
