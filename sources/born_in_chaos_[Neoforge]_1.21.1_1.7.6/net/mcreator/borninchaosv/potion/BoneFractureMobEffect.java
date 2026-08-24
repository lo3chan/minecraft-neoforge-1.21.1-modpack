package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.BoneFractureKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.NeoForgeMod;

public class BoneFractureMobEffect extends MobEffect {
   public BoneFractureMobEffect() {
      super(MobEffectCategory.HARMFUL, -9542813);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.bone_fracture_0"), -0.45, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         NeoForgeMod.SWIM_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.bone_fracture_1"), -0.35, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.ATTACK_KNOCKBACK, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.bone_fracture_2"), -0.45, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.bone_fracture_3"), -0.15, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      BoneFractureKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity);
      return super.applyEffectTick(entity, amplifier);
   }
}
