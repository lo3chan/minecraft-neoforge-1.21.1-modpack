package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.entity.EntityGeladaMonkey;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class GeladaAIGroom extends Goal {
   private final EntityGeladaMonkey monkey;
   private int groomTime = 0;
   private int groomCooldown = 220;
   private EntityGeladaMonkey beingGroomed;

   public GeladaAIGroom(EntityGeladaMonkey monkey) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.monkey = monkey;
   }

   public boolean canUse() {
      if (this.groomCooldown > 0) {
         this.groomCooldown--;
         return false;
      } else {
         this.groomCooldown = 200 + this.monkey.getRandom().nextInt(1000);
         EntityGeladaMonkey nearestMonkey = null;

         for (EntityGeladaMonkey entity : this.monkey.level().getEntitiesOfClass(EntityGeladaMonkey.class, this.monkey.getBoundingBox().inflate(15.0))) {
            if (entity.getId() != this.monkey.getId()
               && this.monkey.canBeGroomed()
               && (nearestMonkey == null || this.monkey.distanceTo(nearestMonkey) > this.monkey.distanceTo(entity))) {
               nearestMonkey = entity;
            }
         }

         this.beingGroomed = nearestMonkey;
         return this.beingGroomed != null;
      }
   }

   public boolean canContinueToUse() {
      return this.beingGroomed != null
         && this.beingGroomed.isAlive()
         && !this.beingGroomed.shouldStopBeingGroomed()
         && this.groomTime < 200
         && (this.beingGroomed.groomerID == -1 || this.beingGroomed.groomerID == this.monkey.getId());
   }

   public void stop() {
      this.groomTime = 0;
      this.monkey.isGrooming = false;
      if (this.beingGroomed != null) {
         this.beingGroomed.groomerID = -1;
      }

      this.beingGroomed = null;
   }

   public void tick() {
      double dist = this.monkey.distanceTo(this.beingGroomed);
      if (dist < this.monkey.getBbWidth() + 0.5F) {
         this.monkey.isGrooming = true;
         this.beingGroomed.groomerID = this.monkey.getId();
         this.monkey.setSitting(true);
         this.groomTime++;
         if (this.groomTime % 50 == 0) {
            this.monkey.heal(1.0F);
         }

         if (this.monkey.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.monkey.setAnimation(EntityGeladaMonkey.ANIMATION_GROOM);
         }

         this.monkey.getNavigation().stop();
         this.monkey.lookAt(this.beingGroomed, 360.0F, 360.0F);
      } else {
         this.monkey.isGrooming = false;
         this.beingGroomed.groomerID = -1;
         this.monkey.setSitting(false);
         this.monkey.getNavigation().moveTo(this.beingGroomed, 1.0);
      }
   }
}
