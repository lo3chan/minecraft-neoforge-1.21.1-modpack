package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.SnowStormKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.SnowStormPriNalozhieniiEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SnowStormMobEffect extends MobEffect {
   public SnowStormMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -4198145);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      SnowStormPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      SnowStormKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
