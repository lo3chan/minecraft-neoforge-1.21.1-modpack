package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.IntoxicationKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.IntoxicationKoghdaEffiektNachatprimienienProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class IntoxicationMobEffect extends MobEffect {
   public IntoxicationMobEffect() {
      super(MobEffectCategory.HARMFUL, -8375942);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.intoxication_0"), -0.15, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.intoxication_1"), -0.15, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      IntoxicationKoghdaEffiektNachatprimienienProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      IntoxicationKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
      return super.applyEffectTick(entity, amplifier);
   }
}
