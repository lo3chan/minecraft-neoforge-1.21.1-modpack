package com.github.alexthe666.alexsmobs.entity.ai;

import com.github.alexthe666.alexsmobs.entity.EntityElephant;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.AbstractVillager;

public class ElephantAIVillagerRide extends Goal {
   private final EntityElephant elephant;
   private AbstractVillager villager;
   private final double speed;

   public ElephantAIVillagerRide(EntityElephant dragon, double speed) {
      this.elephant = dragon;
      this.speed = speed;
   }

   public boolean canUse() {
      if (this.elephant.getControllingVillager() != null) {
         this.villager = this.elephant.getControllingVillager();
         return true;
      } else {
         return false;
      }
   }

   public void start() {
   }

   public void tick() {
      if (this.villager.getNavigation().isInProgress()) {
         this.elephant.getNavigation().moveTo(this.villager.getNavigation().getPath(), 1.6);
      }
   }
}
