package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class ElephantAIFollowCaravan extends Goal {
   public final EntityElephant elephant;
   private double speedModifier;
   private int distCheckCounter;

   public ElephantAIFollowCaravan(EntityElephant llamaIn, double speedModifierIn) {
      this.elephant = llamaIn;
      this.speedModifier = speedModifierIn;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (this.elephant.aiItemFlag || this.elephant.getControllingPassenger() != null) {
         return false;
      } else if (!this.elephant.isTusked() && !this.elephant.inCaravan() && !this.elephant.isSitting()) {
         double dist = 32.0;
         List<EntityElephant> list = this.elephant
            .level()
            .getEntitiesOfClass(EntityElephant.class, this.elephant.getBoundingBox().inflate(dist, dist / 2.0, dist));
         EntityElephant elephant = null;
         double d0 = 1.7976931348623157E308;

         for (Entity entity : list) {
            EntityElephant elephant1 = (EntityElephant)entity;
            if (elephant1.inCaravan() && !elephant1.hasCaravanTrail()) {
               double d1 = this.elephant.distanceToSqr(elephant1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  elephant = elephant1;
               }
            }
         }

         if (elephant == null) {
            for (Entity entity1 : list) {
               EntityElephant llamaentity2 = (EntityElephant)entity1;
               if (llamaentity2.isTusked() && !llamaentity2.isBaby() && !llamaentity2.hasCaravanTrail()) {
                  double d2 = this.elephant.distanceToSqr(llamaentity2);
                  if (!(d2 > d0)) {
                     d0 = d2;
                     elephant = llamaentity2;
                  }
               }
            }
         }

         if (elephant == null) {
            return false;
         } else if (d0 < 5.0) {
            return false;
         } else if (!elephant.isTusked() && !elephant.isBaby() && !this.firstIsTusk(elephant, 1)) {
            return false;
         } else {
            this.elephant.joinCaravan(elephant);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (this.elephant.isSitting() || this.elephant.aiItemFlag) {
         return false;
      } else if (this.elephant.inCaravan() && this.elephant.getCaravanHead().isAlive() && this.firstIsTusk(this.elephant, 0)) {
         double d0 = this.elephant.distanceToSqr(this.elephant.getCaravanHead());
         if (d0 > 676.0) {
            if (this.speedModifier <= 1.0) {
               this.speedModifier *= 1.2;
               this.distCheckCounter = 40;
               return true;
            }

            if (this.distCheckCounter == 0) {
               return false;
            }
         }

         if (this.distCheckCounter > 0) {
            this.distCheckCounter--;
         }

         return true;
      } else {
         return false;
      }
   }

   public void stop() {
      this.elephant.leaveCaravan();
      this.speedModifier = 1.0;
   }

   public void tick() {
      if (this.elephant.inCaravan() && !this.elephant.isSitting()) {
         EntityElephant llamaentity = this.elephant.getCaravanHead();
         if (llamaentity != null) {
            double d0 = this.elephant.distanceTo(llamaentity);
            Vec3 vector3d = new Vec3(
                  llamaentity.getX() - this.elephant.getX(), llamaentity.getY() - this.elephant.getY(), llamaentity.getZ() - this.elephant.getZ()
               )
               .normalize()
               .scale(Math.max(d0 - 4.0, 0.0));
            if (this.elephant.getNavigation().isDone()) {
               try {
                  this.elephant
                     .getNavigation()
                     .moveTo(this.elephant.getX() + vector3d.x, this.elephant.getY() + vector3d.y, this.elephant.getZ() + vector3d.z, this.speedModifier);
               } catch (NullPointerException var6) {
                  AlexsMobs.LOGGER.warn("elephant encountered issue following caravan head");
               }
            }
         }
      }
   }

   private boolean firstIsTusk(EntityElephant llama, int p_190858_2_) {
      if (p_190858_2_ > 8) {
         return false;
      } else if (llama.inCaravan()) {
         if (llama.getCaravanHead().isTusked() && !llama.getCaravanHead().isBaby()) {
            return true;
         } else {
            EntityElephant llamaentity = llama.getCaravanHead();
            return this.firstIsTusk(llamaentity, ++p_190858_2_);
         }
      } else {
         return false;
      }
   }
}
