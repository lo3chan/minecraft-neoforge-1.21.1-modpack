package com.aetherteam.cumulus.mixin.mixins.client;

import com.aetherteam.cumulus.client.WorldDisplayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PanoramaRenderer.class})
public class PanoramaRendererMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"render(Lnet/minecraft/client/gui/GuiGraphics;IIFF)V"},
      cancellable = true
   )
   public void render(GuiGraphics guiGraphics, int width, int height, float fade, float partialTick, CallbackInfo ci) {
      if (Minecraft.getInstance().level != null && WorldDisplayHelper.isActive()) {
         ci.cancel();
      }
   }
}
