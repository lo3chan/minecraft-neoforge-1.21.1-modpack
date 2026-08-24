package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.BoneChillingKazhdyiTikVoVriemiaEffiektaProcedure;
import net.mcreator.borninchaosv.procedures.BoneChillingPriNalozhieniiEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BoneChillingMobEffect extends MobEffect {
   public BoneChillingMobEffect() {
      super(MobEffectCategory.HARMFUL, -3611673);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.bone_chilling_0"), -0.1, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      BoneChillingPriNalozhieniiEffiektaProcedure.execute(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      BoneChillingKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
