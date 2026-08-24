package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.AntiflyinhOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AntiflyinhMobEffect extends MobEffect {
   public AntiflyinhMobEffect() {
      super(MobEffectCategory.HARMFUL, -1);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      AntiflyinhOnEffectActiveTickProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
