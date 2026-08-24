package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMoose;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;

public class MooseAIJostle extends Goal {
   private static final TargetingConditions JOSTLE_PREDICATE = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight();
   protected EntityMoose targetMoose;
   private EntityMoose moose;
   private Level world;
   private float angle;

   public MooseAIJostle(EntityMoose moose) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
      this.moose = moose;
      this.world = moose.level();
   }

   public boolean canUse() {
      if (!this.moose.isJostling() && this.moose.isAntlered() && !this.moose.isBaby() && this.moose.getTarget() == null && this.moose.jostleCooldown <= 0) {
         if (this.moose.instantlyTriggerJostleAI || this.moose.getRandom().nextInt(30) == 0) {
            this.moose.instantlyTriggerJostleAI = false;
            if (this.moose.getJostlingPartner() instanceof EntityMoose) {
               this.targetMoose = (EntityMoose)this.moose.getJostlingPartner();
               return this.targetMoose.jostleCooldown == 0;
            }

            EntityMoose possiblePartner = this.getNearbyMoose();
            if (possiblePartner != null) {
               this.moose.setJostlingPartner(possiblePartner);
               possiblePartner.setJostlingPartner(this.moose);
               this.targetMoose = possiblePartner;
               this.targetMoose.instantlyTriggerJostleAI = true;
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void start() {
      this.moose.jostleTimer = 0;
      this.angle = 0.0F;
      this.setJostleDirection(this.moose.getRandom().nextBoolean());
   }

   public void setJostleDirection(boolean dir) {
      this.moose.jostleDirection = dir;
      this.targetMoose.jostleDirection = dir;
   }

   public void stop() {
      this.moose.setJostling(false);
      this.moose.setJostlingPartner(null);
      this.moose.jostleTimer = 0;
      this.angle = 0.0F;
      this.moose.getNavigation().stop();
      if (this.targetMoose != null) {
         this.targetMoose.setJostling(false);
         this.targetMoose.setJostlingPartner(null);
         this.targetMoose.jostleTimer = 0;
         this.targetMoose = null;
      }
   }

   public void tick() {
      if (this.targetMoose != null) {
         this.moose.lookAt(this.targetMoose, 360.0F, 180.0F);
         this.moose.setJostling(true);
         float f = (float)(this.moose.getX() - this.targetMoose.getX());
         float f1 = Math.abs((float)(this.moose.getY() - this.targetMoose.getY()));
         float f2 = (float)(this.moose.getZ() - this.targetMoose.getZ());
         double distXZ = Math.sqrt(f * f + f2 * f2);
         if (distXZ < 4.0) {
            this.moose.getNavigation().stop();
            this.moose.getMoveControl().strafe(-0.5F, 0.0F);
         } else if (distXZ > 4.5) {
            this.moose.setJostling(false);
            this.moose.getNavigation().moveTo(this.targetMoose, 1.0);
         } else {
            this.moose.lookAt(this.targetMoose, 360.0F, 180.0F);
            if (this.moose.jostleDirection) {
               if (this.angle < 30.0F) {
                  this.angle++;
               }

               this.moose.getMoveControl().strafe(0.0F, -0.2F);
            }

            if (!this.moose.jostleDirection) {
               if (this.angle > -30.0F) {
                  this.angle--;
               }

               this.moose.getMoveControl().strafe(0.0F, 0.2F);
            }

            if (this.moose.getRandom().nextInt(55) == 0 && this.moose.onGround()) {
               this.moose.pushBackJostling(this.targetMoose, 0.2F);
            }

            if (this.moose.getRandom().nextInt(25) == 0 && this.moose.onGround()) {
               this.moose.playJostleSound();
            }

            this.moose.setJostleAngle(this.angle);
            if (this.moose.jostleTimer % 60 == 0 || this.moose.getRandom().nextInt(80) == 0) {
               this.setJostleDirection(!this.moose.jostleDirection);
            }

            this.moose.jostleTimer++;
            this.targetMoose.jostleTimer++;
            if (this.moose.jostleTimer > 1000 || f1 > 2.0F) {
               this.moose.hasImpulse = true;
               if (this.moose.onGround()) {
                  this.moose.pushBackJostling(this.targetMoose, 0.9F);
               }

               if (this.targetMoose.onGround()) {
                  this.targetMoose.pushBackJostling(this.moose, 0.9F);
               }

               this.moose.jostleTimer = 0;
               this.targetMoose.jostleTimer = 0;
               this.moose.jostleCooldown = 500 + this.moose.getRandom().nextInt(2000);
               this.targetMoose.jostleTimer = 0;
               this.targetMoose.jostleCooldown = 500 + this.targetMoose.getRandom().nextInt(2000);
               this.stop();
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.moose.isBaby()
         && this.moose.isAntlered()
         && this.moose.getTarget() == null
         && this.targetMoose != null
         && this.targetMoose.isAntlered()
         && this.targetMoose.isAlive()
         && this.moose.jostleCooldown == 0
         && this.targetMoose.jostleCooldown == 0;
   }

   @Nullable
   private EntityMoose getNearbyMoose() {
      List<EntityMoose> listOfMeese = AMCompat.getNearbyEntities(
         this.world, EntityMoose.class, JOSTLE_PREDICATE, this.moose, this.moose.getBoundingBox().inflate(16.0)
      );
      double lvt_2_1_ = 1.7976931348623157E308;
      EntityMoose lvt_4_1_ = null;

      for (EntityMoose lvt_6_1_ : listOfMeese) {
         if (this.moose.canJostleWith(lvt_6_1_) && this.moose.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
            lvt_4_1_ = lvt_6_1_;
            lvt_2_1_ = this.moose.distanceToSqr(lvt_6_1_);
         }
      }

      return lvt_4_1_;
   }
}
