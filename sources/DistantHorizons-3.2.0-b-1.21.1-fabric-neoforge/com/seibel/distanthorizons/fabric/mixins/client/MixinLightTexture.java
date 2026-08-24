package com.seibel.distanthorizons.fabric.mixins.client;

import com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper_fabric;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import net.minecraft.class_1011;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_765.class})
public class MixinLightTexture {
   @Shadow
   @Final
   private class_1011 field_4133;
   @Unique
   private MinecraftRenderWrapper_fabric renderWrapper = null;

   @Inject(
      method = {"updateLightTexture(F)V"},
      at = {@At("RETURN")}
   )
   public void updateLightTexture(float partialTicks, CallbackInfo ci) {
      if (this.renderWrapper == null) {
         this.renderWrapper = (MinecraftRenderWrapper_fabric)SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class);
      }

      this.renderWrapper.updateLightmap(this.field_4133);
   }
}
