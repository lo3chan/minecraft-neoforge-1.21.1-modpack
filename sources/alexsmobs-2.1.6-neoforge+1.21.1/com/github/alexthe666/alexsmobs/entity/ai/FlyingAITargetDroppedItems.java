package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;

public class FlyingAITargetDroppedItems extends CreatureAITargetItems {
   public FlyingAITargetDroppedItems(PathfinderMob creature, boolean checkSight, boolean onlyNearby, int tickThreshold, int radius) {
      super(creature, checkSight, onlyNearby, tickThreshold, radius);
      this.executionChance = 1;
   }

   @Override
   public void stop() {
      super.stop();
      this.hunter.setItemFlag(false);
   }

   @Override
   public boolean canUse() {
      return super.canUse() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
   }

   @Override
   public boolean canContinueToUse() {
      return super.canContinueToUse() && (this.mob.getTarget() == null || !this.mob.getTarget().isAlive());
   }

   @Override
   protected void moveTo() {
      if (this.targetEntity != null) {
         this.hunter.setItemFlag(true);
         if (this.mob.distanceTo(this.targetEntity) < 2.0F) {
            this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.5);
            this.hunter.peck();
         }

         if (!(this.mob.distanceTo(this.targetEntity) > 8.0F) && !this.hunter.isFlying()) {
            this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.5);
         } else {
            this.hunter.setFlying(true);
            float f = (float)(this.mob.getX() - this.targetEntity.getX());
            float f1 = 1.8F;
            float f2 = (float)(this.mob.getZ() - this.targetEntity.getZ());
            float xzDist = Mth.sqrt(f * f + f2 * f2);
            if (!this.mob.hasLineOfSight(this.targetEntity)) {
               this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), 1.0 + this.mob.getY(), this.targetEntity.getZ(), 1.5);
            } else {
               if (xzDist < 5.0F) {
                  f1 = 0.0F;
               }

               this.mob.getMoveControl().setWantedPosition(this.targetEntity.getX(), f1 + this.targetEntity.getY(), this.targetEntity.getZ(), 1.5);
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.moveTo();
   }
}
