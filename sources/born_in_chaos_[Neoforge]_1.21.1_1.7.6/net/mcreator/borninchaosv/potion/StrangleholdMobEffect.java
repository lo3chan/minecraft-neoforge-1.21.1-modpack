package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.StrangleholdKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class StrangleholdMobEffect extends MobEffect {
   public StrangleholdMobEffect() {
      super(MobEffectCategory.HARMFUL, -13884120);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      StrangleholdKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
