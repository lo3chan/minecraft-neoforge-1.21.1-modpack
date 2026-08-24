package net.blay09.mods.balm.core.particles;

import java.util.function.Function;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

public interface BalmParticleTypeRegistrar {
   <TOptions extends ParticleOptions, TType extends ParticleType<TOptions>> BalmParticleTypeRegistration<TType> register(
      String var1, Function<ResourceLocation, TType> var2
   );

   BalmParticleTypeRegistration<SimpleParticleType> register(String var1, boolean var2);

   SimpleParticleType createSimple(boolean var1);
}
