package net.blay09.mods.balm.core.particles.internal;

import java.util.function.Function;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistrar;
import net.blay09.mods.balm.core.particles.BalmParticleTypeRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractBalmParticleTypeRegistrarImpl implements BalmParticleTypeRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public AbstractBalmParticleTypeRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public <TOptions extends ParticleOptions, TType extends ParticleType<TOptions>> BalmParticleTypeRegistration<TType> register(
      String name, Function<ResourceLocation, TType> constructor
   ) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<ParticleType<?>> key = ResourceKey.create(Registries.PARTICLE_TYPE, id);
      Holder<ParticleType<?>> holder = this.registrar.register(key, constructor::apply);
      return new AbstractBalmParticleTypeRegistrarImpl.BalmParticleTypeRegistrationImpl<>(holder);
   }

   @Override
   public BalmParticleTypeRegistration<SimpleParticleType> register(String name, boolean overrideLimiter) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<ParticleType<?>> key = ResourceKey.create(Registries.PARTICLE_TYPE, identifier);
      Holder<ParticleType<?>> holder = this.registrar.register(key, id -> this.createSimple(overrideLimiter));
      return new AbstractBalmParticleTypeRegistrarImpl.BalmParticleTypeRegistrationImpl(holder);
   }

   private static class BalmParticleTypeRegistrationImpl<TOptions extends ParticleOptions, TType extends ParticleType<TOptions>>
      implements BalmParticleTypeRegistration<TType> {
      private final Holder<TType> holder;

      private BalmParticleTypeRegistrationImpl(Holder<?> holder) {
         this.holder = (Holder<TType>)holder;
      }

      @Override
      public Holder<TType> asHolder() {
         return this.holder;
      }
   }
}
