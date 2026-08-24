package net.diebuddies.mixins.weather;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.WaterDropParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WaterDropParticle.class})
public class MixinWaterDropParticle {
   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   private void physicsmod$constructor(ClientLevel clientLevel, double d, double e, double f, CallbackInfo info) {
      if (ConfigClient.weatherParticles) {
         ((MixinParticleAccessor)this).setAlpha((int)((175 + (int)(Math.random() * 40.0)) * ConfigClient.particleRainOpacity) / 255.0F);
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"getRenderType"},
      cancellable = true
   )
   private void physicsmod$makeParticleCompatibleWithTransparency(CallbackInfoReturnable<ParticleRenderType> info) {
      if (ConfigClient.weatherParticles) {
         info.setReturnValue(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
      }
   }
}
