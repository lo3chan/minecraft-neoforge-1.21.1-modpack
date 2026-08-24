package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.RottenSmellKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RottenSmellMobEffect extends MobEffect {
   public RottenSmellMobEffect() {
      super(MobEffectCategory.HARMFUL, -11376835);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      RottenSmellKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
