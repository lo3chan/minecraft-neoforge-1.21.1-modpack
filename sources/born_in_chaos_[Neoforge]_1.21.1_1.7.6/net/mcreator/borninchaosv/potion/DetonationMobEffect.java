package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.DetonationKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DetonationMobEffect extends MobEffect {
   public DetonationMobEffect() {
      super(MobEffectCategory.HARMFUL, -11448498);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      DetonationKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
