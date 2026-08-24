package net.irisshaders.iris.compat.sodium.mixin;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SodiumWorldRenderer.class})
public class MixinSodiumWorldRenderer {
   @Unique
   private float lastSunAngle;

   @Redirect(
      method = {"setupTerrain"},
      remap = false,
      at = @At(
         value = "INVOKE",
         target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;needsUpdate()Z",
         ordinal = 0,
         remap = false
      )
   )
   private boolean iris$forceChunkGraphRebuildInShadowPass(RenderSectionManager instance) {
      if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
         float sunAngle = Minecraft.getInstance().level.getSunAngle(CapturedRenderingState.INSTANCE.getTickDelta());
         if (this.lastSunAngle != sunAngle) {
            this.lastSunAngle = sunAngle;
            return true;
         }
      }

      return instance.needsUpdate();
   }

   @Redirect(
      method = {"setupTerrain"},
      remap = false,
      at = @At(
         value = "INVOKE",
         target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSectionManager;needsUpdate()Z",
         ordinal = 1,
         remap = false
      )
   )
   private boolean iris$forceEndGraphRebuild(RenderSectionManager instance) {
      return ShadowRenderingState.areShadowsCurrentlyBeingRendered() ? false : instance.needsUpdate();
   }

   @Inject(
      method = {"isEntityVisible"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void iris$skipEntityCheck(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (ShadowRenderingState.areShadowsCurrentlyBeingRendered()) {
         cir.setReturnValue(true);
      }
   }
}
