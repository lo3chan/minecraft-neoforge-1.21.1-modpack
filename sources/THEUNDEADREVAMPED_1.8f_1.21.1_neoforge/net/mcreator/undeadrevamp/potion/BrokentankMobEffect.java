package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.BrokentankOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BrokentankMobEffect extends MobEffect {
   public BrokentankMobEffect() {
      super(MobEffectCategory.NEUTRAL, -8795437);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      BrokentankOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
