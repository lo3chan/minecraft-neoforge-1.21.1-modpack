package dev.latvian.mods.kubejs.misc;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.client.ParticleGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

@ReturnsSelf
public class ParticleTypeBuilder extends BuilderBase<ParticleType<?>> {
   public transient boolean overrideLimiter = false;
   public transient MapCodec<ParticleOptions> codec;
   public transient StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptions> streamCodec;
   public transient Consumer<ParticleGenerator> assetGen = gen -> gen.texture(this.id.toString());

   public ParticleTypeBuilder(ResourceLocation i) {
      super(i);
   }

   public ParticleType<?> createObject() {
      return (ParticleType<?>)(this.codec != null && this.streamCodec != null ? new ComplexParticleType(this) : new BasicParticleType(this.overrideLimiter));
   }

   public ParticleTypeBuilder overrideLimiter(boolean o) {
      this.overrideLimiter = o;
      return this;
   }

   public ParticleTypeBuilder codec(MapCodec<ParticleOptions> c) {
      this.codec = c;
      return this;
   }

   public ParticleTypeBuilder streamCodec(StreamCodec<? super RegistryFriendlyByteBuf, ParticleOptions> s) {
      this.streamCodec = s;
      return this;
   }

   public ParticleTypeBuilder textures(List<String> textures) {
      this.assetGen = g -> g.textures(textures);
      return this;
   }

   public ParticleTypeBuilder texture(String texture) {
      this.assetGen = g -> g.texture(texture);
      return this;
   }

   @Override
   public void generateAssets(KubeAssetGenerator generator) {
      generator.particle(this.id, this.assetGen);
   }
}
