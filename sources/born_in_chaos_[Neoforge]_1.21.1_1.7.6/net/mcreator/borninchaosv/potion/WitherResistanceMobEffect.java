package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.WitherResistanceKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WitherResistanceMobEffect extends MobEffect {
   public WitherResistanceMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -14803164);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      WitherResistanceKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
