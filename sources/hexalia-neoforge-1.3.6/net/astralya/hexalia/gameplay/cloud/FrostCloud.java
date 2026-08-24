package net.astralya.hexalia.gameplay.cloud;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FrostCloud extends AreaEffectCloud {
   private static final int FREEZE_INCREMENT_PER_TICK = 5;
   private static final int HOLD_SECONDS = 2;
   private final SacCloudHelper.HoldShrinkPlan holdPlan;
   private final boolean[] shrinkStarted = new boolean[]{false};
   private int ageTicks = 0;

   public FrostCloud(Level level, double x, double y, double z, int durationSeconds) {
      super(level, x, y, z);
      this.holdPlan = SacCloudHelper.configureWithHold(this, durationSeconds, 2, 3.0F, 10476031);
      this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, true));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.ageTicks++;
         SacCloudHelper.startShrinkIfReady(this, this.ageTicks, this.holdPlan, this.shrinkStarted);
         SacCloudHelper.forEachLivingInRadius(this, this::applyFreeze);
      }
   }

   private void applyFreeze(LivingEntity target) {
      int required = target.getTicksRequiredToFreeze();
      int current = target.getTicksFrozen();
      if (current < required && target.canFreeze()) {
         target.setTicksFrozen(Math.min(required, current + 5));
      }
   }

   public void setCloudOwner(LivingEntity owner) {
      this.setOwner(owner);
   }
}
