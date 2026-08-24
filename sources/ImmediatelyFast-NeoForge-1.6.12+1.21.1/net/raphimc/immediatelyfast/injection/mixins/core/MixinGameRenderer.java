package net.raphimc.immediatelyfast.injection.mixins.core;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;"
      )
   )
   private BufferSource returnNonBatchingVertexConsumer(RenderBuffers instance) {
      return BatchingBuffers.getNonBatchingEntityVertexConsumers();
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V",
         shift = Shift.AFTER
      )}
   )
   private void drawDataFromModsWhichRenderIntoTheWrongBuffer(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
      Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
      Minecraft.getInstance().renderBuffers().outlineBufferSource().endOutlineBatch();
   }
}
