package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlightMoveController extends MoveControl {
   private final Mob parentEntity;
   private final float speedGeneral;
   private final boolean shouldLookAtTarget;
   private final boolean needsYSupport;

   public FlightMoveController(Mob bird, float speedGeneral, boolean shouldLookAtTarget, boolean needsYSupport) {
      super(bird);
      this.parentEntity = bird;
      this.shouldLookAtTarget = shouldLookAtTarget;
      this.speedGeneral = speedGeneral;
      this.needsYSupport = needsYSupport;
   }

   public FlightMoveController(Mob bird, float speedGeneral, boolean shouldLookAtTarget) {
      this(bird, speedGeneral, shouldLookAtTarget, false);
   }

   public FlightMoveController(Mob bird, float speedGeneral) {
      this(bird, speedGeneral, true);
   }

   public void tick() {
      if (this.operation == Operation.MOVE_TO) {
         Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
         double d0 = vector3d.length();
         if (d0 < this.parentEntity.getBoundingBox().getSize()) {
            this.operation = Operation.WAIT;
            this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5));
         } else {
            this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * this.speedGeneral * 0.05 / d0)));
            if (this.needsYSupport) {
               double d1 = this.wantedY - this.parentEntity.getY();
               this.parentEntity
                  .setDeltaMovement(
                     this.parentEntity
                        .getDeltaMovement()
                        .add(0.0, (double)this.parentEntity.getSpeed() * this.speedGeneral * Mth.clamp(d1, -1.0, 1.0) * 0.6000000238418579, 0.0)
                  );
            }

            if (this.parentEntity.getTarget() != null && this.shouldLookAtTarget) {
               double d2 = this.parentEntity.getTarget().getX() - this.parentEntity.getX();
               double d1 = this.parentEntity.getTarget().getZ() - this.parentEntity.getZ();
               this.parentEntity.setYRot(-((float)Mth.atan2(d2, d1)) * 57.295776F);
               this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            } else {
               Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
               this.parentEntity.setYRot(-((float)Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
               this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            }
         }
      } else if (this.operation == Operation.STRAFE) {
         this.operation = Operation.WAIT;
      }
   }

   private boolean canReach(Vec3 p_220673_1_, int p_220673_2_) {
      AABB axisalignedbb = this.parentEntity.getBoundingBox();

      for (int i = 1; i < p_220673_2_; i++) {
         axisalignedbb = axisalignedbb.move(p_220673_1_);
         if (!this.parentEntity.level().noCollision(this.parentEntity, axisalignedbb)) {
            return false;
         }
      }

      return true;
   }
}
