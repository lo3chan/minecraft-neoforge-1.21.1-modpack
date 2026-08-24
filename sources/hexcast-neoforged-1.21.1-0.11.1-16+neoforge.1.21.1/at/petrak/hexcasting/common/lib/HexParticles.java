package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.client.particles.ConjureParticle;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

public class HexParticles {
   private static final Map<ResourceLocation, ParticleType<?>> PARTICLES = new LinkedHashMap<>();
   public static final ConjureParticleOptions.Type CONJURE_PARTICLE = register("conjure_particle", new ConjureParticleOptions.Type(false));

   public static void registerParticles(BiConsumer<ParticleType<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, ParticleType<?>> e : PARTICLES.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <O extends ParticleOptions, T extends ParticleType<O>> T register(String id, T particle) {
      ParticleType<?> old = PARTICLES.put(HexAPI.modLoc(id), particle);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         return particle;
      }
   }

   public static class FactoryHandler {
      public static void registerFactories(HexParticles.FactoryHandler.Consumer consumer) {
         consumer.register(HexParticles.CONJURE_PARTICLE, ConjureParticle.Provider::new);
      }

      public interface Consumer {
         <T extends ParticleOptions> void register(ParticleType<T> var1, Function<SpriteSet, ParticleProvider<T>> var2);
      }
   }
}
