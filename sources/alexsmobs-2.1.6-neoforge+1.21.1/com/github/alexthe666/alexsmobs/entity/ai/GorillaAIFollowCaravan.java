package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.EntityGorilla;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class GorillaAIFollowCaravan extends Goal {
   public final EntityGorilla gorilla;
   private double speedModifier;
   private int distCheckCounter;

   public GorillaAIFollowCaravan(EntityGorilla llamaIn, double speedModifierIn) {
      this.gorilla = llamaIn;
      this.speedModifier = speedModifierIn;
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.gorilla.isSilverback() && !this.gorilla.inCaravan() && !this.gorilla.isSitting()) {
         double dist = 15.0;
         List<EntityGorilla> list = this.gorilla.level().getEntitiesOfClass(EntityGorilla.class, this.gorilla.getBoundingBox().inflate(dist, dist / 2.0, dist));
         EntityGorilla gorilla = null;
         double d0 = 1.7976931348623157E308;

         for (Entity entity : list) {
            EntityGorilla gorilla1 = (EntityGorilla)entity;
            if (gorilla1.inCaravan() && !gorilla1.hasCaravanTrail()) {
               double d1 = this.gorilla.distanceToSqr(gorilla1);
               if (!(d1 > d0)) {
                  d0 = d1;
                  gorilla = gorilla1;
               }
            }
         }

         if (gorilla == null) {
            for (Entity entity1 : list) {
               EntityGorilla llamaentity2 = (EntityGorilla)entity1;
               if (llamaentity2.isSilverback() && !llamaentity2.hasCaravanTrail()) {
                  double d2 = this.gorilla.distanceToSqr(llamaentity2);
                  if (!(d2 > d0)) {
                     d0 = d2;
                     gorilla = llamaentity2;
                  }
               }
            }
         }

         if (gorilla == null) {
            return false;
         } else if (d0 < 2.0) {
            return false;
         } else if (!gorilla.isSilverback() && !this.firstIsSilverback(gorilla, 1)) {
            return false;
         } else {
            this.gorilla.joinCaravan(gorilla);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      if (this.gorilla.isSitting()) {
         return false;
      } else if (this.gorilla.inCaravan() && this.gorilla.getCaravanHead().isAlive() && this.firstIsSilverback(this.gorilla, 0)) {
         double d0 = this.gorilla.distanceToSqr(this.gorilla.getCaravanHead());
         if (d0 > 676.0) {
            if (this.speedModifier <= 1.5) {
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
      this.gorilla.leaveCaravan();
      this.speedModifier = 1.5;
   }

   public void tick() {
      if (this.gorilla.inCaravan() && !this.gorilla.isSitting()) {
         EntityGorilla llamaentity = this.gorilla.getCaravanHead();
         if (llamaentity != null) {
            double d0 = this.gorilla.distanceTo(llamaentity);
            Vec3 vector3d = new Vec3(
                  llamaentity.getX() - this.gorilla.getX(), llamaentity.getY() - this.gorilla.getY(), llamaentity.getZ() - this.gorilla.getZ()
               )
               .normalize()
               .scale(Math.max(d0 - 2.0, 0.0));
            if (this.gorilla.getNavigation().isDone()) {
               try {
                  this.gorilla
                     .getNavigation()
                     .moveTo(this.gorilla.getX() + vector3d.x, this.gorilla.getY() + vector3d.y, this.gorilla.getZ() + vector3d.z, this.speedModifier);
               } catch (NullPointerException var6) {
                  AlexsMobs.LOGGER.warn("gorilla encountered issue following caravan head");
               }
            }
         }
      }
   }

   private boolean firstIsSilverback(EntityGorilla llama, int p_190858_2_) {
      if (p_190858_2_ > 8) {
         return false;
      } else if (llama.inCaravan()) {
         if (llama.getCaravanHead().isSilverback()) {
            return true;
         } else {
            EntityGorilla llamaentity = llama.getCaravanHead();
            return this.firstIsSilverback(llamaentity, ++p_190858_2_);
         }
      } else {
         return false;
      }
   }
}
