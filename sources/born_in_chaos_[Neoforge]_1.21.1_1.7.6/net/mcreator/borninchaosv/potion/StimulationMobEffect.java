package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.StimulationKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.StimulationPriNalozhieniiEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StimulationMobEffect extends MobEffect {
   public StimulationMobEffect() {
      super(MobEffectCategory.BENEFICIAL, -3169230);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stimulation_0"), 0.25, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stimulation_1"), 0.25, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      StimulationPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      StimulationKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
