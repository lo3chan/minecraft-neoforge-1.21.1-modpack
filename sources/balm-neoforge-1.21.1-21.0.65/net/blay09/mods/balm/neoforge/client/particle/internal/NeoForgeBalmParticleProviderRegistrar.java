package net.blay09.mods.balm.neoforge.client.particle.internal;

import java.util.function.Function;
import net.blay09.mods.balm.client.particle.BalmParticleProviderRegistrar;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class NeoForgeBalmParticleProviderRegistrar implements BalmParticleProviderRegistrar {
   private final RegisterParticleProvidersEvent event;

   public NeoForgeBalmParticleProviderRegistrar(RegisterParticleProvidersEvent event) {
      this.event = event;
   }

   @Override
   public <T extends ParticleOptions> void register(Holder<? extends ParticleType<T>> particleType, Function<SpriteSet, ParticleProvider<T>> factory) {
      this.event.registerSpriteSet((ParticleType)particleType.value(), factory::apply);
   }

   @Override
   public <T extends ParticleOptions> void register(Holder<? extends ParticleType<T>> particleType, ParticleProvider<T> provider) {
      this.event.registerSpriteSet((ParticleType)particleType.value(), spriteSet -> provider);
   }
}
