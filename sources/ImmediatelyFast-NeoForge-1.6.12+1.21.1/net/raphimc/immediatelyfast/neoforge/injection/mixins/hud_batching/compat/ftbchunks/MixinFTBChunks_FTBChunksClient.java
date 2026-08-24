package net.raphimc.immediatelyfast.neoforge.injection.mixins.hud_batching.compat.ftbchunks;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"dev/ftb/mods/ftbchunks/client/FTBChunksClient"},
   remap = false
)
@Pseudo
public abstract class MixinFTBChunks_FTBChunksClient {
   @Inject(
      method = {"renderHud"},
      at = {@At("HEAD")}
   )
   private void forceDrawBatch(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics drawContext) {
      if (ImmediatelyFast.runtimeConfig.hud_batching) {
         drawContext.flush();
      }
   }
}
