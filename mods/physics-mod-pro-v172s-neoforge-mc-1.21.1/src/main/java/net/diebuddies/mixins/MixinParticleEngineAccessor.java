/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleEngine$MutableSpriteSet
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.diebuddies.mixins;

import java.util.Map;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ParticleEngine.class})
public interface MixinParticleEngineAccessor {
    @Accessor(value="providers")
    public Map<ResourceLocation, ParticleProvider<?>> getParticleProviders();

    @Accessor(value="spriteSets")
    public Map<ResourceLocation, ParticleEngine.MutableSpriteSet> getSpriteSets();
}

