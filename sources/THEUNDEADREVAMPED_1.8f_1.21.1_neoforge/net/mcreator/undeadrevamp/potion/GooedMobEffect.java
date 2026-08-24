package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.GooedOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GooedMobEffect extends MobEffect {
   public GooedMobEffect() {
      super(MobEffectCategory.NEUTRAL, -8157890);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      GooedOnEffectActiveTickProcedure.execute(entity.level(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
