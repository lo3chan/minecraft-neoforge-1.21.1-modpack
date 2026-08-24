package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EffectKnockbackResistance extends MobEffect {
   public EffectKnockbackResistance() {
      super(MobEffectCategory.BENEFICIAL, 8803127);
      this.addAttributeModifier(
         Attributes.KNOCKBACK_RESISTANCE, AMCompat.attrModId("03C3C89D-7037-4B42-869F-B146BCB64D2F", "knockback_resistance"), 0.5, Operation.ADD_VALUE
      );
   }

   public boolean applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.knockback_resistance";
   }
}
