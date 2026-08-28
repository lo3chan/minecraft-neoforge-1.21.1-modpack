/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.entity.vehicle.Boat$Status
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.ocean;

import java.util.Map;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Boat.class})
public class MixinBoat {
    @Shadow
    private Boat.Status status;

    @Inject(at={@At(value="TAIL")}, method={"tick"})
    public void tick(CallbackInfo info) {
        Level level;
        Boat boat = (Boat)this;
        if (ConfigClient.areOceanPhysicsEnabled() && (level = boat.level()) instanceof ClientLevel) {
            ClientLevel level2 = (ClientLevel)level;
            if (this.status == Boat.Status.IN_WATER && ConfigClient.oceanParticles) {
                int i;
                double diffX = boat.getX() - boat.xo;
                double diffZ = boat.getZ() - boat.zo;
                double velocity = java.lang.Math.sqrt(diffX * diffX + diffZ * diffZ);
                double speed = boat.getDeltaMovement().horizontalDistance();
                if (velocity < 0.1 || ((EntityOcean)boat).isInPhysicsAir()) {
                    return;
                }
                OceanWorld oceanWorld = PhysicsMod.getInstance(boat.level()).getPhysicsWorld().getOceanWorld();
                Vec3 forward = boat.getForward();
                double yOffset = oceanWorld.calculateYOffset(boat.getX() + forward.x, boat.getY() + forward.y, boat.getZ() + forward.z);
                double dirfirst = 1.0;
                double dirsecond = -1.0;
                double angle = java.lang.Math.toRadians(boat.getYRot());
                double vx1 = dirfirst * java.lang.Math.cos(angle);
                double vz1 = dirfirst * java.lang.Math.sin(angle);
                double vx2 = dirsecond * java.lang.Math.cos(angle);
                double vz2 = dirsecond * java.lang.Math.sin(angle);
                double x1 = 0.6;
                double z1 = 0.5;
                angle = java.lang.Math.toRadians(boat.getYRot());
                double rotatedX1 = x1 * java.lang.Math.cos(angle) - z1 * java.lang.Math.sin(angle);
                double rotatedZ1 = x1 * java.lang.Math.sin(angle) + z1 * java.lang.Math.cos(angle);
                ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
                Map<ResourceLocation, ParticleProvider<?>> provider = ((MixinParticleEngineAccessor)particleEngine).getParticleProviders();
                ParticleProvider<?> smallProvider = provider.get(WeatherParticlesRegistry.SPLASH_SMALL_RESOURCE);
                for (int i2 = 0; i2 < 5; ++i2) {
                    if (!(velocity * 1.0 > (double)Math.random())) continue;
                    particleEngine.add(smallProvider.createParticle((ParticleOptions)WeatherEffects.PHYSICS_SPLASH_SMALL, level2, boat.getX() + rotatedX1 + ((double)Math.random() - 0.5) * 0.3, boat.getY() + yOffset + 0.35 + (double)Math.random() * 0.2, boat.getZ() + rotatedZ1 + ((double)Math.random() - 0.5) * 0.3, vx1 * 0.04 + ((double)Math.random() - 0.5) * 0.1, 0.1 + ((double)Math.random() - 0.5) * 0.1, vz1 * 0.04 + ((double)Math.random() - 0.5) * 0.1));
                }
                double x2 = -0.6;
                double z2 = 0.5;
                angle = java.lang.Math.toRadians(boat.getYRot());
                double rotatedX2 = x2 * java.lang.Math.cos(angle) - z2 * java.lang.Math.sin(angle);
                double rotatedZ2 = x2 * java.lang.Math.sin(angle) + z2 * java.lang.Math.cos(angle);
                for (i = 0; i < 5; ++i) {
                    if (!(velocity * 1.0 > (double)Math.random())) continue;
                    particleEngine.add(smallProvider.createParticle((ParticleOptions)WeatherEffects.PHYSICS_SPLASH_SMALL, level2, boat.getX() + rotatedX2 + ((double)Math.random() - 0.5) * 0.3, boat.getY() + yOffset + 0.35 + (double)Math.random() * 0.2, boat.getZ() + rotatedZ2 + ((double)Math.random() - 0.5) * 0.3, vx2 * 0.04 * speed + ((double)Math.random() - 0.5) * 0.1, 0.1 + ((double)Math.random() - 0.5) * 0.1, vz2 * 0.04 * speed + ((double)Math.random() - 0.5) * 0.1));
                }
                for (i = 0; i < 8; ++i) {
                    if (!(velocity * 1.0 > (double)Math.random())) continue;
                    double perc = Math.random();
                    double nx = Mth.lerp((double)perc, (double)rotatedX1, (double)rotatedX2);
                    double nz = Mth.lerp((double)perc, (double)rotatedZ1, (double)rotatedZ2);
                    particleEngine.add(smallProvider.createParticle((ParticleOptions)WeatherEffects.PHYSICS_SPLASH_SMALL, level2, boat.getX() + nx + ((double)Math.random() - 0.5) * 0.3, boat.getY() + yOffset + 0.35 + (double)Math.random() * 0.2, boat.getZ() + nz + ((double)Math.random() - 0.5) * 0.3, forward.x * speed * 1.15 + ((double)Math.random() - 0.5) * 0.3, 0.1 + ((double)Math.random() - 0.5) * 0.1, forward.z * speed * 1.15 + ((double)Math.random() - 0.5) * 0.3));
                }
            }
        }
    }
}

