package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.AnimationtestOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AnimationtestMobEffect extends MobEffect {
   public AnimationtestMobEffect() {
      super(MobEffectCategory.NEUTRAL, -16724941);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      AnimationtestOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
