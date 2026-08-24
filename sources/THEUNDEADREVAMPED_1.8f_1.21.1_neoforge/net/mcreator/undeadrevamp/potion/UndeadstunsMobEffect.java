package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.UndeadstunsOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class UndeadstunsMobEffect extends MobEffect {
   public UndeadstunsMobEffect() {
      super(MobEffectCategory.NEUTRAL, -1);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      UndeadstunsOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
