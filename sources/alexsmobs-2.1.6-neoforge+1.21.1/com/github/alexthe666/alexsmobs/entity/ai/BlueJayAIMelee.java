package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityBlueJay;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class BlueJayAIMelee extends Goal {
   private final EntityBlueJay blueJay;
   float circlingTime = 0.0F;
   float circleDistance = 1.0F;
   float yLevel = 2.0F;
   boolean clockwise = false;
   private int maxCircleTime;

   public BlueJayAIMelee(EntityBlueJay blueJay) {
      this.blueJay = blueJay;
   }

   public boolean canUse() {
      Entity entity = this.blueJay.getTarget();
      return entity != null && entity.isAlive();
   }

   public void start() {
      this.clockwise = this.blueJay.getRandom().nextBoolean();
      this.yLevel = this.blueJay.getRandom().nextInt(2);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.blueJay.getRandom().nextInt(20);
      this.circleDistance = 0.5F + this.blueJay.getRandom().nextFloat() * 2.0F;
   }

   public void stop() {
      this.clockwise = this.blueJay.getRandom().nextBoolean();
      this.yLevel = this.blueJay.getRandom().nextInt(2);
      this.circlingTime = 0.0F;
      this.maxCircleTime = 20 + this.blueJay.getRandom().nextInt(20);
      this.circleDistance = 0.5F + this.blueJay.getRandom().nextFloat() * 2.0F;
      if (this.blueJay.onGround()) {
         this.blueJay.setFlying(false);
      }
   }

   public void tick() {
      if (this.blueJay.isFlying()) {
         this.circlingTime++;
      }

      LivingEntity target = this.blueJay.getTarget();
      if (target != null) {
         if (this.blueJay.distanceTo(target) < 3.0F) {
            this.blueJay.peck();
            target.hurt(target.damageSources().generic(), 1.0F);
            this.stop();
         }

         if (this.circlingTime > this.maxCircleTime) {
            this.blueJay.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() / 2.0F, target.getZ(), 1.600000023841858);
         } else {
            Vec3 circlePos = this.getVultureCirclePos(target.position());
            if (circlePos == null) {
               circlePos = target.position();
            }

            this.blueJay.setFlying(true);
            this.blueJay
               .getMoveControl()
               .setWantedPosition(circlePos.x(), circlePos.y() + target.getEyeHeight() + 0.20000000298023224, circlePos.z(), 1.600000023841858);
         }
      }
   }

   public Vec3 getVultureCirclePos(Vec3 target) {
      float angle = 0.2268928F * (this.clockwise ? -this.circlingTime : this.circlingTime);
      double extraX = this.circleDistance * Mth.sin(angle);
      double extraZ = this.circleDistance * Mth.cos(angle);
      Vec3 pos = new Vec3(target.x() + extraX, target.y() + this.yLevel, target.z() + extraZ);
      return this.blueJay.level().isEmptyBlock(AMBlockPos.fromVec3(pos)) ? pos : null;
   }
}
