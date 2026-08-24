package net.raphimc.immediatelyfast.injection.mixins.hud_batching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import net.raphimc.immediatelyfast.feature.batching.BatchingBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @WrapOperation(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
      )}
   )
   private void hudBatching(Gui instance, GuiGraphics context, DeltaTracker tickCounter, Operation<Void> original) {
      if (ImmediatelyFast.runtimeConfig.hud_batching) {
         BatchingBuffers.runBatched(context, () -> original.call(new Object[]{instance, context, tickCounter}));
      } else {
         original.call(new Object[]{instance, context, tickCounter});
      }
   }
}
