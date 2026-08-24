package com.seibel.distanthorizons.neoforge.mixins.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_neoforge;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LightTexture.class})
public class MixinLightTexture {
   @Shadow
   @Final
   private NativeImage lightPixels;
   @Unique
   private MinecraftRenderWrapper_neoforge renderWrapper = null;

   @Inject(
      method = {"updateLightTexture(F)V"},
      at = {@At("RETURN")}
   )
   public void updateLightTexture(float partialTicks, CallbackInfo ci) {
      if (this.renderWrapper == null) {
         this.renderWrapper = (MinecraftRenderWrapper_neoforge)SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
      }

      this.renderWrapper.updateLightmap(this.lightPixels);
   }
}
