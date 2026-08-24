package traben.entity_model_features.mixin.mixins;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Mixin({GameRenderer.class})
public class MixinGameRenderer {
   @Inject(
      method = {"getFov"},
      at = {@At("RETURN")}
   )
   private void emf$captureFov(CallbackInfoReturnable<Double> cir) {
      if (((EMFConfig)EMF.config().getConfig()).animationLODDistance != 0) {
         EMFAnimationEntityContext.lastFOV = (Double)cir.getReturnValue();
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void emf$injectCounter(CallbackInfo ci) {
      EMFAnimationEntityContext.incFrameCount();
   }
}
