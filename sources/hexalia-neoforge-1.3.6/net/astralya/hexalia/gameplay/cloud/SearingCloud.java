package net.astralya.hexalia.gameplay.cloud;

import net.astralya.hexalia.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SearingCloud extends AreaEffectCloud {
   private static final float DAMAGE_PER_SECOND = 0.5F;
   private static final int FIRE_SECONDS_PER_PULSE = 3;
   private static final int HOLD_SECONDS = 2;
   private final SacCloudHelper.HoldShrinkPlan holdPlan;
   private final boolean[] shrinkStarted = new boolean[]{false};
   private int ageTicks = 0;
   private int tickCounter = 0;

   public SearingCloud(Level level, double x, double y, double z, int durationSeconds) {
      super(level, x, y, z);
      this.holdPlan = SacCloudHelper.configureWithHold(this, durationSeconds, 2, 3.0F, 15227434);
      this.addEffect(new MobEffectInstance(ModMobEffects.BLEEDING, 200, 0, false, true));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.ageTicks++;
         SacCloudHelper.startShrinkIfReady(this, this.ageTicks, this.holdPlan, this.shrinkStarted);
         this.tickCounter++;
         if (this.tickCounter >= 20) {
            this.tickCounter = 0;
            this.pulse();
         }
      }
   }

   private void pulse() {
      SacCloudHelper.forEachLivingInRadius(this, target -> {
         SacCloudHelper.damageMagic(this, target, 0.5F);
         target.igniteForSeconds(3.0F);
      });
   }

   public void setCloudOwner(LivingEntity owner) {
      this.setOwner(owner);
   }
}
