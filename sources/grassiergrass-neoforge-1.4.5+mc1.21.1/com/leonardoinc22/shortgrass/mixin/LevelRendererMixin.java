package com.leonardoinc22.shortgrass.mixin;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class LevelRendererMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      method = {"setSectionDirty(IIIZ)V"},
      at = {@At("TAIL")}
   )
   private void grassiergrass$invalidateGrassRenderSection(int sectionX, int sectionY, int sectionZ, boolean reRenderOnMainThread, CallbackInfo ci) {
      ClientLevel level = this.minecraft.level;
      if (level != null) {
         GrassRenderPass.invalidateRenderSection(level, sectionX, sectionY, sectionZ);
      }
   }
}
