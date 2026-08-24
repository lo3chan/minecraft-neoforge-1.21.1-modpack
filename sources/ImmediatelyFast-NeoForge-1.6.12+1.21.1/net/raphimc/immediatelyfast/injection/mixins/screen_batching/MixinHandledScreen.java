package net.raphimc.immediatelyfast.injection.mixins.screen_batching;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {AbstractContainerScreen.class},
   priority = 500
)
public abstract class MixinHandledScreen {
   @Unique
   private BufferSource immediatelyFast$prevVertexConsumers;

   @Inject(
      method = {"render"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hoveredSlot:Lnet/minecraft/world/inventory/Slot;",
         ordinal = 0
      )}
   )
   private void beginBatching(GuiGraphics drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      this.immediatelyFast$prevVertexConsumers = BatchingBuffers.beginHudBatching(drawContext);
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"
      )}
   )
   private void endBatching(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      BatchingBuffers.endHudBatching(context, this.immediatelyFast$prevVertexConsumers);
   }
}
