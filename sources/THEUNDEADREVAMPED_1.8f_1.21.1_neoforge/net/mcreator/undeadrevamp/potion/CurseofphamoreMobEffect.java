package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.CurseofphamoreEffectStartedappliedProcedure;
import net.mcreator.undeadrevamp.procedures.CurseofphamoreOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CurseofphamoreMobEffect extends MobEffect {
   public CurseofphamoreMobEffect() {
      super(MobEffectCategory.HARMFUL, -12569847);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      CurseofphamoreEffectStartedappliedProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      CurseofphamoreOnEffectActiveTickProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
