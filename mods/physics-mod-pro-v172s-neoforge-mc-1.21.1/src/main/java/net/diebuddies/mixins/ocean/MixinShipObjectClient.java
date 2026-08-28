/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4d
 *  org.joml.Matrix4f
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.valkyrienskies.core.api.ships.properties.ShipTransform
 *  org.valkyrienskies.core.impl.game.ships.ShipObjectClient
 *  org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
 */
package net.diebuddies.mixins.ocean;

import com.mojang.math.Axis;
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
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.ShipObjectClient;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;

@Pseudo
@Mixin(value={ShipObjectClient.class})
public class MixinShipObjectClient
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
    private double rippleTime;
    @Unique
    private boolean wasEyeUnderwater;
    @Unique
    private Quaterniondc originalShipRotation;
    @Shadow(remap=false)
    private ShipTransform renderTransform;

    @Inject(at={@At(value="TAIL")}, method={"tickUpdateShipTransform"}, remap=false)
    public final void physicsmod$calculateWaveTransforms(CallbackInfo info) {
        ShipObjectClient ship = (ShipObjectClient)this;
        ClientLevel level = Minecraft.getInstance().level;
        if (ConfigClient.areOceanPhysicsEnabled() && level != null) {
            double gravityThreshold;
            OceanWorld oceanWorld = PhysicsMod.getInstance((Level)level).getPhysicsWorld().getOceanWorld();
            Vector3dc position = ship.getShipData().getTransform().getPositionInWorld();
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
            Quaterniondc rotation = ship.getShipData().getTransform().getShipToWorldRotation();
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

    @Inject(at={@At(value="HEAD")}, method={"getRenderTransform"}, remap=false)
    public void physicsmod$getRenderTransform(CallbackInfoReturnable<ShipTransform> info) {
    }

    @Inject(at={@At(value="TAIL")}, method={"updateRenderShipTransform"}, remap=false)
    public final void physicsmod$applyOceanTransformation(double partialTicks, CallbackInfo info) {
        double pitch = this.getPhysicsPitch((float)partialTicks);
        double roll = this.getPhysicsRoll((float)partialTicks);
        float actualYRot = (float)this.renderTransform.getShipToWorldRotation().get((Matrix4d)new Matrix4d()).getEulerAnglesXYZ((Vector3d)new Vector3d()).y;
        double forwardZ = java.lang.Math.cos(actualYRot);
        double forwardX = java.lang.Math.sin(actualYRot);
        double leftX = forwardZ;
        double leftZ = -forwardX;
        Matrix4f transformation = new Matrix4f();
        transformation.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)forwardX, 0.0f, (float)forwardZ)).rotationDegrees((float)(-java.lang.Math.toDegrees(roll))));
        transformation.rotate((Quaternionfc)Axis.of((Vector3f)new Vector3f((float)leftX, 0.0f, (float)leftZ)).rotationDegrees((float)java.lang.Math.toDegrees(pitch)));
        this.originalShipRotation = this.renderTransform.getShipToWorldRotation();
        this.renderTransform = ShipTransformImpl.Companion.create((Vector3dc)new Vector3d(this.renderTransform.getPositionInWorld()).add(0.0, this.getPhysicsYOffset((float)partialTicks), 0.0), this.renderTransform.getPositionInShip(), (Quaterniondc)this.renderTransform.getShipToWorldRotation().mul((Quaterniondc)transformation.getUnnormalizedRotation(new Quaterniond()), new Quaterniond()), this.renderTransform.getShipToWorldScaling());
    }

    @Override
    public Quaterniondc getOriginalRotation() {
        if (this.originalShipRotation == null) {
            this.originalShipRotation = new Quaterniond();
        }
        return this.originalShipRotation;
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
}

