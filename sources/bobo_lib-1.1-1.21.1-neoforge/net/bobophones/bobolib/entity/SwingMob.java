package net.bobophones.bobolib.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class SwingMob extends DefaultMob {
   public SwingMob(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public void aiStep() {
      super.aiStep();
      this.updateSwingTime();
   }
}
