package net.raphimc.immediatelyfast.injection.mixins.hud_batching;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayeredDraw.class})
public abstract class MixinLayeredDrawer {
   @Inject(
      method = {"renderInner"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/LayeredDraw$Layer;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
         shift = Shift.AFTER
      )}
   )
   private void renderBatch(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
      if (ImmediatelyFast.runtimeConfig.hud_batching) {
         context.flush();
      }
   }
}
