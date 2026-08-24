package net.raphimc.immediatelyfast.injection.mixins.hud_batching.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.RenderType;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {DebugScreenOverlay.class},
   priority = 1500
)
public abstract class MixinDebugHud {
   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void fixDrawOrder(GuiGraphics context, CallbackInfo ci) {
      if (context.bufferSource instanceof BatchableBufferSource batchableBufferSource) {
         batchableBufferSource.drawDirect(RenderType.gui());
         batchableBufferSource.drawDirect(RenderType.guiOverlay());
      }
   }
}
