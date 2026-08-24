package dev.architectury.registry.client.particle;

import dev.architectury.registry.registries.RegistrySupplier;
import java.util.List;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ParticleProviderRegistry {
   public static <T extends ParticleOptions> void register(RegistrySupplier<? extends ParticleType<T>> supplier, ParticleProvider<T> provider) {
      supplier.listen(it -> register((ParticleType<T>)it, provider));
   }

   public static <T extends ParticleOptions> void register(
      RegistrySupplier<? extends ParticleType<T>> supplier, ParticleProviderRegistry.DeferredParticleProvider<T> provider
   ) {
      supplier.listen(it -> register((ParticleType<T>)it, provider));
   }

   public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
   }

   public static <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProviderRegistry.DeferredParticleProvider<T> provider) {
   }

   @FunctionalInterface
   public interface DeferredParticleProvider<T extends ParticleOptions> {
      ParticleProvider<T> create(ParticleProviderRegistry.ExtendedSpriteSet var1);
   }

   public interface ExtendedSpriteSet extends SpriteSet {
      TextureAtlas getAtlas();

      List<TextureAtlasSprite> getSprites();
   }
}
