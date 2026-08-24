package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.MoonflowersscentOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class MoonflowersscentMobEffect extends MobEffect {
   public MoonflowersscentMobEffect() {
      super(MobEffectCategory.NEUTRAL, -15527149);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      MoonflowersscentOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
