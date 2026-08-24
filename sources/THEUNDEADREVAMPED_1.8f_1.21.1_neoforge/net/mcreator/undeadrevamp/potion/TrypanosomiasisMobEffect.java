package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.TrypanosomiasisEffectStartedappliedProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TrypanosomiasisMobEffect extends MobEffect {
   public TrypanosomiasisMobEffect() {
      super(MobEffectCategory.HARMFUL, -14857398);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      TrypanosomiasisEffectStartedappliedProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      TrypanosomiasisEffectStartedappliedProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
