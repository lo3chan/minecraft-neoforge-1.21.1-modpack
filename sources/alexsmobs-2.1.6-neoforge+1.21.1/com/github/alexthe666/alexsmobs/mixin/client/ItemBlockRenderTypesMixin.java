package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.citadel.client.event.EventGetFluidRenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemBlockRenderTypes.class})
public class ItemBlockRenderTypesMixin {
   @Inject(
      at = {@At("TAIL")},
      cancellable = true,
      method = {"Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderLayer(Lnet/minecraft/world/level/material/FluidState;)Lnet/minecraft/client/renderer/RenderType;"}
   )
   private static void alexsmobs_getFluidRenderLayer(FluidState fluidState, CallbackInfoReturnable<RenderType> cir) {
      EventGetFluidRenderType event = new EventGetFluidRenderType(fluidState, (RenderType)cir.getReturnValue());
      event.post();
      if (event.isHandled()) {
         cir.setReturnValue(event.getRenderType());
      }
   }
}
