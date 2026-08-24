package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.citadel.client.event.EventGetStarBrightness;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientLevel.class})
public abstract class ClientLevelMixin {
   @Inject(
      at = {@At("RETURN")},
      method = {"Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"},
      cancellable = true
   )
   private void alexsmobs_getStarBrightness(float partialTicks, CallbackInfoReturnable<Float> cir) {
      EventGetStarBrightness event = new EventGetStarBrightness((ClientLevel)this, (Float)cir.getReturnValue(), partialTicks);
      event.post();
      if (event.isHandled()) {
         cir.setReturnValue(event.getBrightness());
      }
   }
}
