package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.SleepwalkingEffectStartedappliedProcedure;
import net.mcreator.undeadrevamp.procedures.SleepwalkingOnEffectActiveTicknewProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SleepwalkingMobEffect extends MobEffect {
   public SleepwalkingMobEffect() {
      super(MobEffectCategory.HARMFUL, -16777216);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      SleepwalkingEffectStartedappliedProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      SleepwalkingOnEffectActiveTicknewProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
