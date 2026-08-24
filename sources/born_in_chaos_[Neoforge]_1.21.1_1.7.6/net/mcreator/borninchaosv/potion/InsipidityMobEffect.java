package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.InsipidityKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class InsipidityMobEffect extends MobEffect {
   public InsipidityMobEffect() {
      super(MobEffectCategory.HARMFUL, -10344912);
      this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.myiasis_0"), -4.0, Operation.ADD_VALUE);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      InsipidityKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
