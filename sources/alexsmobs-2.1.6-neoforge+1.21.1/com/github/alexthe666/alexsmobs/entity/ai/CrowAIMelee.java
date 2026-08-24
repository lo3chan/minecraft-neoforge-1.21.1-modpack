package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityCrow;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class CrowAIMelee extends Goal {
   private final EntityCrow crow;
   float circlingTime = 0.0F;
   float circleDistance = 1.0F;
   float yLevel = 2.0F;
   boolean clockwise = false;
   private int maxCircleTime;

   public CrowAIMelee(EntityCrow crow) {
      this.crow = crow;
   }

   public boolean canUse() {
      return this.crow.getTarget() != null && !this.crow.isSitting() && this.crow.getCommand() != 3;
   }

   public void start() {
      this.clockwise = this.crow.getRandom().nextBoolean();
      this.yLevel = this.crow.getRandom().nextInt(2);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.crow.getRandom().nextInt(100);
      this.circleDistance = 1.0F + this.crow.getRandom().nextFloat() * 3.0F;
   }

   public void stop() {
      this.clockwise = this.crow.getRandom().nextBoolean();
      this.yLevel = this.crow.getRandom().nextInt(2);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.crow.getRandom().nextInt(100);
      this.circleDistance = 1.0F + this.crow.getRandom().nextFloat() * 3.0F;
      if (this.crow.onGround()) {
         this.crow.setFlying(false);
      }
   }

   public void tick() {
      LivingEntity target = this.crow.getTarget();
      if (target != null) {
         if (this.circlingTime > this.maxCircleTime) {
            this.crow.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() / 2.0F, target.getZ(), 1.2999999523162842);
            if (this.crow.distanceTo(target) < 2.0F) {
               this.crow.peck();
               if (AMCompat.isUndead(target)) {
                  target.hurt(target.damageSources().generic(), 4.0F);
               } else {
                  target.hurt(target.damageSources().generic(), 1.0F);
               }

               this.stop();
            }
         } else {
            Vec3 circlePos = this.getVultureCirclePos(target.position());
            if (circlePos == null) {
               circlePos = target.position();
            }

            this.crow.setFlying(true);
            this.crow.getMoveControl().setWantedPosition(circlePos.x(), circlePos.y() + target.getEyeHeight() + 0.20000000298023224, circlePos.z(), 1.0);
         }
      }

      if (this.crow.isFlying()) {
         this.circlingTime++;
      }
   }

   public Vec3 getVultureCirclePos(Vec3 target) {
      float angle = 0.13962634F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = this.circleDistance * Mth.sin(angle);
      double extraZ = this.circleDistance * Mth.cos(angle);
      Vec3 pos = new Vec3(target.x() + extraX, target.y() + this.yLevel, target.z() + extraZ);
      return this.crow.level().isEmptyBlock(AMBlockPos.fromVec3(pos)) ? pos : null;
   }
}
