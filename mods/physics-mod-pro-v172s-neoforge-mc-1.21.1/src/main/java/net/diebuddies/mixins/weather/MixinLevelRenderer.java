/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.resources.sounds.TickableSoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.biome.Biome$Precipitation
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.weather;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.minecraft.weather.WindSoundInstance;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.wind.WeatherDomain;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private final Long2ObjectMap<Holder<Biome>> cachedBiomes = new Long2ObjectOpenHashMap(169);
    @Unique
    private WindSoundInstance windSound;

    @Inject(at={@At(value="TAIL")}, method={"renderLevel"})
    public void resetWeatherLightInvalidation(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo info) {
        WeatherEffects.invalidateLight = false;
    }

    @Inject(at={@At(value="HEAD")}, method={"setLevel"})
    public void setLevel(@Nullable ClientLevel clientLevel, CallbackInfo info) {
        if (this.windSound != null) {
            this.windSound.stopWind();
        }
        if (clientLevel != null) {
            this.windSound = new WindSoundInstance(WeatherEffects.WIND_SOUND_EVENT, SoundSource.WEATHER);
            this.minecraft.getSoundManager().queueTickingSound((TickableSoundInstance)this.windSound);
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"tickRain"}, cancellable=true)
    private void tickRain(Camera camera, CallbackInfo info) {
        if (!ConfigClient.weatherParticles) {
            return;
        }
        float precipitationAmount = this.minecraft.level.getRainLevel(1.0f);
        if (precipitationAmount <= 0.0f) {
            return;
        }
        int checkRange = 6;
        int camX = Mth.floor((double)camera.getPosition().x);
        int camY = Mth.floor((double)camera.getPosition().y);
        int camZ = Mth.floor((double)camera.getPosition().z);
        int spawnableRain = 0;
        int particlesPerBlock = 0;
        particlesPerBlock += (int)(precipitationAmount * (float)ConfigClient.weatherRainParticleAmount);
        particlesPerBlock += (int)(this.minecraft.level.getThunderLevel(1.0f) * (float)ConfigClient.weatherThunderParticleAmount);
        ClientLevel level = this.minecraft.level;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        this.cachedBiomes.clear();
        for (int zo = camZ - checkRange; zo <= camZ + checkRange; ++zo) {
            for (int xo = camX - checkRange; xo <= camX + checkRange; ++xo) {
                int diffX = xo - camX;
                int diffZ = zo - camZ;
                if (diffX * diffX + diffZ * diffZ > 36) continue;
                mutableBlockPos.set(xo, camY, zo);
                Holder biomeHolder = level.getBiome((BlockPos)mutableBlockPos);
                Biome biome = (Biome)biomeHolder.value();
                this.cachedBiomes.put(mutableBlockPos.asLong(), (Object)biomeHolder);
                if (biome.getPrecipitationAt((BlockPos)mutableBlockPos) == Biome.Precipitation.NONE && !biomeHolder.is(Biomes.DESERT)) continue;
                int rainToThisHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, xo, zo);
                int rainBottom = camY - checkRange;
                int rainTop = camY + checkRange;
                if (rainBottom < rainToThisHeight) {
                    rainBottom = rainToThisHeight;
                }
                if (rainTop < rainToThisHeight) {
                    rainTop = rainToThisHeight;
                }
                if (rainBottom == rainTop) continue;
                int height = rainTop - rainBottom;
                spawnableRain += height * particlesPerBlock;
            }
        }
        int range = 14;
        PhysicsWorld world = PhysicsMod.getInstance((Level)level).getPhysicsWorld();
        WeatherDomain weatherDomain = world.getWeatherDomain();
        Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)this.minecraft.particleEngine).getParticleProviders();
        ParticleProvider<?> rainProvider = provider.get(WeatherParticlesRegistry.RAIN_RESOURCE);
        ParticleProvider<?> snowProvider = provider.get(WeatherParticlesRegistry.SNOW_RESOURCE);
        ParticleProvider<?> dustProvider = provider.get(WeatherParticlesRegistry.DUST_RESOURCE);
        while (WeatherEffects.aliveParticles < spawnableRain) {
            int rainToThisHeight;
            double zo;
            double yo;
            double xo;
            double distance;
            while ((distance = (xo = (double)Math.random() * 2.0 - 1.0) * xo + (yo = (double)Math.random() * 2.0 - 1.0) * yo + (zo = (double)Math.random() * 2.0 - 1.0) * zo) > 1.0) {
            }
            int x = Mth.floor((double)(camera.getPosition().x + xo * (double)range));
            int y = Mth.floor((double)(camera.getPosition().y + yo * (double)range));
            int z = Mth.floor((double)(camera.getPosition().z + zo * (double)range));
            mutableBlockPos.set(x, camY, z);
            long blockPosUnique = mutableBlockPos.asLong();
            Holder biomeHolder = (Holder)this.cachedBiomes.computeIfAbsent(blockPosUnique, key -> level.getBiome((BlockPos)mutableBlockPos));
            Biome biome = (Biome)biomeHolder.value();
            if (biome.getPrecipitationAt((BlockPos)mutableBlockPos) == Biome.Precipitation.NONE && !biomeHolder.is(Biomes.DESERT) || y < (rainToThisHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z))) continue;
            int rainBottom = y - range;
            int rainTop = y + range;
            if (rainBottom < rainToThisHeight) {
                rainBottom = rainToThisHeight;
            }
            if (rainTop < rainToThisHeight) {
                rainTop = rainToThisHeight;
            }
            if (rainBottom == rainTop) continue;
            mutableBlockPos.set(x, rainBottom, z);
            level.getBlockState((BlockPos)mutableBlockPos).isAir();
            Vector3f windDirection = weatherDomain.getWindDirection(x, y, z);
            float forceStrength = weatherDomain.getWindStrengthFast();
            if (biomeHolder.is(Biomes.DESERT)) {
                strength = 0.3;
                baseVX = (double)Math.random() * strength - strength * 0.5;
                baseVY = -0.05;
                baseVZ = (double)Math.random() * strength - strength * 0.5;
                particleEngine.add(dustProvider.createParticle((ParticleOptions)WeatherEffects.PHYSICS_DUST, level, (double)((float)x + Math.random()), (double)((float)y + Math.random()), (double)((float)z + Math.random()), baseVX += (double)(windDirection.x * forceStrength) * 2.0, baseVY, baseVZ += (double)(windDirection.z * forceStrength) * 2.0));
            } else if (biome.warmEnoughToRain((BlockPos)mutableBlockPos)) {
                strength = 0.13;
                baseVX = (double)Math.random() * strength - strength * 0.5;
                baseVY = -0.6;
                baseVZ = (double)Math.random() * strength - strength * 0.5;
                particleEngine.add(rainProvider.createParticle((ParticleOptions)WeatherEffects.PHYSICS_RAIN, level, (double)((float)x + Math.random()), (double)((float)y + Math.random()), (double)((float)z + Math.random()), baseVX += (double)(windDirection.x * forceStrength), baseVY, baseVZ += (double)(windDirection.z * forceStrength)));
            } else {
                strength = 0.2;
                baseVX = (double)Math.random() * strength - strength * 0.5;
                baseVY = -0.05;
                baseVZ = (double)Math.random() * strength - strength * 0.5;
                particleEngine.add(snowProvider.createParticle((ParticleOptions)WeatherEffects.PHYSICS_SNOW, level, (double)((float)x + Math.random()), (double)((float)y + Math.random()), (double)((float)z + Math.random()), baseVX += (double)(windDirection.x * forceStrength), baseVY, baseVZ += (double)(windDirection.z * forceStrength)));
            }
            ++WeatherEffects.aliveParticles;
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"renderSnowAndRain"}, cancellable=true)
    private void renderSnowAndRain(LightTexture lightTexture, float f, double d, double e, double g, CallbackInfo info) {
        if (ConfigClient.weatherParticles) {
            info.cancel();
        }
    }
}

