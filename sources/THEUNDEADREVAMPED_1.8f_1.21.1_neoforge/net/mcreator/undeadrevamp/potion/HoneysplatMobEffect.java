package net.mcreator.undeadrevamp.potion;

import net.mcreator.undeadrevamp.procedures.HoneysplatOnEffectActiveTickProcedure;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HoneysplatMobEffect extends MobEffect {
   public HoneysplatMobEffect() {
      super(MobEffectCategory.NEUTRAL, -4891128);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      HoneysplatOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
