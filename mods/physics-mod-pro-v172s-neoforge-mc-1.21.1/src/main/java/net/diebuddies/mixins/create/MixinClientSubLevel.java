/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.create;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ShipRotation;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value={ClientSubLevel.class})
public class MixinClientSubLevel
implements EntityOcean,
ShipRotation {
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
    private Quaterniondc originalShipRotation;
    @Shadow
    @Final
    private Pose3d renderPose;
    @Shadow
    private float lastRenderPosePartialTick;

    @Inject(at={@At(value="HEAD")}, method={"tick"}, remap=false)
    private void physicsmod$updateOceanTransform(CallbackInfo info) {
        ClientLevel level = Minecraft.getInstance().level;
        ClientSubLevel subLevel = (ClientSubLevel)this;
        if (ConfigClient.areOceanPhysicsEnabled() && level != null) {
            double gravityThreshold;
            OceanWorld oceanWorld = PhysicsMod.getInstance((Level)level).getPhysicsWorld().getOceanWorld();
            Vector3d position = subLevel.logicalPose().position();
            this.physicsOldOffset = this.physicsOffset;
            this.physicsOldRoll = this.physicsRoll;
            this.physicsOldPitch = this.physicsPitch;
            double targetOffset = oceanWorld.calculateYOffset(position.x(), position.y(), position.z());
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
            double wx = position.x();
            double wy = position.y();
            double wz = position.z();
            Quaterniond rotation = subLevel.logicalPose().orientation();
            Vector3d forward = rotation.transform(new Vector3d(0.0, 0.0, 1.0));
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
                int amount = (int)Math.remapClamp(this.velocityY, -0.11, -0.25, 75.0, 150.0);
                double intensity = Math.remapClamp(this.velocityY, -0.11, -0.25, 0.5, 1.0);
                float volume = (float)intensity * 0.8f * ConfigClient.oceanSplashVolume;
                float pitch = Math.random() * 0.3f + 0.8f;
                level.playLocalSound(wx, wy + this.physicsOffset, wz, WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
            }
            if (ConfigClient.oceanStickyEntities) {
                this.physicsRoll = targetRoll;
                this.physicsPitch = targetPitch;
            }
            this.wasInAir = ConfigClient.oceanStickyEntities ? false : inAir;
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"renderPose(F)Ldev/ryanhcode/sable/companion/math/Pose3dc;"}, cancellable=true, remap=false)
    private void physicsmod$renderPose(float partialTicks, CallbackInfoReturnable<Pose3dc> info) {
        if (this.lastRenderPosePartialTick == partialTicks) {
            return;
        }
        ClientSubLevel subLevel = (ClientSubLevel)this;
        Pose3d renderPose = this.renderPose.set(subLevel.lastPose());
        Pose3d target = subLevel.logicalPose();
        double pitch = this.getPhysicsPitch(partialTicks);
        double roll = this.getPhysicsRoll(partialTicks);
        double offset = this.getPhysicsYOffset(partialTicks);
        renderPose.position().lerp((Vector3dc)target.position(), (double)partialTicks).add(0.0, offset, 0.0);
        Quaterniond waveRotation = new Quaterniond().rotateAxis(-roll, 0.0, 0.0, 1.0).rotateAxis(pitch, 1.0, 0.0, 0.0);
        renderPose.orientation().slerp((Quaterniondc)target.orientation(), (double)partialTicks).mul((Quaterniondc)waveRotation).normalize();
        this.originalShipRotation = subLevel.lastPose().orientation().slerp((Quaterniondc)target.orientation(), (double)partialTicks, new Quaterniond());
        renderPose.rotationPoint().lerp((Vector3dc)target.rotationPoint(), (double)partialTicks);
        renderPose.scale().lerp((Vector3dc)target.scale(), (double)partialTicks);
        info.setReturnValue((Object)renderPose);
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
        return this.physicsOffset;
    }

    @Override
    public double getPhysicsOldYOffset() {
        return this.physicsOldOffset;
    }

    @Override
    public double getPhysicsPitch() {
        return this.physicsPitch;
    }

    @Override
    public double getPhysicsOldPitch() {
        return this.physicsOldPitch;
    }

    @Override
    public double getPhysicsRoll() {
        return this.physicsRoll;
    }

    @Override
    public double getPhysicsOldRoll() {
        return this.physicsOldRoll;
    }

    @Override
    public boolean isInPhysicsAir() {
        return this.wasInAir;
    }

    @Override
    public Quaterniondc getOriginalRotation() {
        if (this.originalShipRotation == null) {
            this.originalShipRotation = new Quaterniond();
        }
        return this.originalShipRotation;
    }
}

