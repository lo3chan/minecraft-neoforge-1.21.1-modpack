package net.raphimc.immediatelyfast.injection.mixins.hud_batching.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {RenderSystem.class},
   remap = false
)
public abstract class MixinRenderSystem {
   @Inject(
      method = {"applyModelViewMatrix", "backupProjectionMatrix", "restoreProjectionMatrix"},
      at = {@At("HEAD")}
   )
   private static void checkForModificationWhileBatching(CallbackInfo ci) {
      if (BatchingBuffers.isHudBatching()) {
         BatchingBuffers.tryForceDrawHudBuffers();
      }
   }
}
