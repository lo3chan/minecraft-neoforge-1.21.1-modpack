package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.SoulStratificationKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class SoulStratificationMobEffect extends MobEffect {
   public SoulStratificationMobEffect() {
      super(MobEffectCategory.HARMFUL, -10251232);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED,
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.soul_stratification_0"),
         -0.3,
         Operation.ADD_MULTIPLIED_BASE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      SoulStratificationKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
