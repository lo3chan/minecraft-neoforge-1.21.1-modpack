package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.LifestealKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.LifestealPriNalozhieniiEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class LifestealMobEffect extends MobEffect {
   public LifestealMobEffect() {
      super(MobEffectCategory.HARMFUL, -7726815);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      LifestealPriNalozhieniiEffiektaProcedure.execute(entity.level(), entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      LifestealKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
