package com.aetherteam.aether.client.particle;

import net.minecraft.client.particle.SnowflakeParticle.Provider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AetherParticleTypes {
   public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "aether");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AETHER_PORTAL = PARTICLES.register(
      "aether_portal", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CRYSTAL_LEAVES = PARTICLES.register(
      "crystal_leaves", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BOSS_DOORWAY_BLOCK = PARTICLES.register("door", () -> new SimpleParticleType(true));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EVIL_WHIRLWIND = PARTICLES.register(
      "evil_whirlwind", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FROZEN = PARTICLES.register("frozen", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLDEN_OAK_LEAVES = PARTICLES.register(
      "golden_oak_leaves", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOLIDAY_LEAVES = PARTICLES.register(
      "holiday_leaves", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PASSIVE_WHIRLWIND = PARTICLES.register(
      "passive_whirlwind", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ZEPHYR_SNOWFLAKE = PARTICLES.register(
      "zephyr_snowflake", () -> new SimpleParticleType(false)
   );

   public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)AETHER_PORTAL.get(), AetherPortalParticle.Factory::new);
      event.registerSpriteSet((ParticleType)CRYSTAL_LEAVES.get(), CrystalLeavesParticle.Factory::new);
      event.registerSpriteSet((ParticleType)BOSS_DOORWAY_BLOCK.get(), DungeonBlockOverlayParticle.Factory::new);
      event.registerSpriteSet((ParticleType)EVIL_WHIRLWIND.get(), EvilWhirlwindParticle.Factory::new);
      event.registerSpriteSet((ParticleType)FROZEN.get(), FrozenParticle.Factory::new);
      event.registerSpriteSet((ParticleType)GOLDEN_OAK_LEAVES.get(), GoldenOakLeavesParticle.Factory::new);
      event.registerSpriteSet((ParticleType)HOLIDAY_LEAVES.get(), HolidayLeavesParticle.Factory::new);
      event.registerSpriteSet((ParticleType)PASSIVE_WHIRLWIND.get(), PassiveWhirlwindParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ZEPHYR_SNOWFLAKE.get(), Provider::new);
   }
}
