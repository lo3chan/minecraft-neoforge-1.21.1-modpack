package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

public class KomodoDragonAIJostle extends Goal {
   private static final TargetingConditions JOSTLE_PREDICATE = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight();
   protected EntityKomodoDragon targetKomodoDragon;
   private final EntityKomodoDragon komodo;
   private final Level world;
   private float angle;

   public KomodoDragonAIJostle(EntityKomodoDragon moose) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
      this.komodo = moose;
      this.world = moose.level();
   }

   public boolean canUse() {
      if (!this.komodo.isJostling()
         && !this.komodo.isInLove()
         && !this.komodo.isOrderedToSit()
         && !this.komodo.isVehicle()
         && !this.komodo.shouldFollow()
         && !this.komodo.isPassenger()
         && !this.komodo.isBaby()
         && this.komodo.getTarget() == null
         && this.komodo.jostleCooldown <= 0) {
         if (this.komodo.instantlyTriggerJostleAI || this.komodo.getRandom().nextInt(30) == 0) {
            this.komodo.instantlyTriggerJostleAI = false;
            if (this.komodo.getJostlingPartner() instanceof EntityKomodoDragon) {
               this.targetKomodoDragon = (EntityKomodoDragon)this.komodo.getJostlingPartner();
               return this.targetKomodoDragon.jostleCooldown == 0;
            }

            EntityKomodoDragon possiblePartner = this.getNearbyKomodoDragon();
            if (possiblePartner != null) {
               this.komodo.setJostlingPartner(possiblePartner);
               possiblePartner.setJostlingPartner(this.komodo);
               this.targetKomodoDragon = possiblePartner;
               this.targetKomodoDragon.instantlyTriggerJostleAI = true;
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void start() {
      this.komodo.jostleTimer = 0;
      this.angle = 0.0F;
      this.setJostleDirection(this.komodo.getRandom().nextBoolean());
   }

   public void setJostleDirection(boolean dir) {
      this.komodo.jostleDirection = dir;
      this.targetKomodoDragon.jostleDirection = dir;
   }

   public void stop() {
      this.komodo.setJostling(false);
      this.komodo.setJostlingPartner(null);
      this.komodo.jostleTimer = 0;
      this.angle = 0.0F;
      this.komodo.getNavigation().stop();
      if (this.targetKomodoDragon != null) {
         this.targetKomodoDragon.setJostling(false);
         this.targetKomodoDragon.setJostlingPartner(null);
         this.targetKomodoDragon.jostleTimer = 0;
         this.targetKomodoDragon = null;
      }
   }

   public void tick() {
      if (this.targetKomodoDragon != null) {
         this.komodo.lookAt(this.targetKomodoDragon, 360.0F, 180.0F);
         this.komodo.setJostling(true);
         float x = (float)(this.komodo.getX() - this.targetKomodoDragon.getX());
         float y = Math.abs((float)(this.komodo.getY() - this.targetKomodoDragon.getY()));
         float z = (float)(this.komodo.getZ() - this.targetKomodoDragon.getZ());
         double distXZ = Math.sqrt(x * x + z * z);
         if (distXZ < 1.7999999523162842) {
            this.komodo.getNavigation().stop();
            this.komodo.getMoveControl().strafe(-0.5F, 0.0F);
         } else if (distXZ > 2.4000000953674316) {
            this.komodo.setJostling(false);
            this.komodo.getNavigation().moveTo(this.targetKomodoDragon, 1.0);
         } else {
            this.komodo.lookAt(this.targetKomodoDragon, 360.0F, 180.0F);
            float f = this.komodo.getRandom().nextFloat() - 0.5F;
            if (this.komodo.jostleDirection) {
               if (this.angle < 10.0F) {
                  this.angle++;
               } else {
                  this.komodo.jostleDirection = false;
               }

               this.komodo.getMoveControl().strafe(f * 1.0F, -0.4F);
            }

            if (!this.komodo.jostleDirection) {
               if (this.angle > -10.0F) {
                  this.angle--;
               } else {
                  this.komodo.jostleDirection = true;
               }

               this.komodo.getMoveControl().strafe(f * 1.0F, 0.4F);
            }

            if (this.komodo.getRandom().nextInt(15) == 0 && this.komodo.onGround()) {
               this.komodo.pushBackJostling(this.targetKomodoDragon, 0.1F);
            }

            this.komodo.nextJostleAngleFromServer = this.angle;
            this.komodo.jostleTimer++;
            this.targetKomodoDragon.jostleTimer++;
            if (this.komodo.jostleTimer > 500 || y > 2.0F) {
               this.komodo.hasImpulse = true;
               if (this.komodo.onGround()) {
                  this.komodo.pushBackJostling(this.targetKomodoDragon, 0.4F);
               }

               if (this.targetKomodoDragon.onGround()) {
                  this.targetKomodoDragon.pushBackJostling(this.komodo, 0.4F);
               }

               this.komodo.jostleTimer = 0;
               this.targetKomodoDragon.jostleTimer = 0;
               this.komodo.jostleCooldown = 700 + this.komodo.getRandom().nextInt(2000);
               this.targetKomodoDragon.jostleTimer = 0;
               this.targetKomodoDragon.jostleCooldown = 700 + this.targetKomodoDragon.getRandom().nextInt(2000);
               this.stop();
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.komodo.isBaby()
         && !this.komodo.isInLove()
         && !this.komodo.isVehicle()
         && !this.komodo.isOrderedToSit()
         && this.komodo.getTarget() == null
         && this.targetKomodoDragon != null
         && this.targetKomodoDragon.isAlive()
         && this.komodo.jostleCooldown == 0
         && this.targetKomodoDragon.jostleCooldown == 0;
   }

   @Nullable
   private EntityKomodoDragon getNearbyKomodoDragon() {
      List<EntityKomodoDragon> komodoDragons = AMCompat.getNearbyEntities(
         this.world, EntityKomodoDragon.class, JOSTLE_PREDICATE, this.komodo, this.komodo.getBoundingBox().inflate(16.0)
      );
      double lvt_2_1_ = 1.7976931348623157E308;
      EntityKomodoDragon lvt_4_1_ = null;

      for (EntityKomodoDragon lvt_6_1_ : komodoDragons) {
         if (this.komodo.canJostleWith(lvt_6_1_) && this.komodo.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
            lvt_4_1_ = lvt_6_1_;
            lvt_2_1_ = this.komodo.distanceToSqr(lvt_6_1_);
         }
      }

      return lvt_4_1_;
   }
}
