package net.bobophones.bobolib.entity;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;

public abstract class DefaultMob extends PathfinderMob {
   public final AnimationState idle_state = new AnimationState();
   private int idle_timeout = 0;

   public DefaultMob(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
   }

   public boolean canBeLeashed() {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.SetupAnimStates();
      }
   }

   protected void SetupAnimStates() {
      if (this.idle_timeout <= 0) {
         this.idle_timeout = this.random.nextInt(40) + 80;
         this.idle_state.start(this.tickCount);
      } else {
         this.idle_timeout--;
      }
   }

   protected void updateWalkAnimation(float partial_tick) {
      float f;
      if (this.getPose() == Pose.STANDING) {
         f = Math.min(partial_tick * 6.0F, 1.0F);
      } else {
         f = 0.0F;
      }

      this.walkAnimation.update(f, 0.2F);
   }
}
