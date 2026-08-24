package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityMudskipper;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MudskipperAIDisplay extends Goal {
   private static final TargetingConditions JOSTLE_PREDICATE = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight();
   protected EntityMudskipper partner;
   private EntityMudskipper mudskipper;
   private Level world;
   private float angle;
   private Vec3 center = null;

   public MudskipperAIDisplay(EntityMudskipper mudskipper) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
      this.mudskipper = mudskipper;
      this.world = mudskipper.level();
   }

   public boolean canUse() {
      if (!this.mudskipper.isDisplaying()
         && !this.mudskipper.shouldFollow()
         && !this.mudskipper.isOrderedToSit()
         && !this.mudskipper.isInLove()
         && !this.mudskipper.isVehicle()
         && !this.mudskipper.isPassenger()
         && !this.mudskipper.isBaby()
         && this.mudskipper.getTarget() == null
         && !this.mudskipper.onGround()
         && this.mudskipper.displayCooldown <= 0) {
         if (this.mudskipper.instantlyTriggerDisplayAI || this.mudskipper.getRandom().nextInt(30) == 0) {
            this.mudskipper.instantlyTriggerDisplayAI = false;
            if (this.mudskipper.getDisplayingPartner() instanceof EntityMudskipper) {
               this.partner = (EntityMudskipper)this.mudskipper.getDisplayingPartner();
               return this.partner.displayCooldown == 0;
            }

            EntityMudskipper possiblePartner = this.getNearbyMudskipper();
            if (possiblePartner != null) {
               this.mudskipper.setDisplayingPartner(possiblePartner);
               possiblePartner.setDisplayingPartner(this.mudskipper);
               this.partner = possiblePartner;
               this.partner.instantlyTriggerDisplayAI = true;
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void start() {
      this.mudskipper.displayTimer = 0;
      this.angle = 0.0F;
      this.setDisplayDirection(this.mudskipper.getRandom().nextBoolean());
   }

   public void setDisplayDirection(boolean dir) {
      this.mudskipper.displayDirection = dir;
      this.partner.displayDirection = !dir;
   }

   public void stop() {
      this.center = null;
      this.mudskipper.setDisplaying(false);
      this.mudskipper.setDisplayingPartner(null);
      this.mudskipper.displayTimer = 0;
      this.angle = 0.0F;
      this.mudskipper.getNavigation().stop();
      if (this.partner != null) {
         this.partner.setDisplaying(false);
         this.partner.setDisplayingPartner(null);
         this.partner.displayTimer = 0;
         this.partner = null;
      }
   }

   public void tick() {
      if (this.partner != null) {
         if (this.center == null || this.mudskipper.getRandom().nextInt(100) == 0) {
            this.center = new Vec3(
               (this.mudskipper.getX() + this.partner.getX()) / 2.0,
               (this.mudskipper.getY() + this.partner.getY()) / 2.0,
               (this.mudskipper.getZ() + this.partner.getZ()) / 2.0
            );
         }

         this.mudskipper.setDisplaying(true);
         float x = (float)(this.mudskipper.getX() - this.partner.getX());
         float y = Math.abs((float)(this.mudskipper.getY() - this.partner.getY()));
         float z = (float)(this.mudskipper.getZ() - this.partner.getZ());
         double distXZ = Math.sqrt(x * x + z * z);
         if (distXZ > 3.0) {
            this.mudskipper.getNavigation().moveTo(this.partner, 1.0);
         } else {
            float speed = this.mudskipper.getRandom().nextFloat() * 0.5F + 0.8F;
            if (this.mudskipper.displayDirection) {
               if (this.angle < 180.0F) {
                  this.angle += 10.0F;
               } else {
                  this.mudskipper.displayDirection = false;
               }
            }

            if (!this.mudskipper.displayDirection) {
               if (this.angle > -180.0F) {
                  this.angle -= 10.0F;
               } else {
                  this.mudskipper.displayDirection = true;
               }
            }

            if (distXZ < 0.800000011920929 && this.mudskipper.onGround() && this.partner.onGround()) {
               this.mudskipper.lookAt(this.partner, 360.0F, 360.0F);
               this.setDisplayDirection(!this.mudskipper.displayDirection);
               this.mudskipper.openMouth(10 + this.mudskipper.getRandom().nextInt(20));
               this.partner
                  .setDeltaMovement(
                     this.partner
                        .getDeltaMovement()
                        .add(0.2F * this.mudskipper.getRandom().nextFloat(), 0.3499999940395355, 0.2F * this.mudskipper.getRandom().nextFloat())
                  );
            }

            Vec3 circle = this.getCirclingPosOf(this.center, 1.5F + this.mudskipper.getRandom().nextFloat());
            Vec3 dirVec = circle.subtract(this.mudskipper.position());
            float headAngle = -((float)(Mth.atan2(dirVec.x, dirVec.z) * 57.2957763671875));
            this.mudskipper.getNavigation().moveTo(circle.x, circle.y, circle.z, speed);
            this.mudskipper.setYRot(headAngle);
            this.mudskipper.yHeadRot = headAngle;
            this.mudskipper.yBodyRot = headAngle;
            this.mudskipper.nextDisplayAngleFromServer = this.angle;
            this.mudskipper.displayTimer++;
            this.partner.displayTimer++;
            if (this.mudskipper.displayTimer > 400 || y > 2.0F) {
               this.mudskipper.getNavigation().stop();
               this.partner.getNavigation().stop();
               this.mudskipper.hasImpulse = true;
               this.mudskipper.displayTimer = 0;
               this.partner.displayTimer = 0;
               this.mudskipper.displayCooldown = 200 + this.mudskipper.getRandom().nextInt(200);
               this.partner.displayTimer = 0;
               this.partner.displayCooldown = 200 + this.partner.getRandom().nextInt(200);
               this.stop();
            }
         }
      }
   }

   public Vec3 getCirclingPosOf(Vec3 center, double circleDistance) {
      float cir = 0.017453292F * this.angle;
      double extraX = circleDistance * Mth.sin(cir);
      double extraZ = circleDistance * Mth.cos(cir);
      return center.add(extraX, 0.0, extraZ);
   }

   public boolean canContinueToUse() {
      return !this.mudskipper.isBaby()
         && !this.mudskipper.shouldFollow()
         && !this.mudskipper.isOrderedToSit()
         && !this.mudskipper.isInLove()
         && !this.mudskipper.isVehicle()
         && this.mudskipper.getTarget() == null
         && this.partner != null
         && this.partner.isAlive()
         && this.mudskipper.displayCooldown == 0
         && this.partner.displayCooldown == 0;
   }

   @Nullable
   private EntityMudskipper getNearbyMudskipper() {
      List<EntityMudskipper> skippers = AMCompat.getNearbyEntities(
         this.world, EntityMudskipper.class, JOSTLE_PREDICATE, this.mudskipper, this.mudskipper.getBoundingBox().inflate(16.0)
      );
      double lvt_2_1_ = 1.7976931348623157E308;
      EntityMudskipper lvt_4_1_ = null;

      for (EntityMudskipper lvt_6_1_ : skippers) {
         if (this.mudskipper.canDisplayWith(lvt_6_1_) && this.mudskipper.distanceToSqr(lvt_6_1_) < lvt_2_1_) {
            lvt_4_1_ = lvt_6_1_;
            lvt_2_1_ = this.mudskipper.distanceToSqr(lvt_6_1_);
         }
      }

      return lvt_4_1_;
   }
}
