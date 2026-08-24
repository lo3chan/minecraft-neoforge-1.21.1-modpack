package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EffectFear extends MobEffect {
   protected EffectFear() {
      super(MobEffectCategory.NEUTRAL, 7632119);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, AMCompat.attrModId("7107DE5E-7CE8-4030-940E-514C1F160890", "fear_speed"), -1.0, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity.getDeltaMovement().y > 0.0 && !entity.isInWaterOrBubble()) {
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.fear";
   }
}
