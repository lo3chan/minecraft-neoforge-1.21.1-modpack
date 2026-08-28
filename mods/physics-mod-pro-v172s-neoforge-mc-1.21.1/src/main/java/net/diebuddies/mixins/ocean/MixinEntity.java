/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.ocean;

import net.diebuddies.compat.SableCreate;
import net.diebuddies.compat.ValkyrienSkies;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public class MixinEntity
implements EntityOcean {
    @Unique
    private double physicsOffset;
    @Unique
    private double physicsOldOffset;
    @Unique
    private double physicsRoll;
    @Unique
    private double physicsOldRoll;
    @Unique
    private double physicsPitch;
    @Unique
    private double physicsOldPitch;
    @Unique
    private double velocityY;
    @Unique
    private double velocityRoll;
    @Unique
    private double velocityPitch;
    @Unique
    private boolean wasInAir;
    @Unique
    private double rippleTime;
    @Unique
    private boolean wasEyeUnderwater;

    @Inject(at={@At(value="HEAD")}, method={"doWaterSplashEffect"})
    private void physicsmod$doOceanRippleEffect(CallbackInfo info) {
        Entity entity = (Entity)this;
        if (ConfigClient.areOceanPhysicsEnabled() && entity.level() instanceof ClientLevel) {
            if (ConfigClient.oceanRipples) {
                this.spawnSplashRipple(entity);
            }
            this.spawnSplashParticles(entity);
        }
    }

    @Unique
    private void spawnSplashRipple(Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        double vy = java.lang.Math.abs(deltaMovement.y);
        if (vy < 0.1) {
            return;
        }
        double speed = Math.remapClamp(vy, 0.1, 2.0, 0.0425, 0.0625);
        int amount = (int)Math.remapClamp(vy, 0.1, 2.0, 120.0, 240.0);
        int lifetime = (int)Math.remapClamp(vy, 0.1, 2.0, 70.0, 80.0);
        float scale = (float)Math.remapClamp(vy, 0.1, 2.0, 0.2, 0.65);
        OceanWorld oceanWorld = PhysicsMod.getInstance(entity.level()).getPhysicsWorld().getOceanWorld();
        oceanWorld.spawnRipple(amount, lifetime, scale, entity.getX(), entity.getY() + this.getPhysicsYOffset(), entity.getZ(), speed);
    }

    @Unique
    private void spawnSplashParticles(Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        double vy = java.lang.Math.abs(deltaMovement.y);
        if (vy < 0.25) {
            return;
        }
        int splashamount = (int)Math.remapClamp(vy, 0.1, 2.0, 10.0, 75.0);
        double intensity = Math.remapClamp(vy, 0.1, 2.0, 0.075, 0.5);
        float volume = (float)intensity * ConfigClient.oceanSplashVolume;
        float pitch = Math.random() * 0.4f + 0.7f;
        Level level = entity.level();
        level.playLocalSound(entity.getX(), entity.getY() + this.getPhysicsYOffset(), entity.getZ(), WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
        if (ConfigClient.oceanParticles) {
            OceanWorld.createWaterSplash(level, entity.getX(), entity.getY() + this.getPhysicsYOffset(), entity.getZ(), 0.0, 0.0, 0.0, 0.25, intensity, splashamount);
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"tick"})
    private void physicsmod$spawnMovementRippleEffect(CallbackInfo info) {
        Entity entity = (Entity)this;
        if (ConfigClient.areOceanPhysicsEnabled() && entity.level() instanceof ClientLevel) {
            Vec3 movement;
            double speedSquared;
            if (ConfigClient.oceanRipples && this.isCausingRipples(entity) && (speedSquared = (movement = entity.getDeltaMovement()).horizontalDistanceSqr()) > 0.001) {
                double rippleSpeed = Math.remapClamp(java.lang.Math.sqrt(speedSquared), 0.077, 0.4, 0.0375, 0.0625);
                int lifetime = (int)Math.remapClamp(java.lang.Math.sqrt(speedSquared), 0.077, 0.4, 100.0, 60.0);
                OceanWorld oceanWorld = PhysicsMod.getInstance(entity.level()).getPhysicsWorld().getOceanWorld();
                oceanWorld.spawnAngularRipple(360, lifetime, entity.getX(), entity.getY(), entity.getZ(), movement.x, movement.z, java.lang.Math.toRadians(100.0), rippleSpeed, 0.0);
                double perc = 0.5;
                oceanWorld.spawnAngularRipple(360, lifetime, Mth.lerp((double)perc, (double)entity.xo, (double)entity.getX()), Mth.lerp((double)perc, (double)entity.yo, (double)entity.getY()), Mth.lerp((double)perc, (double)entity.zo, (double)entity.getZ()), movement.x, movement.z, java.lang.Math.toRadians(100.0), rippleSpeed, 0.0);
            }
            if (this.isCausingSplash(entity)) {
                if (ConfigClient.oceanRipples) {
                    this.spawnSplashRipple(entity);
                }
                this.spawnSplashParticles(entity);
            }
        }
    }

    @Unique
    private boolean isCausingSplash(Entity entity) {
        boolean isEyeUnderwater = entity.isEyeInFluid(FluidTags.WATER);
        boolean result = !isEyeUnderwater && this.wasEyeUnderwater;
        this.wasEyeUnderwater = isEyeUnderwater;
        return result;
    }

    @Unique
    private boolean isCausingRipples(Entity entity) {
        boolean isOnSurface = true;
        if (!ConfigClient.oceanStickyEntities && entity instanceof Boat) {
            isOnSurface = !((EntityOcean)entity).isInPhysicsAir();
        }
        return isOnSurface && !entity.isSpectator() && entity.isInWater() && (entity.level().getBlockState(entity.blockPosition().above()).isAir() || entity instanceof AbstractClientPlayer && entity.level().getBlockState(entity.blockPosition().above().above()).isAir());
    }

    @Inject(at={@At(value="TAIL")}, method={"tick"})
    private void physicsmod$updateOceanTransformations(CallbackInfo info) {
        Entity entity = (Entity)this;
        if (ConfigClient.areOceanPhysicsEnabled() && entity.level() instanceof ClientLevel) {
            double gravityThreshold;
            OceanWorld oceanWorld = PhysicsMod.getInstance(entity.level()).getPhysicsWorld().getOceanWorld();
            this.physicsOldOffset = this.physicsOffset;
            this.physicsOldRoll = this.physicsRoll;
            this.physicsOldPitch = this.physicsPitch;
            double targetOffset = oceanWorld.computeYOffset(entity.level(), entity);
            boolean inAir = false;
            double threshold = 0.5;
            double absDistance = java.lang.Math.abs(targetOffset - this.physicsOffset);
            double dynamicDamping = 1.0;
            if (absDistance < threshold) {
                dynamicDamping = Math.remap(absDistance / threshold, 0.0, 1.0, 0.0, 1.0);
            }
            if (targetOffset - (gravityThreshold = 0.0) >= this.physicsOffset) {
                this.velocityY += 0.24100000000000002 * dynamicDamping;
                this.velocityY *= 0.6;
            } else if (targetOffset + gravityThreshold < this.physicsOffset) {
                this.velocityY += -0.040999999999999995 * dynamicDamping;
                this.velocityY *= 0.95;
            } else {
                this.velocityY *= 0.8;
            }
            if (this.physicsOffset - targetOffset > 0.075) {
                inAir = true;
            }
            this.physicsOffset += this.velocityY;
            if (ConfigClient.oceanStickyEntities) {
                this.physicsOffset = targetOffset;
            }
            if (entity instanceof Boat) {
                double wx = entity.getX();
                double wy = entity.getY();
                double wz = entity.getZ();
                Vec3 forward = entity.getViewVector(1.0f);
                double leftX = forward.z;
                double leftZ = -forward.x;
                double leftHeight = oceanWorld.calculateYOffset(wx + leftX, wy, wz + leftZ);
                double rightHeight = oceanWorld.calculateYOffset(wx - leftX, wy, wz - leftZ);
                double forwardHeight = oceanWorld.calculateYOffset(wx + forward.x, wy, wz + forward.z);
                double backwardsHeight = oceanWorld.calculateYOffset(wx - forward.x, wy, wz - forward.z);
                double targetRoll = java.lang.Math.atan2(rightHeight - leftHeight, 2.0);
                double targetPitch = java.lang.Math.atan2(backwardsHeight - forwardHeight, 2.0);
                double dampingAdjusterRoll = 1.0 - java.lang.Math.pow(java.lang.Math.min(1.0, java.lang.Math.abs(this.physicsRoll) / java.lang.Math.toRadians(130.0)), 3.0);
                double dampingAdjusterPitch = 1.0 - java.lang.Math.pow(java.lang.Math.min(1.0, java.lang.Math.abs(this.physicsPitch) / java.lang.Math.toRadians(130.0)), 3.0);
                if (inAir) {
                    this.velocityRoll *= 0.95 * dampingAdjusterRoll;
                    this.velocityPitch *= 0.95 * dampingAdjusterPitch;
                } else {
                    this.velocityRoll += (targetRoll - this.physicsOldRoll) * 0.2;
                    this.velocityPitch += (targetPitch - this.physicsOldPitch) * 0.2;
                    this.velocityRoll *= 0.6 * dampingAdjusterRoll;
                    this.velocityPitch *= 0.6 * dampingAdjusterPitch;
                }
                this.physicsRoll += this.velocityRoll;
                this.physicsPitch += this.velocityPitch;
                if (this.wasInAir != inAir && this.velocityY < -0.11 && !ConfigClient.oceanStickyEntities) {
                    Vec3 movement = entity.getDeltaMovement();
                    int amount = (int)Math.remapClamp(this.velocityY, -0.11, -0.25, 75.0, 150.0);
                    double intensity = Math.remapClamp(this.velocityY, -0.11, -0.25, 0.5, 1.0);
                    float volume = (float)intensity * 0.8f * ConfigClient.oceanSplashVolume;
                    float pitch = Math.random() * 0.3f + 0.8f;
                    entity.level().playLocalSound(wx, wy + this.physicsOffset, wz, WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
                    if (ConfigClient.oceanParticles) {
                        OceanWorld.createWaterSplash(entity.level(), entity.getX(), entity.getY() + targetOffset, entity.getZ(), movement.x * 0.4, 0.0, movement.z * 0.4, 1.0, intensity, amount);
                    }
                }
                if (ConfigClient.oceanStickyEntities) {
                    this.physicsRoll = targetRoll;
                    this.physicsPitch = targetPitch;
                }
            }
            this.wasInAir = ConfigClient.oceanStickyEntities ? false : inAir;
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"getBoundingBoxForCulling"}, cancellable=true)
    private void physicsmod$modifyBoundingBoxForCulling(CallbackInfoReturnable<AABB> info) {
        AABB aabb = (AABB)info.getReturnValue();
        if (aabb != null) {
            info.setReturnValue((Object)aabb.move(0.0, this.physicsOffset, 0.0));
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"pick"}, cancellable=true)
    private void physicsmod$modifyBlockPickingForOceanTransformations(double range, float renderPercent, boolean bl, CallbackInfoReturnable<HitResult> info) {
        Entity entity = (Entity)this;
        if (ConfigClient.areOceanPhysicsEnabled() && entity.level() instanceof ClientLevel && ConfigClient.oceanAdjustHitbox) {
            OceanWorld oceanWorld = PhysicsMod.getInstance(entity.level()).getPhysicsWorld().getOceanWorld();
            double xOffset = 0.0;
            double zOffset = 0.0;
            double yOffset = oceanWorld.computeYOffset(entity.level(), entity, renderPercent);
            if (StarterClient.valkyrienSkies && entity.getVehicle() == null && ValkyrienSkies.hasShipMount(entity) != null) {
                Vector3d offset = ValkyrienSkies.getEntityOffset3D(entity, renderPercent);
                xOffset = offset.x;
                yOffset = offset.y;
                zOffset = offset.z;
            }
            Vec3 oldEyePos = entity.getEyePosition(renderPercent);
            Vec3 eyePos = new Vec3(oldEyePos.x + xOffset, oldEyePos.y + yOffset, oldEyePos.z + zOffset);
            Vec3 viewDir = entity.getViewVector(renderPercent);
            Vec3 target = eyePos.add(viewDir.x * range, viewDir.y * range, viewDir.z * range);
            info.setReturnValue((Object)entity.level().clip(new ClipContext(eyePos, target, ClipContext.Block.OUTLINE, bl ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity)));
        }
    }

    @Override
    public double getPhysicsYOffset(float renderPercent) {
        if (PhysicsMod.hudRendering) {
            return 0.0;
        }
        return Mth.lerp((double)renderPercent, (double)this.getPhysicsOldYOffset(), (double)this.getPhysicsYOffset());
    }

    @Override
    public double getPhysicsPitch(float renderPercent) {
        if (PhysicsMod.hudRendering) {
            return 0.0;
        }
        return Mth.lerp((double)renderPercent, (double)this.getPhysicsOldPitch(), (double)this.getPhysicsPitch());
    }

    @Override
    public double getPhysicsRoll(float renderPercent) {
        if (PhysicsMod.hudRendering) {
            return 0.0;
        }
        return Mth.lerp((double)renderPercent, (double)this.getPhysicsOldRoll(), (double)this.getPhysicsRoll());
    }

    @Override
    public double getPhysicsYOffset() {
        EntityOcean vehicle = this.getVehicle();
        if (vehicle != null) {
            return vehicle.getPhysicsYOffset();
        }
        return this.physicsOffset;
    }

    @Override
    public double getPhysicsOldYOffset() {
        EntityOcean vehicle = this.getVehicle();
        if (vehicle != null) {
            return vehicle.getPhysicsOldYOffset();
        }
        return this.physicsOldOffset;
    }

    @Override
    public double getPhysicsPitch() {
        EntityOcean vehicle = this.getVehicle();
        if (vehicle != null) {
            return vehicle.getPhysicsPitch();
        }
        return this.physicsPitch;
    }

    @Override
    public double getPhysicsOldPitch() {
        EntityOcean vehicle = this.getVehicle();
        if (vehicle != null) {
            return vehicle.getPhysicsOldPitch();
        }
        return this.physicsOldPitch;
    }

    @Override
    public double getPhysicsRoll() {
        EntityOcean vehicle = this.getVehicle();
        if (vehicle != null) {
            return vehicle.getPhysicsRoll();
        }
        return this.physicsRoll;
    }

    @Override
    public double getPhysicsOldRoll() {
        EntityOcean vehicle = this.getVehicle();
        if (vehicle != null) {
            return vehicle.getPhysicsOldRoll();
        }
        return this.physicsOldRoll;
    }

    @Unique
    private EntityOcean getVehicle() {
        Entity entity = (Entity)this;
        EntityOcean vehicle = (EntityOcean)entity.getVehicle();
        if (StarterClient.valkyrienSkies && vehicle == null) {
            vehicle = ValkyrienSkies.hasShipMount(entity);
        } else if (StarterClient.sable && vehicle == null) {
            vehicle = SableCreate.hasShipMount(entity);
        }
        return vehicle;
    }

    @Override
    public boolean isInPhysicsAir() {
        return this.wasInAir;
    }
}

