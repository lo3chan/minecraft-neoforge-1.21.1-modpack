package net.cibernet.alchemancy.registries;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.cibernet.alchemancy.client.particle.SparkParticle;
import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.minecraft.client.particle.FlameParticle.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class AlchemancyParticles {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, "alchemancy");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WARHAMMER_FLAME = REGISTRY.register(
      "warhammer_flame", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticleOptions>> SPARK = register(
      "spark", true, SparkParticleOptions::codec, SparkParticleOptions::streamCodec
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticleOptions>> GUST_DUST = register(
      "gust_dust", true, SparkParticleOptions::codec, SparkParticleOptions::streamCodec
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticleOptions>> CLOUD_SMOKE = register(
      "cloud_smoke", true, SparkParticleOptions::codec, SparkParticleOptions::streamCodec
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNO = REGISTRY.register("inferno", () -> new SimpleParticleType(true));

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)WARHAMMER_FLAME.get(), Provider::new);
      event.registerSpriteSet((ParticleType)SPARK.get(), SparkParticle.Provider::new);
      event.registerSpriteSet((ParticleType)GUST_DUST.get(), SparkParticle.Provider::new);
      event.registerSpriteSet((ParticleType)CLOUD_SMOKE.get(), SparkParticle.Provider::new);
      event.registerSpriteSet((ParticleType)INFERNO.get(), net.minecraft.client.particle.SpellParticle.Provider::new);
   }

   private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
      String name,
      boolean overrideLimitter,
      Function<ParticleType<T>, MapCodec<T>> codecGetter,
      Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
   ) {
      return REGISTRY.register(name, () -> new ParticleType<T>(overrideLimitter) {
         public MapCodec<T> codec() {
            return (MapCodec<T>)codecGetter.apply(this);
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return (StreamCodec<? super RegistryFriendlyByteBuf, T>)streamCodecGetter.apply(this);
         }
      });
   }
}
