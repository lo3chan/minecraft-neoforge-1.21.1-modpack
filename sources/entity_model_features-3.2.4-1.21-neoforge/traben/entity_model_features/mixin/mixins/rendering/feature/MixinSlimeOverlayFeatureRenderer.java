package traben.entity_model_features.mixin.mixins.rendering.feature;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin({SlimeOuterLayer.class})
public class MixinSlimeOverlayFeatureRenderer {
   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
      at = {@At("HEAD")}
   )
   private void emf$setLayerForOverrides(CallbackInfo ci) {
      EMFAnimationEntityContext.setLayerFactory(RenderType::entityTranslucent);
   }
}
