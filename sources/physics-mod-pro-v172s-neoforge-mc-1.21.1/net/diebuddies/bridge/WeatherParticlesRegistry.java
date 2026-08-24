package net.diebuddies.bridge;

import net.diebuddies.minecraft.weather.DustParticle;
import net.diebuddies.minecraft.weather.RainParticle;
import net.diebuddies.minecraft.weather.SnowParticle;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.physics.ocean.ExplosionOceanSplashParticle;
import net.diebuddies.physics.ocean.OceanSplashParticle;
import net.diebuddies.physics.ocean.SmallOceanSplashParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine.MutableSpriteSet;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class WeatherParticlesRegistry {
   public static final ResourceLocation RAIN_RESOURCE = ResourceLocation.fromNamespaceAndPath("physicsmod", "rain");
   public static final ResourceLocation SNOW_RESOURCE = ResourceLocation.fromNamespaceAndPath("physicsmod", "snow");
   public static final ResourceLocation DUST_RESOURCE = ResourceLocation.fromNamespaceAndPath("physicsmod", "dust");
   public static final ResourceLocation SPLASH_RESOURCE = ResourceLocation.fromNamespaceAndPath("physicsmod", "splash");
   public static final ResourceLocation SPLASH_SMALL_RESOURCE = ResourceLocation.fromNamespaceAndPath("physicsmod", "splash_small");
   public static final ResourceLocation SPLASH_EXPLOSION_RESOURCE = ResourceLocation.fromNamespaceAndPath("physicsmod", "splash_explosion");

   public static void register(IEventBus modEventBus) {
      modEventBus.register(WeatherParticlesRegistry.class);
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      registerSpriteSet(RAIN_RESOURCE, sprite -> new RainParticle.Provider(sprite));
      registerSpriteSet(SNOW_RESOURCE, sprite -> new SnowParticle.Provider(sprite));
      registerSpriteSet(DUST_RESOURCE, sprite -> new DustParticle.Provider(sprite));
      registerSpriteSet(SPLASH_RESOURCE, sprite -> new OceanSplashParticle.Provider(sprite));
      registerSpriteSet(SPLASH_SMALL_RESOURCE, sprite -> new SmallOceanSplashParticle.Provider(sprite));
      registerSpriteSet(SPLASH_EXPLOSION_RESOURCE, sprite -> new ExplosionOceanSplashParticle.Provider(sprite));
   }

   private static <T extends ParticleOptions> void registerSpriteSet(ResourceLocation resource, SpriteParticleRegistration<T> registration) {
      MixinParticleEngineAccessor particleEngine = (MixinParticleEngineAccessor)Minecraft.getInstance().particleEngine;
      MutableSpriteSet spriteSet = new MutableSpriteSet();
      particleEngine.getSpriteSets().put(resource, spriteSet);
      particleEngine.getParticleProviders().put(resource, registration.create(spriteSet));
   }
}
