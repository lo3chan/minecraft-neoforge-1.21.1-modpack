package dev.latvian.mods.kubejs.misc;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ComplexParticleType extends ParticleType<ParticleOptions> {
   public final transient ParticleTypeBuilder builder;

   public ComplexParticleType(ParticleTypeBuilder builder) {
      super(builder.overrideLimiter);
      this.builder = builder;
   }

   public MapCodec<ParticleOptions> codec() {
      return this.builder.codec;
   }

   public StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptions> streamCodec() {
      return this.builder.streamCodec;
   }
}
