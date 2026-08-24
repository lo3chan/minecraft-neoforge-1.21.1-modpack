package net.raphimc.immediatelyfast.injection.mixins.hud_batching.compat;

import com.mojang.blaze3d.vertex.BufferUploader;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BufferUploader.class})
public abstract class MixinBufferRenderer {
   @Inject(
      method = {"draw", "_drawWithShader"},
      at = {@At("HEAD")}
   )
   private static void checkForDrawCallWhileBatching(CallbackInfo ci) {
      if (BatchingBuffers.isHudBatching()) {
         BatchingBuffers.tryForceDrawHudBuffers();
      }
   }
}
