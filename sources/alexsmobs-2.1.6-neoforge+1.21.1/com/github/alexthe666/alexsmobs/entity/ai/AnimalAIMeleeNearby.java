package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.phys.Vec3;

public class AnimalAIMeleeNearby extends Goal {
   private final Mob entity;
   private final int range;
   private final double speed;
   private BlockPos fightStartPos = null;

   public AnimalAIMeleeNearby(Mob entity, int range, double speed) {
      this.setFlags(EnumSet.of(Flag.MOVE));
      this.entity = entity;
      this.range = range;
      this.speed = speed;
   }

   public boolean canUse() {
      return this.entity.getTarget() != null && this.entity.getTarget().isAlive() && !this.entity.isVehicle();
   }

   public void start() {
      this.fightStartPos = this.entity.getOnPos();
   }

   public void stop() {
      this.entity.getNavigation().stop();
      this.fightStartPos = null;
   }

   public void tick() {
      if (this.entity.distanceTo(this.entity.getTarget()) < 3.0F + this.entity.getBbWidth() + this.entity.getTarget().getBbWidth()) {
         AMCompat.doHurtTarget(this.entity, this.entity.getTarget());
         this.entity.lookAt(this.entity.getTarget(), 180.0F, 180.0F);
      } else if (this.fightStartPos != null) {
         if (this.entity.distanceToSqr(Vec3.atCenterOf(this.fightStartPos)) < this.range * this.range) {
            this.entity.getNavigation().moveTo(this.entity.getTarget(), this.speed);
         } else {
            this.entity
               .getNavigation()
               .moveTo(this.fightStartPos.getX() + 0.5F, this.fightStartPos.getY() + 0.5F, this.fightStartPos.getZ() + 0.5F, 0.4000000059604645 + this.speed);
         }
      }
   }
}
