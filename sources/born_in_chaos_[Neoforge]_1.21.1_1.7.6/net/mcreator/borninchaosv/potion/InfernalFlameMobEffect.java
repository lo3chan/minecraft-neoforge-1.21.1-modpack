package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.InfernalFlameKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class InfernalFlameMobEffect extends MobEffect {
   public InfernalFlameMobEffect() {
      super(MobEffectCategory.HARMFUL, -9393633);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      InfernalFlameKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
