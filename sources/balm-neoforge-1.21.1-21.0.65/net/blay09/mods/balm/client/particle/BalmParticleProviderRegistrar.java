package net.blay09.mods.balm.client.particle;

import java.util.function.Function;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface BalmParticleProviderRegistrar {
   <T extends ParticleOptions> void register(Holder<? extends ParticleType<T>> var1, Function<SpriteSet, ParticleProvider<T>> var2);

   <T extends ParticleOptions> void register(Holder<? extends ParticleType<T>> var1, ParticleProvider<T> var2);
}
