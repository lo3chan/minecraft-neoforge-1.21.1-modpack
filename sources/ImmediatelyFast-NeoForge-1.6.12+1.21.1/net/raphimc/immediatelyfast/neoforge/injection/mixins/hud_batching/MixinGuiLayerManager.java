package net.raphimc.immediatelyfast.neoforge.injection.mixins.hud_batching;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiLayerManager.class})
public abstract class MixinGuiLayerManager {
   @Inject(
      method = {"renderInner"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;",
         shift = Shift.AFTER,
         remap = false
      )},
      slice = {@Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/LayeredDraw$Layer;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
         )
      )}
   )
   private void renderBatch(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      if (ImmediatelyFast.runtimeConfig.hud_batching) {
         context.flush();
      }
   }
}
