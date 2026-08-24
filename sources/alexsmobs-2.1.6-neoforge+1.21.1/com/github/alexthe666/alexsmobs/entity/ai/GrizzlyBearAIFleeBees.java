package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.google.common.base.Predicate;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class GrizzlyBearAIFleeBees extends Goal {
   private final double farSpeed;
   private final double nearSpeed;
   private final float avoidDistance;
   private final Predicate<Bee> avoidTargetSelector = new Predicate<Bee>() {
      public boolean apply(@Nullable Bee p_apply_1_) {
         return p_apply_1_.isAlive()
            && GrizzlyBearAIFleeBees.this.entity.getSensing().hasLineOfSight(p_apply_1_)
            && !GrizzlyBearAIFleeBees.this.entity.isAlliedTo(p_apply_1_)
            && p_apply_1_.getRemainingPersistentAngerTime() > 0;
      }
   };
   protected EntityGrizzlyBear entity;
   protected Bee closestLivingEntity;
   private Path path;

   public GrizzlyBearAIFleeBees(EntityGrizzlyBear entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this.entity = entityIn;
      this.avoidDistance = avoidDistanceIn;
      this.farSpeed = farSpeedIn;
      this.nearSpeed = nearSpeedIn;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (this.entity.isTame()) {
         return false;
      } else {
         if (this.entity.isSitting() && !this.entity.forcedSit) {
            this.entity.setOrderedToSit(false);
         }

         if (this.entity.isSitting()) {
            return false;
         } else {
            List<Bee> beeEntities = this.entity
               .level()
               .getEntitiesOfClass(Bee.class, this.entity.getBoundingBox().inflate(this.avoidDistance, 8.0, this.avoidDistance), this.avoidTargetSelector);
            if (beeEntities.isEmpty()) {
               return false;
            } else {
               this.closestLivingEntity = beeEntities.get(0);
               Vec3 vec3d = LandRandomPos.getPosAway(
                  this.entity, 16, 7, new Vec3(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ())
               );
               if (vec3d == null) {
                  return false;
               } else if (this.closestLivingEntity.distanceToSqr(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.distanceToSqr(this.entity)) {
                  return false;
               } else {
                  this.path = this.entity.getNavigation().createPath(AMBlockPos.fromCoords(vec3d.x, vec3d.y, vec3d.z), 0);
                  return this.path != null;
               }
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.entity.getNavigation().isDone();
   }

   public void start() {
      this.entity.getNavigation().moveTo(this.path, this.farSpeed);
   }

   public void stop() {
      this.entity.getNavigation().stop();
      this.closestLivingEntity = null;
   }

   public void tick() {
      if (this.closestLivingEntity != null && this.closestLivingEntity.getRemainingPersistentAngerTime() <= 0) {
         this.stop();
      }

      this.entity.getNavigation().setSpeedModifier(this.getRunSpeed());
   }

   public double getRunSpeed() {
      return 0.699999988079071;
   }
}
