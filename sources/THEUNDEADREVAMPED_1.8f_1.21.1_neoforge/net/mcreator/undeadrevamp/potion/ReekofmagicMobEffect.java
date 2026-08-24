package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.ReekofmagicOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ReekofmagicMobEffect extends MobEffect {
   public ReekofmagicMobEffect() {
      super(MobEffectCategory.NEUTRAL, -16738048);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      ReekofmagicOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
