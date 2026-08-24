package net.diebuddies.mixins.ocean;

import java.util.Map;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.mixins.MixinParticleEngineAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Status;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Boat.class})
public class MixinBoat {
   @Shadow
   private Status status;

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   public void tick(CallbackInfo info) {
      Boat boat = (Boat)this;
      if (ConfigClient.areOceanPhysicsEnabled() && boat.level() instanceof ClientLevel level && this.status == Status.IN_WATER && ConfigClient.oceanParticles) {
         double diffX = boat.getX() - boat.xo;
         double diffZ = boat.getZ() - boat.zo;
         double velocity = Math.sqrt(diffX * diffX + diffZ * diffZ);
         double speed = boat.getDeltaMovement().horizontalDistance();
         if (velocity < 0.1 || ((EntityOcean)boat).isInPhysicsAir()) {
            return;
         }

         OceanWorld oceanWorld = PhysicsMod.getInstance(boat.level()).getPhysicsWorld().getOceanWorld();
         Vec3 forward = boat.getForward();
         double yOffset = oceanWorld.calculateYOffset(boat.getX() + forward.x, boat.getY() + forward.y, boat.getZ() + forward.z);
         double dirfirst = 1.0;
         double dirsecond = -1.0;
         double angle = Math.toRadians(boat.getYRot());
         double vx1 = dirfirst * Math.cos(angle);
         double vz1 = dirfirst * Math.sin(angle);
         double vx2 = dirsecond * Math.cos(angle);
         double vz2 = dirsecond * Math.sin(angle);
         double x1 = 0.6;
         double z1 = 0.5;
         angle = Math.toRadians(boat.getYRot());
         double rotatedX1 = x1 * Math.cos(angle) - z1 * Math.sin(angle);
         double rotatedZ1 = x1 * Math.sin(angle) + z1 * Math.cos(angle);
         ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
         Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)particleEngine).getParticleProviders();
         ParticleProvider<ParticleOptions> smallProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.SPLASH_SMALL_RESOURCE);

         for (int i = 0; i < 5; i++) {
            if (velocity * 1.0 > net.diebuddies.math.Math.random()) {
               particleEngine.add(
                  smallProvider.createParticle(
                     WeatherEffects.PHYSICS_SPLASH_SMALL,
                     level,
                     boat.getX() + rotatedX1 + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     boat.getY() + yOffset + 0.35 + net.diebuddies.math.Math.random() * 0.2,
                     boat.getZ() + rotatedZ1 + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     vx1 * 0.04 + (net.diebuddies.math.Math.random() - 0.5) * 0.1,
                     0.1 + (net.diebuddies.math.Math.random() - 0.5) * 0.1,
                     vz1 * 0.04 + (net.diebuddies.math.Math.random() - 0.5) * 0.1
                  )
               );
            }
         }

         double x2 = -0.6;
         double z2 = 0.5;
         angle = Math.toRadians(boat.getYRot());
         double rotatedX2 = x2 * Math.cos(angle) - z2 * Math.sin(angle);
         double rotatedZ2 = x2 * Math.sin(angle) + z2 * Math.cos(angle);

         for (int ix = 0; ix < 5; ix++) {
            if (velocity * 1.0 > net.diebuddies.math.Math.random()) {
               particleEngine.add(
                  smallProvider.createParticle(
                     WeatherEffects.PHYSICS_SPLASH_SMALL,
                     level,
                     boat.getX() + rotatedX2 + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     boat.getY() + yOffset + 0.35 + net.diebuddies.math.Math.random() * 0.2,
                     boat.getZ() + rotatedZ2 + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     vx2 * 0.04 * speed + (net.diebuddies.math.Math.random() - 0.5) * 0.1,
                     0.1 + (net.diebuddies.math.Math.random() - 0.5) * 0.1,
                     vz2 * 0.04 * speed + (net.diebuddies.math.Math.random() - 0.5) * 0.1
                  )
               );
            }
         }

         for (int ixx = 0; ixx < 8; ixx++) {
            if (velocity * 1.0 > net.diebuddies.math.Math.random()) {
               double perc = net.diebuddies.math.Math.random();
               double nx = Mth.lerp(perc, rotatedX1, rotatedX2);
               double nz = Mth.lerp(perc, rotatedZ1, rotatedZ2);
               particleEngine.add(
                  smallProvider.createParticle(
                     WeatherEffects.PHYSICS_SPLASH_SMALL,
                     level,
                     boat.getX() + nx + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     boat.getY() + yOffset + 0.35 + net.diebuddies.math.Math.random() * 0.2,
                     boat.getZ() + nz + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     forward.x * speed * 1.15 + (net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     0.1 + (net.diebuddies.math.Math.random() - 0.5) * 0.1,
                     forward.z * speed * 1.15 + (net.diebuddies.math.Math.random() - 0.5) * 0.3
                  )
               );
            }
         }
      }
   }
}
