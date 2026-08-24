package traben.entity_texture_features.mixin.mixins.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer<T extends Entity> {
   private static final String RENDER = "render";

   @Inject(
      method = {"getPackedLightCoords"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void etf$vanillaLightOverrideCancel(T entity, float tickDelta, CallbackInfoReturnable<Integer> cir) {
      cir.setReturnValue(ETF.config().getConfig().getLightOverride(entity, tickDelta, (Integer)cir.getReturnValue()));
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void etf$protectPostRenderersLikeNametag(CallbackInfo ci) {
      ETFRenderContext.preventRenderLayerTextureModify();
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void etf$revertForRenderersThatCallSuperFirst(CallbackInfo ci) {
      ETFRenderContext.allowRenderLayerTextureModify();
   }
}
