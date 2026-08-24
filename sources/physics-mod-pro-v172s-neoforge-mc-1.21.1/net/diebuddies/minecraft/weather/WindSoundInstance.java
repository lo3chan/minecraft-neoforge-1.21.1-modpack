package net.diebuddies.minecraft.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.wind.WeatherDomain;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class WindSoundInstance extends AbstractTickableSoundInstance {
   private float volumeTarget;
   private float pitchTarget;
   private float tmpVolume;

   public WindSoundInstance(SoundEvent soundEvent, SoundSource soundSource) {
      super(soundEvent, soundSource, SoundInstance.createUnseededRandom());
      this.looping = true;
      this.delay = 0;
      this.relative = true;
      this.volume = 0.0F;
      this.volumeTarget = 0.0F;
      this.pitch = 1.0F;
      this.pitchTarget = 1.0F;
   }

   public void tick() {
      if (RenderSystem.isOnRenderThread()) {
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         BlockPos camPos = camera.getBlockPosition();
         int camX = Mth.floor(camera.getPosition().x);
         int camY = Mth.floor(camera.getPosition().y);
         int camZ = Mth.floor(camera.getPosition().z);
         ClientLevel level = Minecraft.getInstance().level;
         if (level == null) {
            this.stopWind();
         } else {
            double distance = level.getHeight(Types.MOTION_BLOCKING, camPos.getX(), camPos.getZ()) - camPos.getY();
            if (distance > 0.0) {
               distance *= distance;
               int checkRange = 6;

               for (int zo = camZ - checkRange; zo <= camZ + checkRange; zo++) {
                  for (int xo = camX - checkRange; xo <= camX + checkRange; xo++) {
                     int diffX = xo - camX;
                     int diffZ = zo - camZ;
                     if (diffX * diffX + diffZ * diffZ <= 36) {
                        int rainToThisHeight = level.getHeight(Types.MOTION_BLOCKING, xo, zo);
                        double dsquared = 0.0;
                        if (rainToThisHeight <= camPos.getY()) {
                           dsquared = Vector2d.distanceSquared(camPos.getX(), camPos.getZ(), xo, zo);
                        } else {
                           dsquared = Vector3d.distanceSquared(camPos.getX(), camPos.getY(), camPos.getZ(), xo, rainToThisHeight, zo);
                        }

                        if (dsquared < distance) {
                           distance = dsquared;
                        }
                     }
                  }
               }

               distance = Math.sqrt(distance);
            } else {
               distance = 0.0;
            }

            WeatherDomain weatherDomain = PhysicsMod.getInstance(level).getPhysicsWorld().getWeatherDomain();
            Vector3f windDirection = weatherDomain.getWindDirection(camX, camY, camZ);
            float distanceBasedVolume = 1.0F - net.diebuddies.math.Math.clamp((float)distance / 6.0F, 0.0F, 1.0F);
            float strengthBasedVolume = net.diebuddies.math.Math.clamp(weatherDomain.getWindStrengthFast() / 0.9F, 0.0F, 1.0F);
            if (strengthBasedVolume > 0.3F) {
               this.pitchTarget = 1.0F + Math.max(0.0F, windDirection.length() - 0.4F);
            } else {
               this.pitchTarget = 1.0F;
            }

            if (strengthBasedVolume < 0.12F) {
               strengthBasedVolume = net.diebuddies.math.Math.remapClamp(Math.max(0.0F, windDirection.length() - 0.3F), 0.0F, 0.3F, 0.0F, 0.08F);
            }

            this.volumeTarget = distanceBasedVolume * strengthBasedVolume;
            this.tmpVolume = org.joml.Math.lerp(this.tmpVolume, this.volumeTarget, 0.05F);
            this.volume = this.tmpVolume * ConfigClient.windVolume;
            this.pitch = org.joml.Math.lerp(this.pitch, this.pitchTarget, 0.075F);
         }
      }
   }

   public boolean canStartSilent() {
      return true;
   }

   public void stopWind() {
      this.stop();
   }
}
