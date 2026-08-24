package traben.entity_model_features.mixin.mixins.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin({PlayerRenderer.class})
public abstract class MixinPlayerEntityRenderer {
   @Inject(
      method = {"renderHand"},
      at = {@At("HEAD")}
   )
   private void emf$setHand(CallbackInfo ci) {
      EMFAnimationEntityContext.setCurrentEntityIteration((EMFEntityRenderState)ETFEntityRenderState.forEntity((ETFEntity)Minecraft.getInstance().player));
      EMFAnimationEntityContext.isFirstPersonHand = true;
   }

   @Inject(
      method = {"renderHand"},
      at = {@At("RETURN")}
   )
   private void emf$unsetHand(CallbackInfo ci) {
      EMFAnimationEntityContext.reset();
   }
}
