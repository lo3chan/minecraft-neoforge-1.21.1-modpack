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
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
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

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Unique
   private final Long2ObjectMap<Holder<Biome>> cachedBiomes = new Long2ObjectOpenHashMap(169);
   @Unique
   private WindSoundInstance windSound;

   @Inject(
      at = {@At("TAIL")},
      method = {"renderLevel"}
   )
   public void resetWeatherLightInvalidation(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f viewMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo info
   ) {
      WeatherEffects.invalidateLight = false;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setLevel"}
   )
   public void setLevel(@Nullable ClientLevel clientLevel, CallbackInfo info) {
      if (this.windSound != null) {
         this.windSound.stopWind();
      }

      if (clientLevel != null) {
         this.minecraft.getSoundManager().queueTickingSound(this.windSound = new WindSoundInstance(WeatherEffects.WIND_SOUND_EVENT, SoundSource.WEATHER));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tickRain"},
      cancellable = true
   )
   private void tickRain(Camera camera, CallbackInfo info) {
      if (ConfigClient.weatherParticles) {
         float precipitationAmount = this.minecraft.level.getRainLevel(1.0F);
         if (!(precipitationAmount <= 0.0F)) {
            int checkRange = 6;
            int camX = Mth.floor(camera.getPosition().x);
            int camY = Mth.floor(camera.getPosition().y);
            int camZ = Mth.floor(camera.getPosition().z);
            int spawnableRain = 0;
            int particlesPerBlock = 0;
            particlesPerBlock += (int)(precipitationAmount * ConfigClient.weatherRainParticleAmount);
            particlesPerBlock += (int)(this.minecraft.level.getThunderLevel(1.0F) * ConfigClient.weatherThunderParticleAmount);
            ClientLevel level = this.minecraft.level;
            MutableBlockPos mutableBlockPos = new MutableBlockPos();
            ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
            this.cachedBiomes.clear();

            for (int zo = camZ - checkRange; zo <= camZ + checkRange; zo++) {
               for (int xo = camX - checkRange; xo <= camX + checkRange; xo++) {
                  int diffX = xo - camX;
                  int diffZ = zo - camZ;
                  if (diffX * diffX + diffZ * diffZ <= 36) {
                     mutableBlockPos.set(xo, camY, zo);
                     Holder<Biome> biomeHolder = level.getBiome(mutableBlockPos);
                     Biome biome = (Biome)biomeHolder.value();
                     this.cachedBiomes.put(mutableBlockPos.asLong(), biomeHolder);
                     if (biome.getPrecipitationAt(mutableBlockPos) != Precipitation.NONE || biomeHolder.is(Biomes.DESERT)) {
                        int rainToThisHeight = level.getHeight(Types.MOTION_BLOCKING, xo, zo);
                        int rainBottom = camY - checkRange;
                        int rainTop = camY + checkRange;
                        if (rainBottom < rainToThisHeight) {
                           rainBottom = rainToThisHeight;
                        }

                        if (rainTop < rainToThisHeight) {
                           rainTop = rainToThisHeight;
                        }

                        if (rainBottom != rainTop) {
                           int height = rainTop - rainBottom;
                           spawnableRain += height * particlesPerBlock;
                        }
                     }
                  }
               }
            }

            int range = 14;
            PhysicsWorld world = PhysicsMod.getInstance(level).getPhysicsWorld();
            WeatherDomain weatherDomain = world.getWeatherDomain();
            Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)this.minecraft.particleEngine).getParticleProviders();
            ParticleProvider<ParticleOptions> rainProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.RAIN_RESOURCE);
            ParticleProvider<ParticleOptions> snowProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.SNOW_RESOURCE);
            ParticleProvider<ParticleOptions> dustProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.DUST_RESOURCE);

            while (WeatherEffects.aliveParticles < spawnableRain) {
               double xox = Math.random() * 2.0 - 1.0;
               double yo = Math.random() * 2.0 - 1.0;
               double zo = Math.random() * 2.0 - 1.0;
               double distance = xox * xox + yo * yo + zo * zo;
               if (!(distance > 1.0)) {
                  int x = Mth.floor(camera.getPosition().x + xox * range);
                  int y = Mth.floor(camera.getPosition().y + yo * range);
                  int z = Mth.floor(camera.getPosition().z + zo * range);
                  mutableBlockPos.set(x, camY, z);
                  long blockPosUnique = mutableBlockPos.asLong();
                  Holder<Biome> biomeHolder = (Holder<Biome>)this.cachedBiomes.computeIfAbsent(blockPosUnique, key -> level.getBiome(mutableBlockPos));
                  Biome biome = (Biome)biomeHolder.value();
                  if (biome.getPrecipitationAt(mutableBlockPos) != Precipitation.NONE || biomeHolder.is(Biomes.DESERT)) {
                     int rainToThisHeightx = level.getHeight(Types.MOTION_BLOCKING, x, z);
                     if (y >= rainToThisHeightx) {
                        int rainBottomx = y - range;
                        int rainTopx = y + range;
                        if (rainBottomx < rainToThisHeightx) {
                           rainBottomx = rainToThisHeightx;
                        }

                        if (rainTopx < rainToThisHeightx) {
                           rainTopx = rainToThisHeightx;
                        }

                        if (rainBottomx != rainTopx) {
                           mutableBlockPos.set(x, rainBottomx, z);
                           level.getBlockState(mutableBlockPos).isAir();
                           Vector3f windDirection = weatherDomain.getWindDirection(x, y, z);
                           float forceStrength = weatherDomain.getWindStrengthFast();
                           if (biomeHolder.is(Biomes.DESERT)) {
                              double strength = 0.3;
                              double baseVX = Math.random() * strength - strength * 0.5;
                              double baseVY = -0.05;
                              double baseVZ = Math.random() * strength - strength * 0.5;
                              baseVX += windDirection.x * forceStrength * 2.0;
                              baseVZ += windDirection.z * forceStrength * 2.0;
                              particleEngine.add(
                                 dustProvider.createParticle(
                                    WeatherEffects.PHYSICS_DUST, level, x + Math.random(), y + Math.random(), z + Math.random(), baseVX, baseVY, baseVZ
                                 )
                              );
                           } else if (biome.warmEnoughToRain(mutableBlockPos)) {
                              double strength = 0.13;
                              double baseVX = Math.random() * strength - strength * 0.5;
                              double baseVY = -0.6;
                              double baseVZ = Math.random() * strength - strength * 0.5;
                              baseVX += windDirection.x * forceStrength;
                              baseVZ += windDirection.z * forceStrength;
                              particleEngine.add(
                                 rainProvider.createParticle(
                                    WeatherEffects.PHYSICS_RAIN, level, x + Math.random(), y + Math.random(), z + Math.random(), baseVX, baseVY, baseVZ
                                 )
                              );
                           } else {
                              double strength = 0.2;
                              double baseVX = Math.random() * strength - strength * 0.5;
                              double baseVY = -0.05;
                              double baseVZ = Math.random() * strength - strength * 0.5;
                              baseVX += windDirection.x * forceStrength;
                              baseVZ += windDirection.z * forceStrength;
                              particleEngine.add(
                                 snowProvider.createParticle(
                                    WeatherEffects.PHYSICS_SNOW, level, x + Math.random(), y + Math.random(), z + Math.random(), baseVX, baseVY, baseVZ
                                 )
                              );
                           }

                           WeatherEffects.aliveParticles++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderSnowAndRain"},
      cancellable = true
   )
   private void renderSnowAndRain(LightTexture lightTexture, float f, double d, double e, double g, CallbackInfo info) {
      if (ConfigClient.weatherParticles) {
         info.cancel();
      }
   }
}
