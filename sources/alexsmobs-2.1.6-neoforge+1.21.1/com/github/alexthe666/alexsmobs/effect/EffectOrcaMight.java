package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EffectOrcaMight extends MobEffect {
   public EffectOrcaMight() {
      super(MobEffectCategory.BENEFICIAL, 4868690);
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, AMCompat.attrModId("03C3C89D-7037-4B42-869F-B146BCB64D3A", "orca_might_attack_speed"), 3.0, Operation.ADD_VALUE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.orcas_might";
   }
}
