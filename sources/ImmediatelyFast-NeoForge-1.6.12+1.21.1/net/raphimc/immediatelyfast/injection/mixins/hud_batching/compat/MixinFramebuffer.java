package net.raphimc.immediatelyfast.injection.mixins.hud_batching.compat;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderTarget.class})
public abstract class MixinFramebuffer {
   @Inject(
      method = {"_blitToScreen"},
      at = {@At("HEAD")}
   )
   private void checkForDrawCallWhileBatching(CallbackInfo ci) {
      if (BatchingBuffers.isHudBatching()) {
         BatchingBuffers.tryForceDrawHudBuffers();
      }
   }
}
