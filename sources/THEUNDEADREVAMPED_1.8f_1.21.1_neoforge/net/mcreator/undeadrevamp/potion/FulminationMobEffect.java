package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.FulminationEffectStartedappliedProcedure;
import net.mcreator.undeadrevamp.procedures.FulminationOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FulminationMobEffect extends MobEffect {
   public FulminationMobEffect() {
      super(MobEffectCategory.HARMFUL, -2096385);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      FulminationEffectStartedappliedProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      FulminationOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
