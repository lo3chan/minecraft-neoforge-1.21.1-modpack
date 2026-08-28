/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.ParticleEngine$MutableSpriteSet
 *  net.minecraft.client.particle.ParticleEngine$SpriteParticleRegistration
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
 */
package net.diebuddies.bridge;

import net.diebuddies.minecraft.weather.DustParticle;
import net.diebuddies.minecraft.weather.RainParticle;
import net.diebuddies.minecraft.weather.SnowParticle;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.physics.ocean.ExplosionOceanSplashParticle;
import net.diebuddies.physics.ocean.OceanSplashParticle;
import net.diebuddies.physics.ocean.SmallOceanSplashParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class WeatherParticlesRegistry {
    public static final ResourceLocation RAIN_RESOURCE = ResourceLocation.fromNamespaceAndPath((String)"physicsmod", (String)"rain");
    public static final ResourceLocation SNOW_RESOURCE = ResourceLocation.fromNamespaceAndPath((String)"physicsmod", (String)"snow");
    public static final ResourceLocation DUST_RESOURCE = ResourceLocation.fromNamespaceAndPath((String)"physicsmod", (String)"dust");
    public static final ResourceLocation SPLASH_RESOURCE = ResourceLocation.fromNamespaceAndPath((String)"physicsmod", (String)"splash");
    public static final ResourceLocation SPLASH_SMALL_RESOURCE = ResourceLocation.fromNamespaceAndPath((String)"physicsmod", (String)"splash_small");
    public static final ResourceLocation SPLASH_EXPLOSION_RESOURCE = ResourceLocation.fromNamespaceAndPath((String)"physicsmod", (String)"splash_explosion");

    public static void register(IEventBus modEventBus) {
        modEventBus.register(WeatherParticlesRegistry.class);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        WeatherParticlesRegistry.registerSpriteSet(RAIN_RESOURCE, sprite -> new RainParticle.Provider(sprite));
        WeatherParticlesRegistry.registerSpriteSet(SNOW_RESOURCE, sprite -> new SnowParticle.Provider(sprite));
        WeatherParticlesRegistry.registerSpriteSet(DUST_RESOURCE, sprite -> new DustParticle.Provider(sprite));
        WeatherParticlesRegistry.registerSpriteSet(SPLASH_RESOURCE, sprite -> new OceanSplashParticle.Provider(sprite));
        WeatherParticlesRegistry.registerSpriteSet(SPLASH_SMALL_RESOURCE, sprite -> new SmallOceanSplashParticle.Provider(sprite));
        WeatherParticlesRegistry.registerSpriteSet(SPLASH_EXPLOSION_RESOURCE, sprite -> new ExplosionOceanSplashParticle.Provider(sprite));
    }

    private static <T extends ParticleOptions> void registerSpriteSet(ResourceLocation resource, ParticleEngine.SpriteParticleRegistration<T> registration) {
        MixinParticleEngineAccessor particleEngine = (MixinParticleEngineAccessor)Minecraft.getInstance().particleEngine;
        ParticleEngine.MutableSpriteSet spriteSet = new ParticleEngine.MutableSpriteSet();
        particleEngine.getSpriteSets().put(resource, spriteSet);
        particleEngine.getParticleProviders().put(resource, registration.create((SpriteSet)spriteSet));
    }
}

