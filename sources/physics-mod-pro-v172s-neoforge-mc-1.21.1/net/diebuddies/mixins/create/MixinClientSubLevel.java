package net.diebuddies.mixins.create;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ShipRotation;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
@Mixin({ClientSubLevel.class})
public class MixinClientSubLevel implements EntityOcean, ShipRotation {
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

   @Inject(
      at = {@At("HEAD")},
      method = {"tick"},
      remap = false
   )
   private void physicsmod$updateOceanTransform(CallbackInfo info) {
      ClientLevel level = Minecraft.getInstance().level;
      ClientSubLevel subLevel = (ClientSubLevel)this;
      if (ConfigClient.areOceanPhysicsEnabled() && level != null) {
         OceanWorld oceanWorld = PhysicsMod.getInstance(level).getPhysicsWorld().getOceanWorld();
         Vector3dc position = subLevel.logicalPose().position();
         this.physicsOldOffset = this.physicsOffset;
         this.physicsOldRoll = this.physicsRoll;
         this.physicsOldPitch = this.physicsPitch;
         double targetOffset = oceanWorld.calculateYOffset(position.x(), position.y(), position.z());
         boolean inAir = false;
         double threshold = 0.5;
         double absDistance = Math.abs(targetOffset - this.physicsOffset);
         double dynamicDamping = 1.0;
         if (absDistance < threshold) {
            dynamicDamping = net.diebuddies.math.Math.remap(absDistance / threshold, 0.0, 1.0, 0.0, 1.0);
         }

         double gravityThreshold = 0.0;
         if (targetOffset - gravityThreshold >= this.physicsOffset) {
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

         this.physicsOffset = this.physicsOffset + this.velocityY;
         if (ConfigClient.oceanStickyEntities) {
            this.physicsOffset = targetOffset;
         }

         double wx = position.x();
         double wy = position.y();
         double wz = position.z();
         Quaterniondc rotation = subLevel.logicalPose().orientation();
         Vector3d forward = rotation.transform(new Vector3d(0.0, 0.0, 1.0));
         double leftX = forward.z;
         double leftZ = -forward.x;
         double leftHeight = oceanWorld.calculateYOffset(wx + leftX, wy, wz + leftZ);
         double rightHeight = oceanWorld.calculateYOffset(wx - leftX, wy, wz - leftZ);
         double forwardHeight = oceanWorld.calculateYOffset(wx + forward.x, wy, wz + forward.z);
         double backwardsHeight = oceanWorld.calculateYOffset(wx - forward.x, wy, wz - forward.z);
         double targetRoll = Math.atan2(rightHeight - leftHeight, 2.0);
         double targetPitch = Math.atan2(backwardsHeight - forwardHeight, 2.0);
         double dampingAdjusterRoll = 1.0 - Math.pow(Math.min(1.0, Math.abs(this.physicsRoll) / Math.toRadians(130.0)), 3.0);
         double dampingAdjusterPitch = 1.0 - Math.pow(Math.min(1.0, Math.abs(this.physicsPitch) / Math.toRadians(130.0)), 3.0);
         if (inAir) {
            this.velocityRoll *= 0.95 * dampingAdjusterRoll;
            this.velocityPitch *= 0.95 * dampingAdjusterPitch;
         } else {
            this.velocityRoll = this.velocityRoll + (targetRoll - this.physicsOldRoll) * 0.2;
            this.velocityPitch = this.velocityPitch + (targetPitch - this.physicsOldPitch) * 0.2;
            this.velocityRoll *= 0.6 * dampingAdjusterRoll;
            this.velocityPitch *= 0.6 * dampingAdjusterPitch;
         }

         this.physicsRoll = this.physicsRoll + this.velocityRoll;
         this.physicsPitch = this.physicsPitch + this.velocityPitch;
         if (this.wasInAir != inAir && this.velocityY < -0.11 && !ConfigClient.oceanStickyEntities) {
            int amount = (int)net.diebuddies.math.Math.remapClamp(this.velocityY, -0.11, -0.25, 75.0, 150.0);
            double intensity = net.diebuddies.math.Math.remapClamp(this.velocityY, -0.11, -0.25, 0.5, 1.0);
            float volume = (float)intensity * 0.8F * ConfigClient.oceanSplashVolume;
            float pitch = net.diebuddies.math.Math.random() * 0.3F + 0.8F;
            level.playLocalSound(wx, wy + this.physicsOffset, wz, WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
         }

         if (ConfigClient.oceanStickyEntities) {
            this.physicsRoll = targetRoll;
            this.physicsPitch = targetPitch;
         }

         if (ConfigClient.oceanStickyEntities) {
            this.wasInAir = false;
         } else {
            this.wasInAir = inAir;
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderPose(F)Ldev/ryanhcode/sable/companion/math/Pose3dc;"},
      cancellable = true,
      remap = false
   )
   private void physicsmod$renderPose(float partialTicks, CallbackInfoReturnable<Pose3dc> info) {
      if (this.lastRenderPosePartialTick != partialTicks) {
         ClientSubLevel subLevel = (ClientSubLevel)this;
         Pose3d renderPose = this.renderPose.set(subLevel.lastPose());
         Pose3d target = subLevel.logicalPose();
         double pitch = this.getPhysicsPitch(partialTicks);
         double roll = this.getPhysicsRoll(partialTicks);
         double offset = this.getPhysicsYOffset(partialTicks);
         renderPose.position().lerp(target.position(), partialTicks).add(0.0, offset, 0.0);
         Quaterniond waveRotation = new Quaterniond().rotateAxis(-roll, 0.0, 0.0, 1.0).rotateAxis(pitch, 1.0, 0.0, 0.0);
         renderPose.orientation().slerp(target.orientation(), partialTicks).mul(waveRotation).normalize();
         this.originalShipRotation = subLevel.lastPose().orientation().slerp(target.orientation(), partialTicks, new Quaterniond());
         renderPose.rotationPoint().lerp(target.rotationPoint(), partialTicks);
         renderPose.scale().lerp(target.scale(), partialTicks);
         info.setReturnValue(renderPose);
      }
   }

   @Override
   public double getPhysicsYOffset(float renderPercent) {
      return PhysicsMod.hudRendering ? 0.0 : Mth.lerp(renderPercent, this.getPhysicsOldYOffset(), this.getPhysicsYOffset());
   }

   @Override
   public double getPhysicsPitch(float renderPercent) {
      return PhysicsMod.hudRendering ? 0.0 : Mth.lerp(renderPercent, this.getPhysicsOldPitch(), this.getPhysicsPitch());
   }

   @Override
   public double getPhysicsRoll(float renderPercent) {
      return PhysicsMod.hudRendering ? 0.0 : Mth.lerp(renderPercent, this.getPhysicsOldRoll(), this.getPhysicsRoll());
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
