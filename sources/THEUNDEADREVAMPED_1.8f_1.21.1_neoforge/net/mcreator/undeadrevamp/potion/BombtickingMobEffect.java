package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.BombtickingOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BombtickingMobEffect extends MobEffect {
   public BombtickingMobEffect() {
      super(MobEffectCategory.NEUTRAL, -6750157);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      BombtickingOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
