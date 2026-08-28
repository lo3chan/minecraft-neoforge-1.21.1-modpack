/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.WaterDropParticle
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.weather;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.mixins.weather.MixinParticleAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.WaterDropParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WaterDropParticle.class})
public class MixinWaterDropParticle {
    @Inject(at={@At(value="TAIL")}, method={"<init>"})
    private void physicsmod$constructor(ClientLevel clientLevel, double d, double e, double f, CallbackInfo info) {
        if (ConfigClient.weatherParticles) {
            ((MixinParticleAccessor)((Object)this)).setAlpha((float)((int)((float)(175 + (int)((double)Math.random() * 40.0)) * ConfigClient.particleRainOpacity)) / 255.0f);
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"getRenderType"}, cancellable=true)
    private void physicsmod$makeParticleCompatibleWithTransparency(CallbackInfoReturnable<ParticleRenderType> info) {
        if (ConfigClient.weatherParticles) {
            info.setReturnValue((Object)ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
        }
    }
}

