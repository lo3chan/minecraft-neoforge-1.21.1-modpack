package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.PredatorsDesireKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.RabbitAgilityPriNalozhieniiEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class PredatorsDesireMobEffect extends MobEffect {
   public PredatorsDesireMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -6110003);
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.predators_desire_0"), 10.0, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.predators_desire_1"), 0.2, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      RabbitAgilityPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      PredatorsDesireKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
