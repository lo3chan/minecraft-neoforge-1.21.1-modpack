package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EffectFleetFooted extends MobEffect {
   private static final ResourceLocation SPRINT_JUMP_SPEED_MODIFIER = AMCompat.rl("alexsmobs", "fleet_footed_speed");
   private static final AttributeModifier SPRINT_JUMP_SPEED_BONUS = AMCompat.attributeModifier(
      SPRINT_JUMP_SPEED_MODIFIER, "fleetfooted speed bonus", 0.20000000298023224, Operation.ADD_VALUE
   );
   private int lastDuration = -1;
   private int removeEffectAfter = 0;

   public EffectFleetFooted() {
      super(MobEffectCategory.BENEFICIAL, 6837313);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      AttributeInstance modifiableattributeinstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      boolean applyEffect = entity.isSprinting() && !entity.onGround() && this.lastDuration > 2;
      if (this.removeEffectAfter > 0) {
         this.removeEffectAfter--;
      }

      if (applyEffect) {
         if (!AMCompat.hasModifier(modifiableattributeinstance, SPRINT_JUMP_SPEED_MODIFIER)) {
            modifiableattributeinstance.addPermanentModifier(SPRINT_JUMP_SPEED_BONUS);
         }

         this.removeEffectAfter = 5;
      }

      if (this.removeEffectAfter <= 0 || this.lastDuration < 2) {
         modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_MODIFIER);
      }

      return true;
   }

   public void removeAttributeModifiers(AttributeMap attributeMap) {
      AttributeInstance modifiableattributeinstance = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
      this.amRemoveBonus(modifiableattributeinstance);
      super.removeAttributeModifiers(attributeMap);
   }

   private void amRemoveBonus(AttributeInstance modifiableattributeinstance) {
      if (modifiableattributeinstance != null && modifiableattributeinstance.getModifier(SPRINT_JUMP_SPEED_MODIFIER) != null) {
         modifiableattributeinstance.removeModifier(SPRINT_JUMP_SPEED_MODIFIER);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      this.lastDuration = duration;
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.fleet_footed";
   }
}
