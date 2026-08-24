package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectEnderFlu extends MobEffect {
   private int lastDuration = -1;

   public EffectEnderFlu() {
      super(MobEffectCategory.HARMFUL, 6829738);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (this.lastDuration == 1) {
         int phages = amplifier + 1;
         entity.hurt(entity.damageSources().magic(), phages * 10);

         for (int i = 0; i < phages; i++) {
            EntityEnderiophage phage = AMCompat.create(AMEntityRegistry.ENDERIOPHAGE.get(), entity.level());
            phage.copyPosition(entity);
            phage.onSpawnFromEffect();
            phage.setSkinForDimension();
            if (!entity.level().isClientSide()) {
               phage.setStandardFleeTime();
               entity.level().addFreshEntity(phage);
            }
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      this.lastDuration = duration;
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.ender_flu";
   }
}
