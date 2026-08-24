package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.phys.Vec3;

public class EtherealMoveController extends MoveControl {
   private final Mob parentEntity;
   private final float speedGeneral;

   public EtherealMoveController(Mob parentEntity, float speedGeneral) {
      super(parentEntity);
      this.parentEntity = parentEntity;
      this.speedGeneral = speedGeneral;
   }

   public void tick() {
      if (this.operation == Operation.MOVE_TO) {
         Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
         double d0 = vector3d.length();
         this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * this.speedGeneral * 0.025 / d0)));
         double yAdd = this.wantedY - this.parentEntity.getY();
         if (d0 > this.parentEntity.getBbWidth()) {
            this.parentEntity
               .setDeltaMovement(
                  this.parentEntity
                     .getDeltaMovement()
                     .add(0.0, (double)this.parentEntity.getSpeed() * this.speedGeneral * Mth.clamp(yAdd, -1.0, 1.0) * 0.6000000238418579, 0.0)
               );
            Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
            this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
            this.parentEntity.yBodyRot = this.parentEntity.getYRot();
         }
      } else if (this.operation == Operation.STRAFE || this.operation == Operation.JUMPING) {
         this.operation = Operation.WAIT;
      }
   }
}
