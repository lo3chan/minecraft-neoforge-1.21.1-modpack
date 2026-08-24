package net.raphimc.immediatelyfast.injection.mixins.core.compat.iris;

import net.minecraft.client.renderer.RenderBuffers;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net/irisshaders/iris/shadows/ShadowRenderer"},
   remap = false
)
@Pseudo
public abstract class MixinIris_ShadowRenderer {
   @Shadow
   @Final
   private RenderBuffers buffers;

   @Inject(
      method = {"renderShadows"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"
      )}
   )
   private void clearDataFromModsWhichRenderIntoTheWrongBuffer(CallbackInfo ci) {
      if (this.buffers.outlineBufferSource().outlineBufferSource instanceof BatchableBufferSource batchableBufferSource) {
         batchableBufferSource.close();
      }
   }
}
