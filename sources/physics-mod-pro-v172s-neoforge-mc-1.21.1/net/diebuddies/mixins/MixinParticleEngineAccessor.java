package net.diebuddies.mixins;

import java.util.Map;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleEngine.MutableSpriteSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ParticleEngine.class})
public interface MixinParticleEngineAccessor {
   @Accessor("providers")
   Map<ResourceLocation, ParticleProvider<?>> getParticleProviders();

   @Accessor("spriteSets")
   Map<ResourceLocation, MutableSpriteSet> getSpriteSets();
}
