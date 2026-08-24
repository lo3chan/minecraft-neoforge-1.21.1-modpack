package com.github.alexthe666.alexsmobs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EffectOiled extends MobEffect {
   public EffectOiled() {
      super(MobEffectCategory.BENEFICIAL, 16771228);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity.isInWaterRainOrBubble()) {
         if (!entity.isShiftKeyDown()) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.1, 0.0));
         } else {
            entity.fallDistance = 0.0F;
         }

         if (!entity.onGround()) {
            Vec3 vector3d = entity.getDeltaMovement();
            entity.setDeltaMovement(vector3d.multiply(1.0, 0.9, 1.0));
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.oiled";
   }
}
