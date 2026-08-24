package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin({ItemFrameRenderer.class})
public class MixinItemFrameEntityRenderer {
   @Inject(
      method = {"render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
         shift = Shift.AFTER
      )}
   )
   private void emf$setFrame(CallbackInfo ci) {
      EMFAnimationEntityContext.setInItemFrame = true;
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At("TAIL")}
   )
   private void emf$unsetFrame(CallbackInfo ci) {
      EMFAnimationEntityContext.setInItemFrame = false;
   }
}
