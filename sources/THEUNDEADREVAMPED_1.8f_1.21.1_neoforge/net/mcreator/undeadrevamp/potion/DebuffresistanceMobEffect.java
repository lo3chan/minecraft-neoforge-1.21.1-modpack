package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.DebuffresistanceOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DebuffresistanceMobEffect extends MobEffect {
   public DebuffresistanceMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -6750055);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      DebuffresistanceOnEffectActiveTickProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
