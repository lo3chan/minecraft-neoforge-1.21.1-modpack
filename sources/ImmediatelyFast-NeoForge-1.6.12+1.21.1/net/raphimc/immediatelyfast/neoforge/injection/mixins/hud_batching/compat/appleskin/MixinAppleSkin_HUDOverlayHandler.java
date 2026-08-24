package net.raphimc.immediatelyfast.neoforge.injection.mixins.hud_batching.compat.appleskin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.raphimc.immediatelyfast.ImmediatelyFast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"squeek/appleskin/client/HUDOverlayHandler"},
   remap = false
)
@Pseudo
public abstract class MixinAppleSkin_HUDOverlayHandler {
   @Inject(
      method = {"drawExhaustionOverlay(FLnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/gui/GuiGraphics;IIF)V"},
      at = {@At("RETURN")},
      remap = true
   )
   private static void forceDrawBatch(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics drawContext) {
      if (ImmediatelyFast.runtimeConfig.hud_batching) {
         drawContext.flush();
      }
   }
}
