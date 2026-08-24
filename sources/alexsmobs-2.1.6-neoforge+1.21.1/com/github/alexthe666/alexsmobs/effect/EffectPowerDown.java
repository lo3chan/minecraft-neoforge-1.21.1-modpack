package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class EffectPowerDown extends MobEffect {
   private int lastDuration = -1;
   private int firstDuration = -1;

   protected EffectPowerDown() {
      super(MobEffectCategory.NEUTRAL, 0);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, AMCompat.attrModId("7107DE5E-7CE8-4030-940E-514C1F160890", "power_down_speed"), -1.0, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity.getDeltaMovement().y > 0.0 && !entity.isInWaterOrBubble()) {
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
      }

      if (this.firstDuration == this.lastDuration) {
         entity.playSound(AMSoundRegistry.APRIL_FOOLS_POWER_OUTAGE.get(), 1.5F, 1.0F);
         entity.gameEvent(AMPlatform.ENTITY_ACTION);
      }

      return true;
   }

   public int getActiveTime() {
      return this.firstDuration - this.lastDuration;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      this.lastDuration = duration;
      if (duration <= 0) {
         this.lastDuration = -1;
         this.firstDuration = -1;
      }

      if (this.firstDuration == -1) {
         this.firstDuration = duration;
      }

      return duration > 0;
   }

   public void removeAttributeModifiers(AttributeMap map) {
      this.lastDuration = -1;
      this.firstDuration = -1;
      super.removeAttributeModifiers(map);
   }

   public void addAttributeModifiers(AttributeMap map, int i) {
      this.lastDuration = -1;
      this.firstDuration = -1;
      super.addAttributeModifiers(map, i);
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.power_down";
   }
}
