package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMimicOctopus;
import com.github.alexthe666.alexsmobs.entity.ISemiAquatic;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;

public class AnimalSwimMoveControllerSink extends MoveControl {
   private final PathfinderMob entity;
   private final float speedMulti;
   private float ySpeedMod = 1.0F;
   private float yawLimit = 10.0F;

   public AnimalSwimMoveControllerSink(PathfinderMob entity, float speedMulti, float ySpeedMod) {
      super(entity);
      this.entity = entity;
      this.speedMulti = speedMulti;
      this.ySpeedMod = ySpeedMod;
   }

   public AnimalSwimMoveControllerSink(PathfinderMob entity, float speedMulti, float ySpeedMod, float yawLimit) {
      super(entity);
      this.entity = entity;
      this.speedMulti = speedMulti;
      this.ySpeedMod = ySpeedMod;
      this.yawLimit = yawLimit;
   }

   public void tick() {
      if (this.entity instanceof ISemiAquatic && ((ISemiAquatic)this.entity).shouldStopMoving()) {
         this.entity.setSpeed(0.0F);
      } else {
         if (this.operation == Operation.MOVE_TO && !this.entity.getNavigation().isDone()) {
            double lvt_1_1_ = this.wantedX - this.entity.getX();
            double lvt_3_1_ = this.wantedY - this.entity.getY();
            double lvt_5_1_ = this.wantedZ - this.entity.getZ();
            double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
            if (lvt_7_1_ < 2.500000277905201E-7) {
               this.mob.setZza(0.0F);
            } else {
               float lvt_9_1_ = (float)(Mth.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875) - 90.0F;
               this.entity.setYRot(this.rotlerp(this.entity.getYRot(), lvt_9_1_, this.yawLimit));
               this.entity.yBodyRot = this.entity.getYRot();
               this.entity.yHeadRot = this.entity.getYRot();
               float lvt_10_1_ = (float)(this.speedModifier * this.speedMulti * 3.0 * this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED));
               if (this.entity.isInWater()) {
                  if (lvt_3_1_ > 0.0 && this.entity.horizontalCollision) {
                     this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0, 0.07999999821186066, 0.0));
                  } else {
                     this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0, this.entity.getSpeed() * lvt_3_1_ * 0.6 * this.ySpeedMod, 0.0));
                  }

                  this.entity.setSpeed(lvt_10_1_ * 0.02F);
                  float lvt_11_1_ = -((float)(Mth.atan2(lvt_3_1_, Mth.sqrt((float)(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_))) * 57.2957763671875));
                  lvt_11_1_ = Mth.clamp(Mth.wrapDegrees(lvt_11_1_), -85.0F, 85.0F);
                  this.entity.setXRot(this.rotlerp(this.entity.getXRot(), lvt_11_1_, 5.0F));
                  float lvt_12_1_ = Mth.cos(this.entity.getXRot() * 0.017453292F);
                  float lvt_13_1_ = Mth.sin(this.entity.getXRot() * 0.017453292F);
                  this.entity.zza = lvt_12_1_ * lvt_10_1_;
                  this.entity.yya = -lvt_13_1_ * lvt_10_1_;
               } else {
                  this.entity.setSpeed(lvt_10_1_ * 0.1F);
               }
            }
         } else {
            if (this.entity instanceof EntityMimicOctopus && !this.entity.onGround()) {
               this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0, -0.02, 0.0));
            }

            this.entity.setSpeed(0.0F);
            this.entity.setXxa(0.0F);
            this.entity.setYya(0.0F);
            this.entity.setZza(0.0F);
         }
      }
   }
}
