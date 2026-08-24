package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.RabbitAgilityKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.RabbitAgilityPriNalozhieniiEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class RabbitAgilityMobEffect extends MobEffect {
   public RabbitAgilityMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -6110003);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.rabbit_agility_0"), 0.7, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.rabbit_agility_1"), -0.1, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      RabbitAgilityPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      RabbitAgilityKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
