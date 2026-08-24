package net.raphimc.immediatelyfast.injection.mixins.hud_batching.compat.xaerominimap;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"xaero/common/events/ClientEvents"},
   remap = false
)
@Pseudo
public abstract class MixinXaerosMinimap_ClientEvents {
   @Inject(
      method = {"handleRenderGameOverlayEventPre"},
      at = {@At("HEAD")}
   )
   private void forceDrawBatch(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics drawContext) {
      if (ImmediatelyFast.runtimeConfig.hud_batching) {
         drawContext.flush();
      }
   }
}
