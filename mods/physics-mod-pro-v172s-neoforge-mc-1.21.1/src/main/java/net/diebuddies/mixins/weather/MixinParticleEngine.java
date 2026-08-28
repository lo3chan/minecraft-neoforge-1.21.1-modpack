/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.ParticleEngine
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.weather;

import net.diebuddies.minecraft.weather.WeatherEffects;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ParticleEngine.class})
public class MixinParticleEngine {
    @Inject(at={@At(value="HEAD")}, method={"tick"})
    public void tick(CallbackInfo info) {
        WeatherEffects.aliveParticles = 0;
    }

    @Inject(at={@At(value="TAIL")}, method={"tick"})
    public void invalidateLight(CallbackInfo info) {
        WeatherEffects.invalidateLight = false;
    }
}

