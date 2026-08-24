package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectMosquitoRepellent extends MobEffect {
   public EffectMosquitoRepellent() {
      super(MobEffectCategory.BENEFICIAL, 13401712);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.mosquito_repellent";
   }
}
