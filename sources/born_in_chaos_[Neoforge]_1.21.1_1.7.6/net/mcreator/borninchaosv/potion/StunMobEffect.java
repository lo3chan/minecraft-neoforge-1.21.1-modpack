package net.mcreator.borninchaosv.potion;

import net.mcreator.borninchaosv.procedures.StunKazhdyiTikVoVriemiaEffiektaProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StunMobEffect extends MobEffect {
   public StunMobEffect() {
      super(MobEffectCategory.HARMFUL, -1319354);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stun_0"), -0.99, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stun_1"), -0.9, Operation.ADD_MULTIPLIED_BASE
      );
      this.addAttributeModifier(
         Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "effect.stun_2"), -0.65, Operation.ADD_MULTIPLIED_BASE
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      StunKazhdyiTikVoVriemiaEffiektaProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
      return super.applyEffectTick(entity, amplifier);
   }
}
